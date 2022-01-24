package software.amazon.apprunner.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InvalidStateException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceStatus;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

    @VisibleForTesting
    static final int CALLBACK_DELAY_SECONDS = 10;

    private Logger logger;
    private ReadHandler readHandler;

    public UpdateHandler() {
        readHandler = new ReadHandler();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        final ResourceModel newModel = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();

        final CallbackContext context = callbackContext == null ? new CallbackContext() : callbackContext;

        if (Strings.isNullOrEmpty(previousModel.getServiceArn())) {
            return ProgressEvent.defaultFailureHandler(
                    new CfnNotFoundException(ResourceModel.TYPE_NAME, newModel.getServiceArn()),
                    HandlerErrorCode.NotFound);
        }
        if (Strings.isNullOrEmpty(newModel.getServiceArn())) {
            newModel.setServiceArn(previousModel.getServiceArn());
        }

        Service service;

        // if isUpdateStarted in context is not set, trigger the service update
        if (!context.isUpdateStarted()) {
            final UpdateServiceRequest updateServiceRequest = Translator.translateToUpdateServiceRequest(newModel);
            UpdateServiceResponse updateServiceResponse;
            try {
                updateServiceResponse = proxyClient.injectCredentialsAndInvokeV2(
                        updateServiceRequest, proxyClient.client()::updateService);
            } catch (final ResourceNotFoundException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
            } catch (final InvalidRequestException | InvalidStateException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
            } catch (final AppRunnerException | SdkClientException e) {
                logger.log(e.getMessage());
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
            }
            context.setUpdateStarted(true);
            service = updateServiceResponse.service();
        } else {
            // if the service update is already in progress, monitor its progress
            final DescribeServiceResponse describeServiceResponse = getService(newModel, proxyClient);
            service = describeServiceResponse.service();
        }

        logger.log("Update operation in progress for the service: " + service.toString());
        final boolean isStabilized = service.status() != ServiceStatus.OPERATION_IN_PROGRESS;
        final boolean updateSuccessful = service.status().equals(ServiceStatus.RUNNING);
        final OperationStatus operationStatus = isStabilized
                ? updateSuccessful ? OperationStatus.SUCCESS : OperationStatus.FAILED
                : OperationStatus.IN_PROGRESS;

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .callbackContext(context)
                .callbackDelaySeconds(isStabilized ? 0 : CALLBACK_DELAY_SECONDS)
                .resourceModel(newModel)
                .status(operationStatus)
                .build();
    }


    private DescribeServiceResponse getService(final ResourceModel model,
                                               final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            return readHandler.getServiceIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            logger.log("Resource with the provided Arn does not exist: " + model.getServiceArn());
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getServiceArn());
        }
    }

    @VisibleForTesting
    void setReadHandler(final ReadHandler readHandler) {
        this.readHandler = readHandler;
    }
}
