package software.amazon.apprunner.vpcingressconnection;

import com.amazonaws.util.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ServiceQuotaExceededException;

import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionStatus;
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

        VpcIngressConnection vpcIngressConnection;

        // Generate and set name for VpcIngressConnection
        if (StringUtils.isNullOrEmpty(model.getVpcIngressConnectionName())) {
            logger.log(String.format("Generating VpcIngressConnection Name..."));
            final String vpcIngressConnectionName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    request.getClientRequestToken());
            model.setVpcIngressConnectionName(vpcIngressConnectionName);
        }

        // If createStarted in callback context is not set (i.e. not being created), trigger VpcIngressConnection creation
        if (!context.isCreateStarted()){
            if (!Strings.isNullOrEmpty(model.getVpcIngressConnectionArn())) {
                DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = getVpcIngressConnection(model, proxyClient);
                logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
                if (describeVpcIngressConnectionResponse.vpcIngressConnection() != null) {
                    throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, model.getVpcIngressConnectionName());
                }
            }

            final CreateVpcIngressConnectionRequest createVpcIngressConnectionRequest = Translator.translateToCreateVpcIngressConnectionRequest(model, tagHelper.generateTagsForCreate(model, request));
            CreateVpcIngressConnectionResponse createVpcIngressConnectionResponse;

            // Call a create API, handle exceptions
            try {
                createVpcIngressConnectionResponse = proxyClient.injectCredentialsAndInvokeV2(
                        createVpcIngressConnectionRequest, proxyClient.client()::createVpcIngressConnection);
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
            logger.log(String.format("%s successfully created. will be checking stability", ResourceModel.TYPE_NAME));

            // translate VpcIngressConnection from API call response to model
            // set callback context CreateStarted
            vpcIngressConnection = createVpcIngressConnectionResponse.vpcIngressConnection();
            model = Translator.translateVpcIngressConnectionFromResponse(createVpcIngressConnectionResponse.vpcIngressConnection());
            context.setCreateStarted(true);
        } else {
            // if vpcIngressConnection creation is in progress, monitor its progress
            final DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = getVpcIngressConnection(model, proxyClient);
            vpcIngressConnection = describeVpcIngressConnectionResponse.vpcIngressConnection();
        }

        // set status for model
        model.setStatus(vpcIngressConnection.status().toString());

        // check the status of VpcIngressConnection, and set operationStatus accordingly
        final boolean isStabilized = vpcIngressConnection != null && vpcIngressConnection.status() != VpcIngressConnectionStatus.PENDING_CREATION;
        if (isStabilized) {
            logger.log(String.format("%s [%s] has been stabilized.", ResourceModel.TYPE_NAME, model.getVpcIngressConnectionName()));
        }
        final boolean creationSuccessful = vpcIngressConnection != null && vpcIngressConnection.status().equals(VpcIngressConnectionStatus.AVAILABLE);
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

    private DescribeVpcIngressConnectionResponse getVpcIngressConnection(final ResourceModel model,
                                                                         final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            return readHandler.getVpcIngressConnectionIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return DescribeVpcIngressConnectionResponse.builder().build();
        }
    }
}
