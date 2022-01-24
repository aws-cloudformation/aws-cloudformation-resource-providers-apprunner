package software.amazon.apprunner.vpcconnector;

import org.junit.jupiter.api.AfterEach;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.AppRunnerException;
import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorResponse;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
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
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static software.amazon.apprunner.vpcconnector.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.vpcconnector.TestData.VPC_CONNECTOR;
import static software.amazon.apprunner.vpcconnector.TestData.VPC_CONNECTOR_NAME;
import static software.amazon.apprunner.vpcconnector.TestData.SECURITY_GROUP_ID;
import static software.amazon.apprunner.vpcconnector.TestData.SUBNET_ID;

import static software.amazon.apprunner.vpcconnector.Translator.translateVpcConnectorFromResponse;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AppRunnerClient> proxyClient;

    @Mock
    private AppRunnerClient apprunnerClient;

    private ResourceModel newResource;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, apprunnerClient);
        newResource = ResourceModel.builder()
                .vpcConnectorName(VPC_CONNECTOR_NAME)
                .subnets(new HashSet<String>(Arrays.asList(SUBNET_ID)))
                .securityGroups(new HashSet<String>(Arrays.asList(SECURITY_GROUP_ID)))
                .build();
    }

    @AfterEach
    public void tear_down() {
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_Create_Success() {
        final CreateHandler handler = new CreateHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(newResource)
                .build();

        CreateVpcConnectorResponse createVpcConnectorResponse = CreateVpcConnectorResponse.builder()
                .vpcConnector(VPC_CONNECTOR)
                .build();
        when(proxyClient.client().createVpcConnector(any(CreateVpcConnectorRequest.class)))
                .thenReturn(createVpcConnectorResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, AbstractTestBase.logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isEqualTo(translateVpcConnectorFromResponse(VPC_CONNECTOR));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Create_Failed() {
        final CreateHandler handler = new CreateHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(newResource)
                .build();

        CreateVpcConnectorResponse createVpcConnectorResponse = CreateVpcConnectorResponse.builder().build();
        when(proxyClient.client().createVpcConnector(any(CreateVpcConnectorRequest.class)))
                .thenReturn(createVpcConnectorResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, AbstractTestBase.logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getResourceModel()).isEqualTo(newResource);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    public void handleRequest_Create_AlreadyExist() {
        final CreateHandler handler = new CreateHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .build();

        DescribeVpcConnectorResponse describeVpcConnectorResponse = DescribeVpcConnectorResponse.builder()
                .vpcConnector(VPC_CONNECTOR)
                .build();
        when(proxyClient.client().describeVpcConnector(any(DescribeVpcConnectorRequest.class)))
                .thenReturn(describeVpcConnectorResponse);

        assertThrows(CfnAlreadyExistsException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, AbstractTestBase.logger));
    }

    @Test
    public void handleRequest_Create_InvalidRequestException() {
        final CreateHandler handler = new CreateHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(newResource)
                .build();
        when(proxyClient.client().createVpcConnector(any(CreateVpcConnectorRequest.class)))
                .thenThrow(InvalidRequestException.class);

        assertThrows(CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, AbstractTestBase.logger));
    }

    @Test
    public void handleRequest_Create_AppRunnerException() {
        final CreateHandler handler = new CreateHandler();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(newResource)
                .build();
        when(proxyClient.client().createVpcConnector(any(CreateVpcConnectorRequest.class)))
                .thenThrow(AppRunnerException.class);

        assertThrows(AppRunnerException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, AbstractTestBase.logger));
    }
}
