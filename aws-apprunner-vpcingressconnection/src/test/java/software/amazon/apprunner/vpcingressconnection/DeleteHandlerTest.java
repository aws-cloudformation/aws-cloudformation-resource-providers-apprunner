package software.amazon.apprunner.vpcingressconnection;

import java.time.Duration;

import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.ResourceNotFoundException;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.apprunner.vpcingressconnection.TestData.RESOURCE_MODEL_PENDING_DELETION;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_ARN;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_STATUS_DELETED;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_STATUS_PENDING_DELETION;


@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

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
        }
        else {
            verify(apprunnerClient, atLeastOnce()).deleteVpcIngressConnection(any(DeleteVpcIngressConnectionRequest.class));
        }

        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_Success_OperationInProgress() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(RESOURCE_MODEL_PENDING_DELETION)
            .build();

        final VpcIngressConnection vpcIngressConnectionWithDeleteInProgress = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_DELETION)
                .build();
        final DeleteVpcIngressConnectionResponse deleteVpcIngressConnectionResponse = DeleteVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnectionWithDeleteInProgress)
                .build();

        when(proxyClient.client().deleteVpcIngressConnection(any(DeleteVpcIngressConnectionRequest.class)))
                .thenReturn(deleteVpcIngressConnectionResponse);

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
    public void handleRequest_Success_Deleted() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL_PENDING_DELETION)
                .build();

        final VpcIngressConnection vpcIngressConnectionWithDeletedStatus = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_DELETED)
                .build();
        final DeleteVpcIngressConnectionResponse deleteVpcIngressConnectionResponse = DeleteVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnectionWithDeletedStatus)
                .build();

        when(proxyClient.client().deleteVpcIngressConnection(any(DeleteVpcIngressConnectionRequest.class)))
                .thenReturn(deleteVpcIngressConnectionResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_DeletedInCallback() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL_PENDING_DELETION)
                .build();

        final VpcIngressConnection vpcIngressConnectionWithDeletedStatus = VpcIngressConnection.builder()
                .status(VPC_INGRESS_CONNECTION_STATUS_DELETED)
                .build();
        final DescribeVpcIngressConnectionResponse describeVpcIngressConnectionResponse = DescribeVpcIngressConnectionResponse.builder()
                .vpcIngressConnection(vpcIngressConnectionWithDeletedStatus)
                .build();

        when(proxyClient.client().describeVpcIngressConnection(any(DescribeVpcIngressConnectionRequest.class)))
                .thenReturn(describeVpcIngressConnectionResponse);

        withCallback = true;
        final CallbackContext callbackContext =  new CallbackContext();
        callbackContext.setDeleteStarted(true);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        when(proxyClient.client().deleteVpcIngressConnection(any(DeleteVpcIngressConnectionRequest.class)))
                .thenThrow(ResourceNotFoundException.class);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(null);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_InternalServiceErrorException() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder().vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();

        when(proxyClient.client().deleteVpcIngressConnection(any(DeleteVpcIngressConnectionRequest.class)))
                .thenThrow(InternalServiceErrorException.class);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(null);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }



}
