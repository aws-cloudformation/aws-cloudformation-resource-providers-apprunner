package software.amazon.apprunner.service;

import software.amazon.awssdk.services.apprunner.model.EgressConfiguration;
import software.amazon.awssdk.services.apprunner.model.EgressType;
import software.amazon.awssdk.services.apprunner.model.IngressConfiguration;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceObservabilityConfiguration;
import software.amazon.awssdk.services.apprunner.model.ServiceSummary;
import software.amazon.awssdk.services.apprunner.model.NetworkConfiguration;

import static software.amazon.apprunner.service.Translator.translateFromNetworkConfiguration;

import static software.amazon.apprunner.service.Translator.translateFromServiceObservabilityConfiguration;

public class TestData {

    public static final String IMAGE_REPOSITORY_TYPE_ECR = "ECR";
    public static final String IMAGE_REPOSITORY_TYPE_INVALID_REPO = "INVALID_REPO_TYPE";
    public static final String IMAGE_IDENTIFIER = "1111111111.dkr.ecr.us-east-1.amazonaws.com/python-test:test";
    public static final String ACCESS_ROLE = "ACCESS_ROLE";
    public static final boolean AUTO_DEPLOYMENTS_ENABLED = true;
    public static final boolean OBSERVABILITY_DISABLED = false;
    public static final boolean OBSERVABILITY_ENABLED = true;
    public static final String CPU = "256";
    public static final String MEMORY = "512";
    public static final String OBSERVABILITY_CONFIGURATION_ARN = "OBSERVABILITY_CONFIGURATION_ARN";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SERVICE_ARN = "SERVICE_ARN";
    public static final String SERVICE_URL = "SERVICE_URL";
    public static final String SERVICE_STATUS_RUNNING = "RUNNING";
    public static final String SERVICE_STATUS_OPERATION_IN_PROGRESS = "OPERATION_IN_PROGRESS";
    public static final String SERVICE_STATUS_CREATE_FAILED = "CREATE_FAILED";
    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String CLIENT_TOKEN = "CLIENT TOKEN";
    public static final String LOGICAL_RESOURCE_IDENTIFIER = "LogicalResourceIdentifier";
    public static final String VPC_CONNECTOR_ARN = "VPC_CONNECTOR_ARN";

    public static final ImageRepository IMAGE_REPOSITORY = ImageRepository.builder()
            .imageRepositoryType(IMAGE_REPOSITORY_TYPE_ECR)
            .imageIdentifier(IMAGE_IDENTIFIER)
            .build();
    public static final AuthenticationConfiguration AUTHENTICATION_CONFIGURATION = AuthenticationConfiguration.builder()
            .accessRoleArn(ACCESS_ROLE)
            .build();
    public static final SourceConfiguration SOURCE_CONFIGURATION = SourceConfiguration.builder()
            .imageRepository(IMAGE_REPOSITORY)
            .autoDeploymentsEnabled(AUTO_DEPLOYMENTS_ENABLED)
            .authenticationConfiguration(AUTHENTICATION_CONFIGURATION)
            .build();
    public static final InstanceConfiguration INSTANCE_CONFIGURATION = InstanceConfiguration.builder()
            .cpu(CPU)
            .memory(MEMORY)
            .build();
    public static final NetworkConfiguration NETWORK_CONFIGURATION = NetworkConfiguration.builder()
            .egressConfiguration(EgressConfiguration.builder()
                    .egressType(EgressType.VPC.name())
                    .vpcConnectorArn(VPC_CONNECTOR_ARN)
                    .build())
            .ingressConfiguration(IngressConfiguration.builder()
                    .isPubliclyAccessible(true)
                    .build())
            .build();
    public static final NetworkConfiguration NETWORK_CONFIGURATION_PRIVATE = NetworkConfiguration.builder()
            .ingressConfiguration(IngressConfiguration.builder()
                    .isPubliclyAccessible(false)
                    .build())
            .build();
    public static final ServiceObservabilityConfiguration SERVICE_OBSERVABILITY_CONFIGURATION_DISABLED = ServiceObservabilityConfiguration.builder()
            .observabilityEnabled(OBSERVABILITY_DISABLED)
            .build();
    public static final ServiceObservabilityConfiguration SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED = ServiceObservabilityConfiguration.builder()
            .observabilityEnabled(OBSERVABILITY_ENABLED)
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .build();
    public static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .serviceName(SERVICE_NAME)
            .serviceArn(SERVICE_ARN)
            .serviceId(SERVICE_ID)
            .serviceUrl(SERVICE_URL)
            .sourceConfiguration(SOURCE_CONFIGURATION)
            .instanceConfiguration(INSTANCE_CONFIGURATION)
            .networkConfiguration(translateFromNetworkConfiguration(NETWORK_CONFIGURATION))
            .observabilityConfiguration(translateFromServiceObservabilityConfiguration(SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED))
            .build();
    public static final Service SERVICE = Service.builder()
            .serviceName(SERVICE_NAME)
            .serviceArn(SERVICE_ARN)
            .serviceUrl(SERVICE_URL)
            .serviceId(SERVICE_ID)
            .status(SERVICE_STATUS_RUNNING)
            .networkConfiguration(NETWORK_CONFIGURATION)
            .observabilityConfiguration(SERVICE_OBSERVABILITY_CONFIGURATION_ENABLED)
            .build();
    public static final ServiceSummary SERVICE_SUMMARY = ServiceSummary.builder()
            .serviceArn(SERVICE_ARN)
            .serviceId(SERVICE_ID)
            .serviceName(SERVICE_NAME)
            .serviceUrl(SERVICE_URL)
            .status(SERVICE_STATUS_RUNNING)
            .build();
}
