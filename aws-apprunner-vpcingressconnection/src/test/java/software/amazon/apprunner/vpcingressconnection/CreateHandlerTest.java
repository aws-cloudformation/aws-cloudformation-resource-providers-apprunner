package software.amazon.apprunner.vpcingressconnection;

import java.time.Duration;

import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.ServiceQuotaExceededException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static software.amazon.apprunner.vpcingressconnection.TestData.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AppRunnerClient> proxyClient;

    @Mock
    private AppRunnerClient apprunnerClient;

    private ResourceModel desiredModelPendingCreation;
    private ResourceModel desiredModelPendingCreation_no_name;
    private ResourceModel desiredModelAvailable;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, apprunnerClient);

        desiredModelPendingCreation = ResourceModel.builder()
                .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
                .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
                .serviceArn(SERVICE_ARN)
                .ingressVpcConfiguration(INGRESS_VPC_CONFIGURATION)
                .domainName(DOMAIN_NAME)
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
                .build();

        desiredModelPendingCreation_no_name = ResourceModel.builder()
                .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
                .serviceArn(SERVICE_ARN)
                .ingressVpcConfiguration(INGRESS_VPC_CONFIGURATION)
                .domainName(DOMAIN_NAME)
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
                .build();

        desiredModelAvailable = ResourceModel.builder()
                .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
                .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .status(VPC_INGRESS_CONNECTION_STATUS_AVAILABLE)
                .serviceArn(SERVICE_ARN)
                .ingressVpcConfiguration(INGRESS_VPC_CONFIGURATION)
                .domainName(DOMAIN_NAME)
                .status(VPC_INGRESS_CONNECTION_STATUS_AVAILABLE)
                .build();
    }

    @AfterEach
    public void tear_down() {
        verify(apprunnerClient, atLeastOnce()).createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class));
        verify(apprunnerClient, atLeastOnce()).describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class));
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_VpcIngressConnectionCreationStarted_Success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(desiredModelPendingCreation)
            .build();


        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        CreateVpcIngressConnectionResponse createVpcIngressConnectionResponse = CreateVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(VPC_INGRESS_CONNECTION_PENDING_CREATION)
                .build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);

        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenReturn(createVpcIngressConnectionResponse);

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
    public void handleRequest_VpcIngressConnectionCreationStarted_NoName_Success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelPendingCreation_no_name)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_IDENTIFIER)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        CreateVpcIngressConnectionResponse createVpcIngressConnectionResponse = CreateVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(VPC_INGRESS_CONNECTION_PENDING_CREATION)
                .build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);

        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenReturn(createVpcIngressConnectionResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_VpcIngressConnectionAvailable_Success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelAvailable)
                .build();


        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        CreateVpcIngressConnectionResponse createVpcIngressConnectionResponse = CreateVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(VPC_INGRESS_CONNECTION_AVAILABLE)
                .build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);

        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenReturn(createVpcIngressConnectionResponse);

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
    public void handleRequest_CreateFailed_InvalidRequestException() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelAvailable)
                .build();
        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);
        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenThrow(InvalidRequestException.class);

        assertThrows(CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_CreateFailed_ServiceQuotaExceededException() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelAvailable)
                .build();
        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);
        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenThrow(ServiceQuotaExceededException.class);

        assertThrows(CfnServiceLimitExceededException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_CreateFailed_InternalServiceErrorException() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelAvailable)
                .build();
        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);
        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenThrow(InternalServiceErrorException.class);

        assertThrows(CfnServiceInternalErrorException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handleRequest_CreateFailed_AppRunnerException() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desiredModelAvailable)
                .build();
        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder().build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);
        when(proxyClient.client().createVpcIngressConnection(any(CreateVpcIngressConnectionRequest.class)))
                .thenThrow(AppRunnerException.class);

        assertThrows(AppRunnerException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }
}
