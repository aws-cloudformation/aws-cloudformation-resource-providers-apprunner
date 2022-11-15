package software.amazon.apprunner.vpcingressconnection;


import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InvalidStateException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionStatus;
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

        VpcIngressConnection vpcIngressConnection;
        // If deleteStarted in callback context is not set (i.e. not being deleted), trigger VpcIngressConnection deletion
        if (!context.isDeleteStarted()) {

            // Call a delete API, handle exceptions
            final DeleteVpcIngressConnectionRequest deleteVpcIngressConnectionRequest = Translator.translateToDeleteVpcIngressConnectionRequest(model);
            DeleteVpcIngressConnectionResponse deleteVpcIngressConnectionResponse;
            try {
                deleteVpcIngressConnectionResponse = proxyClient.injectCredentialsAndInvokeV2(
                        deleteVpcIngressConnectionRequest, proxyClient.client()::deleteVpcIngressConnection);
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
            vpcIngressConnection = deleteVpcIngressConnectionResponse.vpcIngressConnection();


        } else {
            try {
                final DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse
                        = readHandler.getVpcIngressConnectionIfItExists(model, proxyClient);
                vpcIngressConnection = describeVpcIngressConnectionResponse.vpcIngressConnection();
            } catch (final CfnNotFoundException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
            }
        }


        // check the status of VpcIngressConnection, and set operationStatus accordingly
        final boolean isStabilized = vpcIngressConnection != null && vpcIngressConnection.status() != VpcIngressConnectionStatus.PENDING_DELETION;
        final boolean deletionSuccessful = vpcIngressConnection != null && vpcIngressConnection.status().equals(VpcIngressConnectionStatus.DELETED);
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
}
