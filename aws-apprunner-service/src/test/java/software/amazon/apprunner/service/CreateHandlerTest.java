package software.amazon.apprunner.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.CreateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.CreateServiceResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.SourceConfiguration;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.apprunner.service.TestData.NETWORK_CONFIGURATION;
import static software.amazon.apprunner.service.TestData.NETWORK_CONFIGURATION_PRIVATE;
import static software.amazon.apprunner.service.TestData.SERVICE_NAME;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_OPERATION_IN_PROGRESS;
import static software.amazon.apprunner.service.TestData.SERVICE_ARN;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_RUNNING;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_CREATE_FAILED;
import static software.amazon.apprunner.service.TestData.CLIENT_TOKEN;
import static software.amazon.apprunner.service.TestData.LOGICAL_RESOURCE_IDENTIFIER;
import static software.amazon.apprunner.service.TestData.SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED;
import static software.amazon.apprunner.service.Translator.translateFromNetworkConfiguration;
import static software.amazon.apprunner.service.Translator.translateFromServiceObservabilityConfiguration;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AppRunnerClient> proxyClient;

    @Mock
    private AppRunnerClient apprunnerClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, apprunnerClient);
    }

    @AfterEach
    public void tear_down() {
        verify(apprunnerClient, atLeastOnce()).createService(any(CreateServiceRequest.class));
        verify(apprunnerClient, atLeastOnce()).describeService(any(DescribeServiceRequest.class));
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_ServiceCreationStarted_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_OPERATION_IN_PROGRESS)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_OPERATION_IN_PROGRESS)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ServiceCreationVpc_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION))
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(NETWORK_CONFIGURATION)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ServiceCreationServiceObservabilityConfiguration_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .observabilityConfiguration(translateFromServiceObservabilityConfiguration(SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED))
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .observabilityConfiguration(SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ServiceCreationPrivate_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION_PRIVATE))
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .networkConfiguration(NETWORK_CONFIGURATION_PRIVATE)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_RunningService_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_RunningServiceWithoutName_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceArn(SERVICE_ARN)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_IDENTIFIER)
                .clientRequestToken(CLIENT_TOKEN)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_RUNNING)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_CreateFailed_InvalidRequestException() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenThrow(InvalidRequestException.class);

        assertThrows(CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_CreateFailed_AppRunnerException() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_RUNNING)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenThrow(AppRunnerException.class);

        assertThrows(AppRunnerException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_CreateFailed_NoException() {
        final CreateHandler handler = new CreateHandler();
        final ResourceModel model = ResourceModel.builder()
                .status(SERVICE_STATUS_CREATE_FAILED)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        Service service = Service.builder()
                .status(SERVICE_STATUS_CREATE_FAILED)
                .serviceArn(SERVICE_ARN)
                .serviceName(SERVICE_NAME)
                .build();
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        CreateServiceResponse createServiceResponse = CreateServiceResponse.builder()
                .service(service)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);
        when(proxyClient.client().createService(any(CreateServiceRequest.class)))
                .thenReturn(createServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
