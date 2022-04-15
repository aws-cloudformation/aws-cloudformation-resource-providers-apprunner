package software.amazon.apprunner.observabilityconfiguration;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfiguration;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationStatus;
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

import static software.amazon.apprunner.observabilityconfiguration.Translator.translateObservabilityConfigurationFromResponse;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToDescribeObservabilityConfigurationRequest;

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

        DescribeObservabilityConfigurationResponse response;

        try {
            response = getObservabilityConfigurationIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        }

        final ObservabilityConfiguration observabilityConfiguration = response.observabilityConfiguration();
        if (observabilityConfiguration.status().equals(ObservabilityConfigurationStatus.INACTIVE)) {
            logger.log((String.format("ObservabilityConfiguration with name %s is %s. Returning empty response in read handler.",
                    observabilityConfiguration.observabilityConfigurationName(),
                    ObservabilityConfigurationStatus.INACTIVE)));
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .build();
        } else {
            final ResourceModel outputModel = translateObservabilityConfigurationFromResponse(response.observabilityConfiguration());
            logger.log((String.format("ObservabilityConfiguration with name %s has been read.", outputModel.getObservabilityConfigurationName())));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(outputModel)
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }

    protected DescribeObservabilityConfigurationResponse getObservabilityConfigurationIfItExists(final ResourceModel model,
                                                                     final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            final DescribeObservabilityConfigurationRequest describeObservabilityConfigurationRequest = translateToDescribeObservabilityConfigurationRequest(model);
            return proxyClient.injectCredentialsAndInvokeV2(describeObservabilityConfigurationRequest,
                    proxyClient.client()::describeObservabilityConfiguration);
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