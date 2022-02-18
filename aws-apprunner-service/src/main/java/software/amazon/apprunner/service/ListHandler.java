package software.amazon.apprunner.service;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ListServicesRequest;
import software.amazon.awssdk.services.apprunner.model.ListServicesResponse;
import software.amazon.awssdk.services.apprunner.model.ServiceSummary;
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
        final List<ServiceSummary> serviceSummaryList = new ArrayList<>();

        String nextToken = null;
        do {
            final ListServicesResponse listServicesResponse = getServiceList(nextToken, proxyClient);
            serviceSummaryList.addAll(listServicesResponse.serviceSummaryList());
            nextToken = listServicesResponse.nextToken();
        } while (nextToken != null);

        models.addAll(
                serviceSummaryList.stream()
                        .map(serviceSummary -> Translator.translateServiceSummaryToResourceModel(serviceSummary))
                        .collect(ImmutableList.toImmutableList())
        );

        models.forEach(resourceModel -> logger.log(resourceModel.toString()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ListServicesResponse getServiceList(final String nextToken,
                                                final ProxyClient<AppRunnerClient> proxyClient) {
        final ListServicesRequest listServicesRequest = Translator.translateToListServicesRequest(nextToken);
        try {
            return proxyClient.injectCredentialsAndInvokeV2(listServicesRequest, proxyClient.client()::listServices);
        } catch (final InvalidRequestException e) {
            logger.log("Request is not valid: " + e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (final AwsServiceException | SdkClientException e) {
            logger.log(e.getMessage());
            throw new CfnInternalFailureException(e);
        }
    }
}
