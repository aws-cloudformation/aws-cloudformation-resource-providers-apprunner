package software.amazon.apprunner.vpcingressconnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.InvalidRequestException;
import software.amazon.awssdk.services.apprunner.model.UpdateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.UpdateVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_ARN;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_STATUS_AVAILABLE;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_STATUS_PENDING_UPDATE;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_ENDPOINT_ID;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_ID;


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
            verify(apprunnerClient, atLeastOnce()).describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class));
        } else {
            verify(apprunnerClient, atLeastOnce()).updateVpcIngressConnection(any(UpdateVpcIngressConnectionRequest.class));

        }
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_Success_OperationInProgress() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .ingressVpcConfiguration(IngressVpcConfiguration.builder().vpcId(VPC_ID).vpcEndpointId(VPC_ENDPOINT_ID).build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        VpcIngressConnection vpcIngressConnection = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_UPDATE)
                .build();
        UpdateVpcIngressConnectionResponse updateVpcIngressConnectionResponse = UpdateVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnection)
                .build();
        when(proxyClient.client().updateVpcIngressConnection(any(UpdateVpcIngressConnectionRequest.class)))
                .thenReturn(updateVpcIngressConnectionResponse);

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
    public void handleRequest_Success_Available() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .ingressVpcConfiguration(IngressVpcConfiguration.builder().vpcId(VPC_ID).vpcEndpointId(VPC_ENDPOINT_ID).build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        VpcIngressConnection vpcIngressConnection = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_AVAILABLE)
                .build();

        UpdateVpcIngressConnectionResponse updateVpcIngressConnectionResponse = UpdateVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnection)
                .build();
        when(proxyClient.client().updateVpcIngressConnection(any(UpdateVpcIngressConnectionRequest.class)))
                .thenReturn(updateVpcIngressConnectionResponse);

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
    public void handleRequest_Success_RunningInCallback() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .ingressVpcConfiguration(IngressVpcConfiguration.builder().vpcId(VPC_ID).vpcEndpointId(VPC_ENDPOINT_ID).build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        VpcIngressConnection vpcIngressConnection = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_AVAILABLE)
                .build();

        DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnection)
                .build();
        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);

        withCallback = true;
        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setUpdateStarted(true);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Exception_InvalidRequestException() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .ingressVpcConfiguration(IngressVpcConfiguration.builder().vpcId(VPC_ID).vpcEndpointId(VPC_ENDPOINT_ID).build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        when(proxyClient.client().updateVpcIngressConnection(any(UpdateVpcIngressConnectionRequest.class)))
                .thenThrow(InvalidRequestException.builder().message("test invalid request").build());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("test invalid request");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_Exception_InternalServiceErrorException() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
                .ingressVpcConfiguration(IngressVpcConfiguration.builder().vpcId(VPC_ID).vpcEndpointId(VPC_ENDPOINT_ID).build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        when(proxyClient.client().updateVpcIngressConnection(any(UpdateVpcIngressConnectionRequest.class)))
                .thenThrow(InternalServiceErrorException.class);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }
}
