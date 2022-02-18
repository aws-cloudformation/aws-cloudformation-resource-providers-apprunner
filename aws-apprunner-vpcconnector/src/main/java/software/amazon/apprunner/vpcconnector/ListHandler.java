package software.amazon.apprunner.vpcconnector;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ListVpcConnectorsRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcConnectorsResponse;
import software.amazon.awssdk.services.apprunner.model.VpcConnector;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

import static software.amazon.apprunner.vpcconnector.Translator.translateVpcConnectorFromResponse;

public class ListHandler extends BaseHandlerStd {

    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<AppRunnerClient> proxyClient,
        final Logger logger) {

        this.logger = logger;
        final List<ResourceModel> models = new ArrayList<>();
        final List<VpcConnector> vpcConnectorList = new ArrayList<>();

        String nextToken = null;
        do {
            final ListVpcConnectorsResponse listVpcConnectorsResponse = getVpcConnectorList(nextToken, proxyClient);
            vpcConnectorList.addAll(listVpcConnectorsResponse.vpcConnectors());
            nextToken = listVpcConnectorsResponse.nextToken();
        } while (nextToken != null);

        models.addAll(
                vpcConnectorList.stream()
                        .map(vpcConnector -> translateVpcConnectorFromResponse(vpcConnector))
                        .collect(ImmutableList.toImmutableList())
        );

        models.forEach(resourceModel -> logger.log(resourceModel.toString()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ListVpcConnectorsResponse getVpcConnectorList(final String nextToken,
                                                          final ProxyClient<AppRunnerClient> proxyClient) {
        final ListVpcConnectorsRequest listVpcConnectorsRequest = Translator.translateToListVpcConnectorsRequest(nextToken);
        try {
            return proxyClient.injectCredentialsAndInvokeV2(listVpcConnectorsRequest, proxyClient.client()::listVpcConnectors);
        } catch (final InvalidRequestException e) {
            logger.log("Request is not valid: " + e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (final AwsServiceException | SdkClientException e) {
            logger.log(e.getMessage());
            throw new CfnInternalFailureException(e);
        }
    }
}
