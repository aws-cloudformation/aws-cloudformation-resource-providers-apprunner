package software.amazon.apprunner.vpcconnector;

import com.amazonaws.util.StringUtils;
import com.google.common.base.Strings;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorResponse;
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

import static software.amazon.apprunner.vpcconnector.Translator.translateToCreateVpcConnectorRequest;
import static software.amazon.apprunner.vpcconnector.Translator.translateVpcConnectorFromResponse;

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

        if (StringUtils.isNullOrEmpty(model.getVpcConnectorName())) {
            logger.log(String.format("Generating VpcConnector name..."));
            final String vpcConnectorName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    request.getClientRequestToken());
            model.setVpcConnectorName(vpcConnectorName);
        }

        if (!Strings.isNullOrEmpty(model.getVpcConnectorArn())) {
            DescribeVpcConnectorResponse describeVpcConnectorResponse = getVpcConnector(model, proxyClient);

            if (describeVpcConnectorResponse.vpcConnector() != null) {
                throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, model.getVpcConnectorName());
            }
        }

        final CreateVpcConnectorRequest createVpcConnectorRequest = translateToCreateVpcConnectorRequest(model, tagHelper.generateTagsForCreate(model, request));
        CreateVpcConnectorResponse createVpcConnectorResponse;

        try {
            createVpcConnectorResponse = proxyClient.injectCredentialsAndInvokeV2(
                    createVpcConnectorRequest, proxyClient.client()::createVpcConnector);
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

        if (createVpcConnectorResponse.vpcConnector() == null) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .build();
        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(translateVpcConnectorFromResponse(createVpcConnectorResponse.vpcConnector()))
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }

    private DescribeVpcConnectorResponse getVpcConnector(final ResourceModel model,
                                                         final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            return readHandler.getVpcConnectorIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return DescribeVpcConnectorResponse.builder().build();
        }
    }
}
