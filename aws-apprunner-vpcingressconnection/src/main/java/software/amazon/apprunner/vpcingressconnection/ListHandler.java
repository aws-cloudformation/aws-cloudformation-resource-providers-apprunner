package software.amazon.apprunner.vpcingressconnection;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ListVpcIngressConnectionsRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcIngressConnectionsResponse;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionSummary;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

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
        final List<VpcIngressConnectionSummary> vpcIngressConnectionList = new ArrayList<>();

        String nextToken = null;

        // first call list API
        do {
            final ListVpcIngressConnectionsResponse listVpcIngressConnectionsResponse = getVpcIngressConnectionList(nextToken, proxyClient);
            vpcIngressConnectionList.addAll(listVpcIngressConnectionsResponse.vpcIngressConnectionSummaryList());
            nextToken = listVpcIngressConnectionsResponse.nextToken();
        } while (nextToken != null);

        // construct a list of ResourceModel
        models.addAll(
                vpcIngressConnectionList.stream()
                        .map(vpcIngressConnectionSummary -> Translator.translateVpcIngressConnectionSummaryToResourceModel(vpcIngressConnectionSummary))
                        .collect(ImmutableList.toImmutableList())
        );

        models.forEach(resourceModel -> logger.log(resourceModel.toString()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    protected ListVpcIngressConnectionsResponse getVpcIngressConnectionList(final String nextToken,
                                                                         final ProxyClient<AppRunnerClient> proxyClient) {
        final ListVpcIngressConnectionsRequest listVpcIngressConnectionsRequest = Translator.translateToListVpcIngressConnectionsRequest(nextToken);
        try {
            return proxyClient.injectCredentialsAndInvokeV2(listVpcIngressConnectionsRequest, proxyClient.client()::listVpcIngressConnections);
        } catch (final InvalidRequestException e) {
            logger.log("Request is not valid: " + e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (final InternalServiceErrorException e) {
            logger.log(e.getMessage());
            throw new CfnInternalFailureException(e);
        } catch (final AwsServiceException | SdkClientException e) {
            logger.log(e.getMessage());
            throw new CfnInternalFailureException(e);
        }
    }


}
