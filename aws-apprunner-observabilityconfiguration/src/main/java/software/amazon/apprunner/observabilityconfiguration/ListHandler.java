package software.amazon.apprunner.observabilityconfiguration;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.ListObservabilityConfigurationsRequest;
import software.amazon.awssdk.services.apprunner.model.ListObservabilityConfigurationsResponse;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationSummary;
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

import static software.amazon.apprunner.observabilityconfiguration.Translator.translateObservabilityConfigurationSummaryToResourceModel;

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
        final List<ObservabilityConfigurationSummary> observabilityConfigurationList = new ArrayList<>();

        String nextToken = null;
        do {
            final ListObservabilityConfigurationsResponse listObservabilityConfigurationsResponse = getObservabilityConfigurationList(nextToken, proxyClient);
            observabilityConfigurationList.addAll(listObservabilityConfigurationsResponse.observabilityConfigurationSummaryList());
            nextToken = listObservabilityConfigurationsResponse.nextToken();
        } while (nextToken != null);

        models.addAll(
                observabilityConfigurationList.stream()
                        .map(observabilityConfigurationSummary -> translateObservabilityConfigurationSummaryToResourceModel(observabilityConfigurationSummary))
                        .collect(ImmutableList.toImmutableList())
        );

        models.forEach(resourceModel -> logger.log(resourceModel.toString()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ListObservabilityConfigurationsResponse getObservabilityConfigurationList(final String nextToken,
                                                                                      final ProxyClient<AppRunnerClient> proxyClient) {
        final ListObservabilityConfigurationsRequest listObservabilityConfigurationsRequest = Translator.translateToListObservabilityConfigurationsRequest(nextToken);
        try {
            return proxyClient.injectCredentialsAndInvokeV2(listObservabilityConfigurationsRequest, proxyClient.client()::listObservabilityConfigurations);
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