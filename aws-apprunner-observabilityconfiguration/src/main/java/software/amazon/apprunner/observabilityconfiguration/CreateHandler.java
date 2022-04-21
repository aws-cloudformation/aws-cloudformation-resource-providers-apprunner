package software.amazon.apprunner.observabilityconfiguration;

import com.amazonaws.util.StringUtils;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.CreateObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.CreateObservabilityConfigurationResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ServiceQuotaExceededException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;

import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToCreateObservabilityConfigurationRequest;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateObservabilityConfigurationFromResponse;

public class CreateHandler extends BaseHandlerStd {

    private Logger logger;
    private ReadHandler readHandler;
    private TagHelper tagHelper;

    public CreateHandler() {
        readHandler = new ReadHandler();
        tagHelper = new TagHelper();
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        ResourceModel model = request.getDesiredResourceState();

        if (StringUtils.isNullOrEmpty(model.getObservabilityConfigurationName())) {
            logger.log(String.format("Generating ObservabilityConfiguration name..."));
            final String observabilityConfigurationName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    request.getClientRequestToken(),
                    32);
            model.setObservabilityConfigurationName(observabilityConfigurationName);
        }

        if (!Strings.isNullOrEmpty(model.getObservabilityConfigurationArn())) {
            DescribeObservabilityConfigurationResponse describeObservabilityConfigurationResponse = getObservabilityConfiguration(model, proxyClient);

            if (describeObservabilityConfigurationResponse.observabilityConfiguration() != null) {
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, model.getObservabilityConfigurationName());
            }
        }

        final CreateObservabilityConfigurationRequest createObservabilityConfigurationRequest = translateToCreateObservabilityConfigurationRequest(model, tagHelper.generateTagsForCreate(model, request));
        CreateObservabilityConfigurationResponse createObservabilityConfigurationResponse;

        try {
            createObservabilityConfigurationResponse = proxyClient.injectCredentialsAndInvokeV2(
                    createObservabilityConfigurationRequest, proxyClient.client()::createObservabilityConfiguration);
        } catch (final InvalidRequestException e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (final ServiceQuotaExceededException e) {
            logger.log(e.getMessage());
            throw new CfnServiceLimitExceededException(e);
        } catch (final InternalServiceErrorException e) {
            logger.log(e.getMessage());
            throw new CfnServiceInternalErrorException(e);
        } catch (SdkClientException e) {
            logger.log(e.getMessage());
            throw new CfnServiceInternalErrorException(ResourceModel.TYPE_NAME, e);
        }

        if (createObservabilityConfigurationResponse.observabilityConfiguration() == null) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .build();
        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(translateObservabilityConfigurationFromResponse(createObservabilityConfigurationResponse.observabilityConfiguration()))
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }

    private DescribeObservabilityConfigurationResponse getObservabilityConfiguration(final ResourceModel model,
                                                         final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            return readHandler.getObservabilityConfigurationIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return DescribeObservabilityConfigurationResponse.builder().build();
        }
    }
}
