package software.amazon.apprunner.observabilityconfiguration;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.DeleteObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteObservabilityConfigurationResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.InvalidStateException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

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

        final DeleteObservabilityConfigurationRequest deleteObservabilityConfigurationRequest = Translator.translateToDeleteObservabilityConfigurationRequest(model);
        DeleteObservabilityConfigurationResponse deleteObservabilityConfigurationResponse;

        try {
            deleteObservabilityConfigurationResponse = proxyClient.injectCredentialsAndInvokeV2(
                    deleteObservabilityConfigurationRequest, proxyClient.client()::deleteObservabilityConfiguration);
        } catch (final ResourceNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch (final InvalidStateException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        } catch (final InvalidRequestException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        } catch (final InternalServiceErrorException e) {
            logger.log(e.getMessage());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
        } catch (final AppRunnerException | SdkClientException e) {
            logger.log(e.getMessage());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
        }

        if (deleteObservabilityConfigurationResponse.observabilityConfiguration() == null) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .build();
        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.SUCCESS)
                    .build();
        }
    }
}