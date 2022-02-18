package software.amazon.apprunner.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.EgressType;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.apprunner.service.TestData.NETWORK_CONFIGURATION;
import static software.amazon.apprunner.service.TestData.SERVICE;
import static software.amazon.apprunner.service.TestData.SERVICE_ARN;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_OPERATION_IN_PROGRESS;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_RUNNING;
import static software.amazon.apprunner.service.Translator.translateFromNetworkConfiguration;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AppRunnerClient> proxyClient;

    @Mock
    private AppRunnerClient apprunnerClient;

    private boolean withCallback;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        apprunnerClient = mock(AppRunnerClient.class);
        proxyClient = MOCK_PROXY(proxy, apprunnerClient);
        withCallback = false;
    }

    @AfterEach
    public void tear_down() {
        if (withCallback) {
            verify(apprunnerClient, atLeastOnce()).describeService(any(DescribeServiceRequest.class));
        } else {
            verify(apprunnerClient, atLeastOnce()).updateService(any(UpdateServiceRequest.class));

        }
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_Success_OperationInProgress() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_OPERATION_IN_PROGRESS)
                .build();
        UpdateServiceResponse updateServiceResponse = UpdateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenReturn(updateServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_Running() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .build();
        UpdateServiceResponse updateServiceResponse = UpdateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenReturn(updateServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_RunningInCallback() {
        final UpdateHandler handler = new UpdateHandler();
        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder()
                .service(SERVICE)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);

        withCallback = true;
        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setUpdateStarted(true);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_PublicToVpc() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel previousModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .build();
        final ResourceModel desiredModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModel)
                .previousResourceState(previousModel)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .networkConfiguration(NETWORK_CONFIGURATION)
                .build();
        UpdateServiceResponse updateServiceResponse = UpdateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenReturn(updateServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_VpcToPublic() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel previousModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION))
                .build();
        final ResourceModel desiredModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(NetworkConfiguration.builder()
                        .egressConfiguration(EgressConfiguration.builder()
                                .egressType(EgressType.DEFAULT.name())
                                .build())
                        .build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModel)
                .previousResourceState(previousModel)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .networkConfiguration(software.amazon.awssdk.services.apprunner.model.NetworkConfiguration.builder()
                        .egressConfiguration(software.amazon.awssdk.services.apprunner.model.EgressConfiguration.builder()
                                .egressType(EgressType.DEFAULT.name())
                                .build())
                        .build())
                .build();
        UpdateServiceResponse updateServiceResponse = UpdateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenReturn(updateServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_NullNetworkConfiguration() {

        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel previousModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION))
                .build();
        final ResourceModel desiredModel = ResourceModel.builder()
                .serviceArn(SERVICE_ARN)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModel)
                .previousResourceState(previousModel)
                .build();

        Service service = Service.builder()
                .serviceArn(SERVICE_ARN)
                .status(SERVICE_STATUS_RUNNING)
                .build();
        UpdateServiceResponse updateServiceResponse = UpdateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenReturn(updateServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Exception_InternalServiceErrorException() {
        final UpdateHandler handler = new UpdateHandler();
        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenThrow(InternalServiceErrorException.class);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }

    @Test
    public void handleRequest_Exception_InvalidRequestException() {
        final UpdateHandler handler = new UpdateHandler();
        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();
        final String errorMessage = "The Cpu: 2048 and Memory: 2048 combination provided is not supported.";
        when(proxyClient.client().updateService(any(UpdateServiceRequest.class)))
                .thenThrow(InvalidRequestException.builder().message(errorMessage).build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
}
