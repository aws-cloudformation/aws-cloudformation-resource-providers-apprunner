package software.amazon.apprunner.vpcingressconnection;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InvalidStateException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.UpdateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.UpdateVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionStatus;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

    @VisibleForTesting
    static final int CALLBACK_DELAY_SECONDS = 10;

    private Logger logger;
    private ReadHandler readHandler;

    public UpdateHandler() { readHandler = new ReadHandler(); }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        final ResourceModel newModel = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();

        final CallbackContext context = callbackContext == null ? new CallbackContext() : callbackContext;

        if (Strings.isNullOrEmpty(previousModel.getVpcIngressConnectionArn())) {
            return ProgressEvent.defaultFailureHandler(
                    new CfnNotFoundException(ResourceModel.TYPE_NAME, newModel.getVpcIngressConnectionArn()),
                    HandlerErrorCode.NotFound);
        }
        if (Strings.isNullOrEmpty(newModel.getVpcIngressConnectionArn())) {
            newModel.setVpcIngressConnectionArn(previousModel.getVpcIngressConnectionArn());
        }

        VpcIngressConnection vpcIngressConnection;

        // if isUpdateStarted in context is not set, trigger the VpcIngressConnection update
        if (!context.isUpdateStarted()) {
            UpdateVpcIngressConnectionRequest updateVpcIngressConnectionRequest = Translator.translateToUpdateVpcIngressConnectionRequest(newModel);
            UpdateVpcIngressConnectionResponse updateVpcIngressConnectionResponse;
            try {
                updateVpcIngressConnectionResponse = proxyClient.injectCredentialsAndInvokeV2(
                        updateVpcIngressConnectionRequest, proxyClient.client()::updateVpcIngressConnection);
            } catch (final ResourceNotFoundException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
            } catch (final InvalidRequestException | InvalidStateException e) {
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
            } catch (final AppRunnerException | SdkClientException e) {
                logger.log(e.getMessage());
                return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
            }
            context.setUpdateStarted(true);
            vpcIngressConnection = updateVpcIngressConnectionResponse.vpcIngressConnection();
        } else {
            // if update VpcIngressConnection is already in progress, call describe to monitor its progress
            final DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = getVpcIngressConnection(newModel, proxyClient);
            vpcIngressConnection = describeVpcIngressConnectionResponse.vpcIngressConnection();
        }

        logger.log("Update operation in progress for the VpcIngressConnection: " + vpcIngressConnection.toString());
        final boolean isStabilized = vpcIngressConnection.status() != VpcIngressConnectionStatus.PENDING_UPDATE;
        final boolean updateSuccessful = vpcIngressConnection.status().equals(VpcIngressConnectionStatus.AVAILABLE);
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

    private DescribeVpcIngressConnectionResponse getVpcIngressConnection(final ResourceModel model,
                                                                      final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            return readHandler.getVpcIngressConnectionIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            logger.log("Resource with the provided Arn does not exist: " + model.getVpcIngressConnectionArn());
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getVpcIngressConnectionArn());
        }
    }
}
