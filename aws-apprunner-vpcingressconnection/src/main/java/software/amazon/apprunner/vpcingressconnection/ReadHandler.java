package software.amazon.apprunner.vpcingressconnection;

import software.amazon.awssdk.services.apprunner.AppRunnerClient;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionStatus;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();

        DescribeVpcIngressConnectionResponse response;

        // check if the VPCIngressConnection exists by calling a describe API
        try {
            response = getVpcIngressConnectionIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        }

        final VpcIngressConnection vpcIngressConnection = response.vpcIngressConnection();
        final ResourceModel outputModel = Translator.translateVpcIngressConnectionFromResponse(response.vpcIngressConnection());

        // if VPCIngressConnection is deleted, cannot read from CFN and throw CfnNotFoundException
        if (vpcIngressConnection.status().equals(VpcIngressConnectionStatus.DELETED)) {
            logger.log(String.format("VpcIngressConnection with name %s is in DELETED state and cannot be read",
                    outputModel.getVpcIngressConnectionName()));
            return ProgressEvent.defaultFailureHandler(
                    new CfnNotFoundException(software.amazon.apprunner.vpcingressconnection.ResourceModel.TYPE_NAME, model.getVpcIngressConnectionArn()),
                    HandlerErrorCode.NotFound);
        } else {

            logger.log((String.format("VpcIngressConnection with name %s has been read.", outputModel.getVpcIngressConnectionName())));
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(outputModel)
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }

    protected DescribeVpcIngressConnectionResponse getVpcIngressConnectionIfItExists(final ResourceModel model,
                                                                                  final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            final DescribeVpcIngressConnectionRequest describeVpcIngressConnectionRequest = Translator.translateToDescribeVpcIngressConnectionRequest(model);
            return proxyClient.injectCredentialsAndInvokeV2(describeVpcIngressConnectionRequest,
                    proxyClient.client()::describeVpcIngressConnection);
        } catch (final ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (final InvalidRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (final InternalServiceErrorException e) {
            throw new CfnInternalFailureException(e);
        } catch (AwsServiceException | SdkClientException e) {
            throw new CfnGeneralServiceException(e);
        }
    }

}
