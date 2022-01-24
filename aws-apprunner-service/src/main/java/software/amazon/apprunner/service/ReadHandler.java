package software.amazon.apprunner.service;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.ServiceStatus;
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

import static software.amazon.apprunner.service.Translator.translateServiceFromResponse;
import static software.amazon.apprunner.service.Translator.translateToDescribeServiceRequest;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AppRunnerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();

        DescribeServiceResponse response;

        try {
            response = getServiceIfItExists(model, proxyClient);
        } catch (final CfnNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        }

        final ResourceModel outputModel = translateServiceFromResponse(response.service());
        if (outputModel.getStatus().equals(ServiceStatus.DELETED.toString())) {
            logger.log(String.format("Service with name %s is in Deleted State and cannot be read.",
                    outputModel.getServiceName()));
            return ProgressEvent.defaultFailureHandler(
                    new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getServiceArn()),
                    HandlerErrorCode.NotFound);
        }

        logger.log((String.format("Service with name %s has been read.", outputModel.getServiceName())));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(outputModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    public DescribeServiceResponse getServiceIfItExists(final ResourceModel model,
                                                         final ProxyClient<AppRunnerClient> proxyClient) {
        try {
            final DescribeServiceRequest describeServiceRequest = translateToDescribeServiceRequest(model);
            return proxyClient.injectCredentialsAndInvokeV2(describeServiceRequest,
                    proxyClient.client()::describeService);
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
