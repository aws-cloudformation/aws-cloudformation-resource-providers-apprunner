package software.amazon.apprunner.observabilityconfiguration;

import org.junit.jupiter.api.AfterEach;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfiguration;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static software.amazon.apprunner.observabilityconfiguration.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION_INACTIVE;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AppRunnerClient> proxyClient;

    @Mock
    AppRunnerClient apprunnerClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        proxy = new AmazonWebServicesClientProxy(logger, AbstractTestBase.MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, apprunnerClient);
    }

    @AfterEach
    public void tear_down() {
        verify(apprunnerClient, atLeastOnce()).describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class));
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .build();
        when(proxyClient.client().describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class)))
                .thenReturn(DescribeObservabilityConfigurationResponse.builder()
                        .observabilityConfiguration(OBSERVABILITY_CONFIGURATION)
                        .build());
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(RESOURCE_MODEL);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        final ReadHandler handler = new ReadHandler();
        final ResourceModel model = ResourceModel.builder().build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class)))
                .thenThrow(ResourceNotFoundException.class);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_InvalidRequest() {
        final ReadHandler handler = new ReadHandler();
        final ResourceModel model = ResourceModel.builder().build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class)))
                .thenThrow(InvalidRequestException.class);

        assertThrows(CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_InternalServiceErrorException() {
        final ReadHandler handler = new ReadHandler();
        final ResourceModel model = ResourceModel.builder().build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class)))
                .thenThrow(InternalServiceErrorException.class);

        assertThrows(CfnInternalFailureException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_InActive() {
        final ReadHandler handler = new ReadHandler();
        final ResourceModel model = ResourceModel.builder().build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeObservabilityConfiguration(any(DescribeObservabilityConfigurationRequest.class)))
                .thenReturn(DescribeObservabilityConfigurationResponse.builder()
                        .observabilityConfiguration(OBSERVABILITY_CONFIGURATION_INACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}