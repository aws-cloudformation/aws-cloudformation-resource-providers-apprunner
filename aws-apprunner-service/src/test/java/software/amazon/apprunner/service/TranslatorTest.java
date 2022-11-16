package software.amazon.apprunner.service;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.apprunner.model.CreateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceResponse;
import software.amazon.awssdk.services.apprunner.model.ImageRepositoryType;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.EgressType;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.apprunner.service.TestData.AUTHENTICATION_CONFIGURATION;
import static software.amazon.apprunner.service.TestData.AUTO_DEPLOYMENTS_ENABLED;
import static software.amazon.apprunner.service.TestData.IMAGE_IDENTIFIER;
import static software.amazon.apprunner.service.TestData.IMAGE_REPOSITORY_TYPE_INVALID_REPO;
import static software.amazon.apprunner.service.TestData.INSTANCE_CONFIGURATION;
import static software.amazon.apprunner.service.TestData.NETWORK_CONFIGURATION;
import static software.amazon.apprunner.service.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.service.TestData.SERVICE;
import static software.amazon.apprunner.service.TestData.SERVICE_ARN;
import static software.amazon.apprunner.service.TestData.SERVICE_ID;
import static software.amazon.apprunner.service.TestData.SERVICE_NAME;
import static software.amazon.apprunner.service.TestData.SERVICE_STATUS_RUNNING;
import static software.amazon.apprunner.service.TestData.SERVICE_URL;

import static software.amazon.apprunner.service.TestData.SOURCE_CONFIGURATION;
import static software.amazon.apprunner.service.Translator.translateServiceFromResponse;
import static software.amazon.apprunner.service.Translator.translateToCreateServiceRequest;
import static software.amazon.apprunner.service.Translator.translateToDescribeServiceRequest;
import static software.amazon.apprunner.service.Translator.translateToUpdateServiceRequest;

public class TranslatorTest {

    private static final String TAG_KEY = "key";
    private static final String TAG_VALUE = "value";

    @Test
    public void translateToCreateServiceRequest_ECR_success() {

        final Map<String, String> tagsMap = Collections.singletonMap(TAG_KEY, TAG_VALUE);
        final CreateServiceRequest createServiceRequest = translateToCreateServiceRequest(RESOURCE_MODEL, tagsMap);

        assertThat(createServiceRequest).isNotNull();
        assertThat(createServiceRequest.serviceName()).isNotNull();
        assertThat(createServiceRequest.sourceConfiguration()).isNotNull();
        assertThat(createServiceRequest.instanceConfiguration()).isNotNull();
        assertThat(createServiceRequest.networkConfiguration()).isNotNull();
        assertThat(createServiceRequest.networkConfiguration().ingressConfiguration()).isNotNull();
        assertThat(createServiceRequest.networkConfiguration().egressConfiguration()).isNotNull();
        assertThat(createServiceRequest.sourceConfiguration().imageRepository().imageRepositoryType())
                .isEqualTo(ImageRepositoryType.ECR);
        assertThat(createServiceRequest.tags()).contains(
                software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(TAG_KEY)
                        .value(TAG_VALUE)
                        .build());
        assertThat(createServiceRequest.observabilityConfiguration()).isNotNull();
    }

    @Test
    public void translateToCreateServiceRequest_ECR_InvalidRepoType() {

        final ImageRepository imageRepository = ImageRepository.builder()
                .imageRepositoryType(IMAGE_REPOSITORY_TYPE_INVALID_REPO)
                .imageIdentifier(IMAGE_IDENTIFIER)
                .build();
        final SourceConfiguration SOURCE_CONFIGURATION = SourceConfiguration.builder()
                .imageRepository(imageRepository)
                .autoDeploymentsEnabled(AUTO_DEPLOYMENTS_ENABLED)
                .authenticationConfiguration(AUTHENTICATION_CONFIGURATION)
                .build();

        final ResourceModel resourceModel = ResourceModel.builder()
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .sourceConfiguration(SOURCE_CONFIGURATION)
                .instanceConfiguration(INSTANCE_CONFIGURATION)
                .build();

        final Map<String, String> tagsMap = Collections.singletonMap(TAG_KEY, TAG_VALUE);
        final CreateServiceRequest createServiceRequest = translateToCreateServiceRequest(resourceModel, tagsMap);

        assertThat(createServiceRequest).isNotNull();
        assertThat(createServiceRequest.sourceConfiguration().imageRepository().imageRepositoryType())
                .isEqualTo(ImageRepositoryType.UNKNOWN_TO_SDK_VERSION);
    }

    @Test
    public void translateToDescribeServiceRequest_success() {

        final DescribeServiceRequest describeServiceRequest
                = translateToDescribeServiceRequest(RESOURCE_MODEL);

        assertThat(describeServiceRequest).isNotNull();
        assertThat(describeServiceRequest.serviceArn()).isEqualTo(SERVICE_ARN);
    }

