package software.amazon.apprunner.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.DeleteServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteServiceResponse;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.InternalServiceErrorException;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceStatus;
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
import static software.amazon.apprunner.service.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.service.TestData.SERVICE_ARN;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_OPERATION_IN_PROGRESS;

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
            verify(apprunnerClient, atLeastOnce()).describeService(any(DescribeServiceRequest.class));
        } else {
            verify(apprunnerClient, atLeastOnce()).deleteService(any(DeleteServiceRequest.class));
        }
        verifyNoMoreInteractions(apprunnerClient);
    }

    @Test
    public void handleRequest_Success_OperationInProgress() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .build();

        final Service serviceWithDeleteInProgress = Service.builder()
                .status(SERVICE_STATUS_OPERATION_IN_PROGRESS)
                .build();
        final DeleteServiceResponse deleteServiceResponse = DeleteServiceResponse.builder()
                .service(serviceWithDeleteInProgress)
                .build();
        when(proxyClient.client().deleteService(any(DeleteServiceRequest.class)))
                .thenReturn(deleteServiceResponse);

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
                .desiredResourceState(RESOURCE_MODEL)
                .build();

        final Service serviceWithDeletedStatus = Service.builder()
                .status(ServiceStatus.DELETED)
                .build();
        final DeleteServiceResponse deleteServiceResponse = DeleteServiceResponse.builder()
                .service(serviceWithDeletedStatus)
                .build();
        when(proxyClient.client().deleteService(any(DeleteServiceRequest.class)))
                .thenReturn(deleteServiceResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(null);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_DeletedInCallback() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .build();

        final Service serviceWithDeletedStatus = Service.builder()
                .status(ServiceStatus.DELETED)
                .build();
        final DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder()
                .service(serviceWithDeletedStatus)
                .build();
        when(proxyClient.client().describeService(any(DescribeServiceRequest.class)))
                .thenReturn(describeServiceResponse);

        withCallback = true;
        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setDeleteStarted(true);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(null);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Exception_InternalServiceErrorException() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder().serviceArn(SERVICE_ARN).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(model)
                .build();
        when(proxyClient.client().deleteService(any(DeleteServiceRequest.class)))
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
