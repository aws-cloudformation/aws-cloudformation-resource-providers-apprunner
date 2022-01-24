package software.amazon.apprunner.service;

import com.amazonaws.util.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.CreateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.CreateServiceResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.apprunner.model.ServiceStatus;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;

import static software.amazon.apprunner.service.Translator.translateServiceFromResponse;
import static software.amazon.apprunner.service.Translator.translateToCreateServiceRequest;

public class CreateHandler extends BaseHandlerStd {

    @VisibleForTesting
    static final int CALLBACK_DELAY_SECONDS = 10;

    private Logger logger;
    private ReadHandler readHandler;
    private TagHelper tagHelper;

    public CreateHandler() {
        readHandler = new ReadHandler();
        tagHelper = new TagHelper();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        ResourceModel model = request.getDesiredResourceState();
        final CallbackContext context = callbackContext == null ? new CallbackContext() : callbackContext;

        Service service;

        if (StringUtils.isNullOrEmpty(model.getServiceName())) {
            logger.log(String.format("Generating Service name..."));
            final String serviceName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    request.getClientRequestToken());
            model.setServiceName(serviceName);
        }

        // if isCreated in context is not set, trigger the service creation
        if (!context.isCreateStarted()) {
            if (!Strings.isNullOrEmpty(model.getServiceArn())) {
                DescribeServiceResponse describeServiceResponse = getService(model, proxyClient);

                if (describeServiceResponse.service() != null) {
                    throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, model.getServiceName());
                }
            }

            final CreateServiceRequest createServiceRequest = translateToCreateServiceRequest(model, tagHelper.generateTagsForCreate(model, request));
            CreateServiceResponse createServiceResponse;

            try {
                createServiceResponse = proxyClient.injectCredentialsAndInvokeV2(
                        createServiceRequest, proxyClient.client()::createService);
            } catch (final InvalidRequestException e) {
                logger.log(e.getMessage());
                throw new CfnInvalidRequestException(e);
            } catch (final ServiceQuotaExceededException e) {
                logger.log(e.getMessage());
                throw new  CfnServiceLimitExceededException(e);
            } catch (final InternalServiceErrorException e) {
                logger.log(e.getMessage());
                throw new CfnServiceInternalErrorException(e);
            } catch (SdkClientException e) {
                logger.log(e.getMessage());
                throw new CfnServiceInternalErrorException(ResourceModel.TYPE_NAME, e);
            }

            service = createServiceResponse.service();
            model = translateServiceFromResponse(createServiceResponse.service());
            context.setCreateStarted(true);
        } else {
            // if the service creation is already in progress, monitor its progress
            final DescribeServiceResponse describeServiceResponse = getService(model, proxyClient);
            service = describeServiceResponse.service();
        }
        model.setStatus(service.status().toString());
        final boolean isStabilized = service != null && service.status() != ServiceStatus.OPERATION_IN_PROGRESS;
        final boolean creationSuccessful = service != null && service.status().equals(ServiceStatus.RUNNING);
        final OperationStatus operationStatus = isStabilized
                ? creationSuccessful ? OperationStatus.SUCCESS : OperationStatus.FAILED
                : OperationStatus.IN_PROGRESS;

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .callbackContext(context)
                .callbackDelaySeconds(isStabilized ? 0 : CALLBACK_DELAY_SECONDS)
                .resourceModel(model)
                .status(operationStatus)
                .build();
    }

    private DescribeServiceResponse getService(final ResourceModel model,
                                               final ProxyClient<AppRunnerClient> proxyClient) {
        try {
              return readHandler.getServiceIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return DescribeServiceResponse.builder().build();
        }
    }

    @VisibleForTesting
    void setReadHandler(final ReadHandler readHandler) {
        this.readHandler = readHandler;
    }
}