    @Test
    public void translateFromDescribeServiceResponse_success() {
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder()
                .service(SERVICE)
                .build();

        final ResourceModel resourceModel = translateServiceFromResponse(describeServiceResponse.service());

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel.getServiceArn()).isEqualTo(SERVICE_ARN);
        assertThat(resourceModel.getServiceName()).isEqualTo(SERVICE_NAME);
        assertThat(resourceModel.getServiceId()).isEqualTo(SERVICE_ID);
        assertThat(resourceModel.getServiceUrl()).isEqualTo(SERVICE_URL);
        assertThat(resourceModel.getStatus()).isEqualTo(SERVICE_STATUS_RUNNING);
        assertThat(resourceModel.getNetworkConfiguration()).isEqualTo(RESOURCE_MODEL.getNetworkConfiguration());
        assertThat(resourceModel.getNetworkConfiguration().getIngressConfiguration()).isEqualTo(RESOURCE_MODEL.getNetworkConfiguration().getIngressConfiguration());
        assertThat(resourceModel.getObservabilityConfiguration()).isEqualTo(RESOURCE_MODEL.getObservabilityConfiguration());
    }

    @Test
    public void translateFromDescribeServiceResponse_nullResponse() {
        DescribeServiceResponse describeServiceResponse = DescribeServiceResponse.builder().build();
        final ResourceModel resourceModel = translateServiceFromResponse(describeServiceResponse.service());
        assertThat(resourceModel).isNull();
    }

    @Test
    public void translateToUpdateServiceRequest_Success() {

        final UpdateServiceRequest updateServiceRequest = translateToUpdateServiceRequest(RESOURCE_MODEL);

        assertThat(updateServiceRequest).isNotNull();
        assertThat(updateServiceRequest.serviceArn()).isNotNull();
        assertThat(updateServiceRequest.sourceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.instanceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration().egressConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration().ingressConfiguration()).isNotNull();
        assertThat(updateServiceRequest.sourceConfiguration().imageRepository().imageRepositoryType())
                .isEqualTo(ImageRepositoryType.ECR);
    }

    @Test
    public void translateToUpdateServiceRequest_NullNetworkConfiguration() {

        final ResourceModel resourceModel = ResourceModel.builder()
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .serviceId(SERVICE_ID)
                .serviceUrl(SERVICE_URL)
                .sourceConfiguration(SOURCE_CONFIGURATION)
                .instanceConfiguration(INSTANCE_CONFIGURATION)
                .build();

        final UpdateServiceRequest updateServiceRequest = translateToUpdateServiceRequest(resourceModel);

        assertThat(updateServiceRequest).isNotNull();
        assertThat(updateServiceRequest.serviceArn()).isNotNull();
        assertThat(updateServiceRequest.sourceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.instanceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration().egressConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration().egressConfiguration().egressType()).isEqualTo(EgressType.DEFAULT);
        assertThat(updateServiceRequest.networkConfiguration().ingressConfiguration()).isNotNull();
        assertThat(updateServiceRequest.networkConfiguration().ingressConfiguration().isPubliclyAccessible()).isEqualTo(Boolean.TRUE);
        assertThat(updateServiceRequest.sourceConfiguration().imageRepository().imageRepositoryType())
                .isEqualTo(ImageRepositoryType.ECR);
    }

    @Test
    public void translateToUpdateServiceRequest_NullObservabilityConfiguration() {

        final ResourceModel resourceModel = ResourceModel.builder()
                .serviceName(SERVICE_NAME)
                .serviceArn(SERVICE_ARN)
                .serviceId(SERVICE_ID)
                .serviceUrl(SERVICE_URL)
                .sourceConfiguration(SOURCE_CONFIGURATION)
                .instanceConfiguration(INSTANCE_CONFIGURATION)
                .build();

        final UpdateServiceRequest updateServiceRequest = translateToUpdateServiceRequest(resourceModel);

        assertThat(updateServiceRequest).isNotNull();
        assertThat(updateServiceRequest.serviceArn()).isNotNull();
        assertThat(updateServiceRequest.sourceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.instanceConfiguration()).isNotNull();
        assertThat(updateServiceRequest.observabilityConfiguration()).isNotNull();
        assertThat(updateServiceRequest.observabilityConfiguration().observabilityEnabled()).isFalse();
        assertThat(updateServiceRequest.observabilityConfiguration().observabilityConfigurationArn()).isNull();
        assertThat(updateServiceRequest.sourceConfiguration().imageRepository().imageRepositoryType())
                .isEqualTo(ImageRepositoryType.ECR);
    }
}
