package software.amazon.apprunner.vpcconnector;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.VpcConnector;
import software.amazon.awssdk.services.apprunner.model.VpcConnectorStatus;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.apprunner.vpcconnector.Translator.translateVpcConnectorFromResponse;
import static software.amazon.apprunner.vpcconnector.Translator.translateToDescribeVpcConnectorRequest;

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

        DescribeVpcConnectorResponse response;

        try {
            response = getVpcConnectorIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        }

        final VpcConnector vpcConnector = response.vpcConnector();
        if (vpcConnector.status().equals(VpcConnectorStatus.INACTIVE)) {
            logger.log((String.format("VpcConnector with name %s is %s. Returning empty response in read handler.",
                    vpcConnector.vpcConnectorName(),
                    VpcConnectorStatus.INACTIVE)));
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .build();
        } else {
            final ResourceModel outputModel = translateVpcConnectorFromResponse(response.vpcConnector());
            logger.log((String.format("VpcConnector with name %s has been read.", outputModel.getVpcConnectorName())));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(outputModel)
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }

    protected DescribeVpcConnectorResponse getVpcConnectorIfItExists(final ResourceModel model,
                                                        final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            final DescribeVpcConnectorRequest describeVpcConnectorRequest = translateToDescribeVpcConnectorRequest(model);
            return proxyClient.injectCredentialsAndInvokeV2(describeVpcConnectorRequest,
                    proxyClient.client()::describeVpcConnector);
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
