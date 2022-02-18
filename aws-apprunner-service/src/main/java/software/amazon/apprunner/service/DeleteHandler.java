package software.amazon.apprunner.service;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.DeleteServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteServiceResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InvalidStateException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceStatus;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

    @VisibleForTesting
    static final int CALLBACK_DELAY_SECONDS = 10;

    private Logger logger;
    private ReadHandler readHandler;

    public DeleteHandler() {
        readHandler = new ReadHandler();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();
        final CallbackContext context = callbackContext == null ? new CallbackContext() : callbackContext;

        Service service;
        // if isDeleteStarted in context is not set, trigger the service deletion
        if (!context.isDeleteStarted()) {
            final DeleteServiceRequest deleteServiceRequest = Translator.translateToDeleteServiceRequest(model);
            DeleteServiceResponse deleteServiceResponse;
            try {
                deleteServiceResponse = proxyClient.injectCredentialsAndInvokeV2(
                        deleteServiceRequest, proxyClient.client()::deleteService);
            } catch (final ResourceNotFoundException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
            } catch (final InvalidStateException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
            } catch (final InvalidRequestException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
            } catch (final AppRunnerException | SdkClientException e) {
                logger.log(e.getMessage());
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
            }
            context.setDeleteStarted(true);
            service = deleteServiceResponse.service();
        } else {
            // if the service deletion is already in progress, monitor its progress
            try {
                final DescribeServiceResponse describeServiceResponse
                        = readHandler.getServiceIfItExists(model, proxyClient);
                service = describeServiceResponse.service();
            } catch (final CfnNotFoundException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
            }
        }

        final boolean isStabilized = service != null && service.status() != ServiceStatus.OPERATION_IN_PROGRESS;
        final boolean deletionSuccessful = service != null && service.status().equals(ServiceStatus.DELETED);
        final OperationStatus operationStatus = isStabilized
                ? deletionSuccessful ? OperationStatus.SUCCESS : OperationStatus.FAILED
                : OperationStatus.IN_PROGRESS;

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .callbackContext(context)
                .callbackDelaySeconds(isStabilized ? 0 : CALLBACK_DELAY_SECONDS)
                .resourceModel(isStabilized ? null : model)
                .status(operationStatus)
                .build();
    }

    @VisibleForTesting
    void setReadHandler(final ReadHandler readHandler) {
        this.readHandler = readHandler;
    }
}
