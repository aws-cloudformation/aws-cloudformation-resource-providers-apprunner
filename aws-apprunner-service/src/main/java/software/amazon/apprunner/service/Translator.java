package software.amazon.apprunner.service;

import software.amazon.awssdk.services.apprunner.model.CreateServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteServiceRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeServiceRequest;
import software.amazon.awssdk.services.apprunner.model.EgressType;
import software.amazon.awssdk.services.apprunner.model.ListServicesRequest;
import software.amazon.awssdk.services.apprunner.model.Service;
import software.amazon.awssdk.services.apprunner.model.ServiceSummary;
import software.amazon.awssdk.services.apprunner.model.UpdateServiceRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static software.amazon.apprunner.service.TagHelper.convertToList;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

    /**
     * Request to create a AppRunner Service resource
     * @param model AppRunner Service resource model
     * @return createServiceRequest the request to create AppRunner Service resource
     */
    static CreateServiceRequest translateToCreateServiceRequest(final ResourceModel model, final Map<String, String> tags) {
        return CreateServiceRequest.builder()
                .serviceName(model.getServiceName())
                .sourceConfiguration(translateToSourceConfiguration(model.getSourceConfiguration()))
                .instanceConfiguration(translateToInstanceConfiguration(model.getInstanceConfiguration()))
                .encryptionConfiguration(translateToEncryptionConfiguration(model.getEncryptionConfiguration()))
                .healthCheckConfiguration(translateToHealthCheckConfiguration(model.getHealthCheckConfiguration()))
                .networkConfiguration(translateToNetworkConfigurationForCreate(model.getNetworkConfiguration()))
                .observabilityConfiguration(translateToServiceObservabilityConfiguration(model.getObservabilityConfiguration()))
                .autoScalingConfigurationArn(model.getAutoScalingConfigurationArn())
                .tags(convertToList(tags))
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.SourceConfiguration
    translateToSourceConfiguration(final SourceConfiguration sourceConfigurationModel) {
        software.amazon.awssdk.services.apprunner.model.AuthenticationConfiguration authenticationConfiguration
                = sourceConfigurationModel != null
                ? translateToAuthenticationConfiguration(sourceConfigurationModel.getAuthenticationConfiguration())
                : null;

        software.amazon.awssdk.services.apprunner.model.CodeRepository codeRepository
                = sourceConfigurationModel != null
                ? translateToCodeRepository(sourceConfigurationModel.getCodeRepository())
                : null;

        software.amazon.awssdk.services.apprunner.model.ImageRepository imageRepository
                = sourceConfigurationModel != null
                ? translateToImageRepository(sourceConfigurationModel.getImageRepository())
                : null;

        boolean autoDeploymentEnabled = sourceConfigurationModel != null
                && sourceConfigurationModel.getAutoDeploymentsEnabled() != null
                && sourceConfigurationModel.getAutoDeploymentsEnabled();

        return software.amazon.awssdk.services.apprunner.model.SourceConfiguration.builder()
                .codeRepository(codeRepository)
                .imageRepository(imageRepository)
                .autoDeploymentsEnabled(autoDeploymentEnabled)
                .authenticationConfiguration(authenticationConfiguration)
                .build();
    }

    private static SourceConfiguration translateFromSourceConfiguration(
            final software.amazon.awssdk.services.apprunner.model.SourceConfiguration sourceConfiguration) {
        if (sourceConfiguration == null) {
            return null;
        }
        AuthenticationConfiguration authenticationConfigurationModel
                = sourceConfiguration != null
                ? translateFromAuthenticationConfiguration(sourceConfiguration.authenticationConfiguration())
                : null;

        CodeRepository codeRepositoryModel
                = sourceConfiguration != null
                ? translateFromCodeRepository(sourceConfiguration.codeRepository())
                : null;

        ImageRepository imageRepositoryModel
                = sourceConfiguration != null
                ? translateFromImageRepository(sourceConfiguration.imageRepository())
                : null;

        boolean autoDeploymentEnabledModel = sourceConfiguration != null
                && sourceConfiguration.autoDeploymentsEnabled() != null
                && sourceConfiguration.autoDeploymentsEnabled();

        return SourceConfiguration.builder()
                .codeRepository(codeRepositoryModel)
                .imageRepository(imageRepositoryModel)
                .autoDeploymentsEnabled(autoDeploymentEnabledModel)
                .authenticationConfiguration(authenticationConfigurationModel)
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.AuthenticationConfiguration
        translateToAuthenticationConfiguration(final AuthenticationConfiguration authenticationConfigurationModel) {
        return authenticationConfigurationModel == null ? null
                : software.amazon.awssdk.services.apprunner.model.AuthenticationConfiguration.builder()
                .connectionArn(authenticationConfigurationModel.getConnectionArn())
                .accessRoleArn(authenticationConfigurationModel.getAccessRoleArn())
                .build();
    }

    private static AuthenticationConfiguration translateFromAuthenticationConfiguration(
            final software.amazon.awssdk.services.apprunner.model.AuthenticationConfiguration
                    authenticationConfiguration) {
        return authenticationConfiguration == null ? null : AuthenticationConfiguration.builder()
                .accessRoleArn(authenticationConfiguration.accessRoleArn())
                .connectionArn(authenticationConfiguration.connectionArn())
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.ImageRepository translateToImageRepository(
            final ImageRepository imageRepositoryModel) {
        software.amazon.awssdk.services.apprunner.model.ImageRepository imageRepository = null;
        if (imageRepositoryModel != null) {
            ImageConfiguration imageConfigurationModel = imageRepositoryModel.getImageConfiguration();
            software.amazon.awssdk.services.apprunner.model.ImageConfiguration imageConfiguration
                    = software.amazon.awssdk.services.apprunner.model.ImageConfiguration.builder()
                    .startCommand(imageConfigurationModel == null ? null : imageConfigurationModel.getStartCommand())
                    .port(imageConfigurationModel == null ? null : imageConfigurationModel.getPort())
                    .runtimeEnvironmentVariables(imageConfigurationModel == null ? null
                            : translateToEnvironmentVariables(imageConfigurationModel.getRuntimeEnvironmentVariables()))
                    .build();
            imageRepository = software.amazon.awssdk.services.apprunner.model.ImageRepository.builder()
                    .imageIdentifier(imageRepositoryModel.getImageIdentifier())
                    .imageConfiguration(imageConfiguration)
                    .imageRepositoryType(imageRepositoryModel.getImageRepositoryType())
                    .build();
        }
        return imageRepository;
    }

    private static ImageRepository translateFromImageRepository(
            final software.amazon.awssdk.services.apprunner.model.ImageRepository imageRepository) {
        ImageRepository imageRepositoryModel = null;
        if (imageRepository != null) {
            software.amazon.awssdk.services.apprunner.model.ImageConfiguration imageConfiguration
                    = imageRepository.imageConfiguration();
            ImageConfiguration imageConfigurationModel = ImageConfiguration.builder()
                    .startCommand(imageConfiguration == null ? null : imageConfiguration.startCommand())
                    .port(imageConfiguration == null ? null : imageConfiguration.port())
                    .runtimeEnvironmentVariables(imageConfiguration == null ? null
                            : translateFromEnvironmentVariables(imageConfiguration.runtimeEnvironmentVariables()))
                    .build();
            imageRepositoryModel = ImageRepository.builder()
                    .imageIdentifier(imageRepository.imageIdentifier())
                    .imageConfiguration(imageConfigurationModel)
                    .imageRepositoryType(imageRepository.imageRepositoryType().name())
                    .build();
        }
        return imageRepositoryModel;
    }

    private static software.amazon.awssdk.services.apprunner.model.CodeRepository translateToCodeRepository(
            final CodeRepository codeRepositoryModel) {
        software.amazon.awssdk.services.apprunner.model.CodeRepository codeRepository = null;
        if (codeRepositoryModel != null) {
            software.amazon.awssdk.services.apprunner.model.SourceCodeVersion sourceCodeVersion
                    = codeRepositoryModel.getSourceCodeVersion() == null ? null
                    : translateToSourceCodeVersion(codeRepositoryModel.getSourceCodeVersion());
            software.amazon.awssdk.services.apprunner.model.CodeConfiguration codeConfiguration
                    = codeRepositoryModel.getCodeConfiguration() == null ? null
                    : translateToCodeConfiguration(codeRepositoryModel.getCodeConfiguration());
            codeRepository = software.amazon.awssdk.services.apprunner.model.CodeRepository.builder()
                    .repositoryUrl(codeRepositoryModel.getRepositoryUrl())
                    .sourceCodeVersion(sourceCodeVersion)
                    .codeConfiguration(codeConfiguration)
                    .build();
        }
        return codeRepository;
    }

    private static CodeRepository translateFromCodeRepository(
            final software.amazon.awssdk.services.apprunner.model.CodeRepository codeRepository) {
        CodeRepository codeRepositoryModel = null;
        if (codeRepository != null) {
            SourceCodeVersion sourceCodeVersionModel
                    = codeRepository.sourceCodeVersion() == null ? null
                    : translateFromSourceCodeVersion(codeRepository.sourceCodeVersion());
            CodeConfiguration codeConfigurationModel
                    = codeRepository.codeConfiguration() == null ? null
                    : translateFromCodeConfiguration(codeRepository.codeConfiguration());
            codeRepositoryModel = CodeRepository.builder()
                    .repositoryUrl(codeRepository.repositoryUrl())
                    .sourceCodeVersion(sourceCodeVersionModel)
                    .codeConfiguration(codeConfigurationModel)
                    .build();
        }
        return codeRepositoryModel;
    }

    private static software.amazon.awssdk.services.apprunner.model.CodeConfiguration translateToCodeConfiguration(
            final CodeConfiguration codeConfigurationModel) {
        if (codeConfigurationModel == null) {
            return null;
        }
        final software.amazon.awssdk.services.apprunner.model.CodeConfigurationValues codeConfigurationValues
                = translateToCodeConfigurationValues(codeConfigurationModel.getCodeConfigurationValues());
        return software.amazon.awssdk.services.apprunner.model.CodeConfiguration.builder()
                .configurationSource(codeConfigurationModel.getConfigurationSource())
                .codeConfigurationValues(codeConfigurationValues)
                .build();
    }

    private static CodeConfiguration translateFromCodeConfiguration(
            final software.amazon.awssdk.services.apprunner.model.CodeConfiguration codeConfiguration) {
        if (codeConfiguration == null) {
            return null;
        }
        final CodeConfigurationValues codeConfigurationValuesModel
                = translateFromCodeConfigurationValues(codeConfiguration.codeConfigurationValues());
        return CodeConfiguration.builder()
                .configurationSource(codeConfiguration.configurationSource().name())
                .codeConfigurationValues(codeConfigurationValuesModel)
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.CodeConfigurationValues
        translateToCodeConfigurationValues(final CodeConfigurationValues codeConfigurationValuesModel) {
        if (codeConfigurationValuesModel == null) {
            return null;
        }
        final Map<String, String> runTimeEnvironmentVariables
                = translateToEnvironmentVariables(codeConfigurationValuesModel.getRuntimeEnvironmentVariables());
        return software.amazon.awssdk.services.apprunner.model.CodeConfigurationValues.builder()
                .runtime(codeConfigurationValuesModel.getRuntime())
                .buildCommand(codeConfigurationValuesModel.getBuildCommand())
                .startCommand(codeConfigurationValuesModel.getStartCommand())
                .port(codeConfigurationValuesModel.getPort())
                .runtimeEnvironmentVariables(runTimeEnvironmentVariables)
                .build();
    }

    private static CodeConfigurationValues translateFromCodeConfigurationValues(
            final software.amazon.awssdk.services.apprunner.model.CodeConfigurationValues codeConfigurationValues) {
        if (codeConfigurationValues == null) {
            return null;
        }
        final List<KeyValuePair> runTimeEnvironmentVariables
                = translateFromEnvironmentVariables(codeConfigurationValues.runtimeEnvironmentVariables());
        return CodeConfigurationValues.builder()
                .runtime(codeConfigurationValues.runtime().name())
                .buildCommand(codeConfigurationValues.buildCommand())
                .startCommand(codeConfigurationValues.startCommand())
                .port(codeConfigurationValues.port())
                .runtimeEnvironmentVariables(runTimeEnvironmentVariables)
                .build();
    }

    private static Map<String, String> translateToEnvironmentVariables(
            final List<KeyValuePair> runtimeEnvironmentVariables) {
        return runtimeEnvironmentVariables == null ? null : runtimeEnvironmentVariables.stream()
                .collect(Collectors.toMap(KeyValuePair::getName, KeyValuePair::getValue));
    }

    private static List<KeyValuePair> translateFromEnvironmentVariables(
            final Map<String, String> runtimeEnvironmentVariables) {
        return runtimeEnvironmentVariables == null || runtimeEnvironmentVariables.size() == 0
                ? null
                : runtimeEnvironmentVariables.keySet().stream()
                .map(key -> new KeyValuePair(key, runtimeEnvironmentVariables.get(key)))
                .collect(Collectors.toList());
    }

    private static software.amazon.awssdk.services.apprunner.model.SourceCodeVersion translateToSourceCodeVersion(
            final SourceCodeVersion sourceCodeVersion) {
        return sourceCodeVersion == null ? null
                : software.amazon.awssdk.services.apprunner.model.SourceCodeVersion.builder()
                .type(sourceCodeVersion.getType())
                .value(sourceCodeVersion.getValue())
                .build();
    }

    private static SourceCodeVersion translateFromSourceCodeVersion(
            final software.amazon.awssdk.services.apprunner.model.SourceCodeVersion sourceCodeVersion) {
        return sourceCodeVersion == null ? null
                : SourceCodeVersion.builder()
                .type(sourceCodeVersion.type().name())
                .value(sourceCodeVersion.value())
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.InstanceConfiguration
        translateToInstanceConfiguration(final InstanceConfiguration instanceConfigurationModel) {
        return instanceConfigurationModel == null ? null
                : software.amazon.awssdk.services.apprunner.model.InstanceConfiguration.builder()
                .cpu(instanceConfigurationModel.getCpu())
                .memory(instanceConfigurationModel.getMemory())
                .instanceRoleArn(instanceConfigurationModel.getInstanceRoleArn())
                .build();
    }

    private static InstanceConfiguration translateFromInstanceConfiguration(
            final software.amazon.awssdk.services.apprunner.model.InstanceConfiguration instanceConfiguration) {
        return instanceConfiguration == null ? null
                : InstanceConfiguration.builder()
                .cpu(instanceConfiguration.cpu())
                .memory(instanceConfiguration.memory())
                .instanceRoleArn(instanceConfiguration.instanceRoleArn())
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.EncryptionConfiguration
        translateToEncryptionConfiguration(final EncryptionConfiguration encryptionConfigurationModel) {
        return encryptionConfigurationModel != null
                ? software.amazon.awssdk.services.apprunner.model.EncryptionConfiguration.builder()
                .kmsKey(encryptionConfigurationModel.getKmsKey())
                .build()
                : null;
    }

    private static EncryptionConfiguration translateFromEncryptionConfiguration(
            final software.amazon.awssdk.services.apprunner.model.EncryptionConfiguration encryptionConfiguration) {
        return encryptionConfiguration != null
                ? EncryptionConfiguration.builder().kmsKey(encryptionConfiguration.kmsKey()).build()
                : null;
    }

    private static software.amazon.awssdk.services.apprunner.model.HealthCheckConfiguration
        translateToHealthCheckConfiguration(final HealthCheckConfiguration healthCheckConfiguration) {
        return healthCheckConfiguration != null
                ? software.amazon.awssdk.services.apprunner.model.HealthCheckConfiguration.builder()
                .protocol(healthCheckConfiguration.getProtocol())
                .path(healthCheckConfiguration.getPath())
                .interval(healthCheckConfiguration.getInterval())
                .timeout(healthCheckConfiguration.getTimeout())
                .healthyThreshold(healthCheckConfiguration.getHealthyThreshold())
                .unhealthyThreshold(healthCheckConfiguration.getUnhealthyThreshold())
                .build()
                : null;
    }

    private static HealthCheckConfiguration translateFromHealthCheckConfiguration(
            final software.amazon.awssdk.services.apprunner.model.HealthCheckConfiguration healthCheckConfiguration) {
        return healthCheckConfiguration != null
                ? HealthCheckConfiguration.builder()
                .protocol(healthCheckConfiguration.protocol().name())
                .path(healthCheckConfiguration.path())
                .interval(healthCheckConfiguration.interval())
                .timeout(healthCheckConfiguration.timeout())
                .healthyThreshold(healthCheckConfiguration.healthyThreshold())
                .unhealthyThreshold(healthCheckConfiguration.unhealthyThreshold())
                .build()
                : null;
    }

    static software.amazon.awssdk.services.apprunner.model.NetworkConfiguration translateToNetworkConfigurationForCreate(
            final NetworkConfiguration networkConfigurationModel) {
        return networkConfigurationModel == null ? null
                : software.amazon.awssdk.services.apprunner.model.NetworkConfiguration.builder()
                .egressConfiguration(translateToEgressConfiguration(networkConfigurationModel.getEgressConfiguration()))
                .ingressConfiguration(translateToIngressConfiguration(networkConfigurationModel.getIngressConfiguration()))
                .build();
    }

    static software.amazon.awssdk.services.apprunner.model.NetworkConfiguration translateToNetworkConfigurationForUpdate(
            final NetworkConfiguration networkConfigurationModel) {
        if (networkConfigurationModel == null) {
            // Null NetworkConfiguration in CFN means we should set the customer's service to DEFAULT (public egress and public ingress).
            return software.amazon.awssdk.services.apprunner.model.NetworkConfiguration.builder()
                    .egressConfiguration(software.amazon.awssdk.services.apprunner.model.EgressConfiguration.builder()
                            .egressType(EgressType.DEFAULT)
                            .build())
                    .ingressConfiguration(software.amazon.awssdk.services.apprunner.model.IngressConfiguration.builder()
                            .isPubliclyAccessible(Boolean.TRUE)
                            .build())
                    .build();
        }
        return software.amazon.awssdk.services.apprunner.model.NetworkConfiguration.builder()
                .egressConfiguration(translateToEgressConfiguration(networkConfigurationModel.getEgressConfiguration()))
                .ingressConfiguration(translateToIngressConfiguration(networkConfigurationModel.getIngressConfiguration()))
                .build();
    }

    static NetworkConfiguration translateFromNetworkConfiguration(
            final software.amazon.awssdk.services.apprunner.model.NetworkConfiguration networkConfiguration) {
        return networkConfiguration == null ? null
                : NetworkConfiguration.builder()
                .egressConfiguration(translateFromEgressConfiguration(networkConfiguration.egressConfiguration()))
                .ingressConfiguration(translateFromIngressConfiguration(networkConfiguration.ingressConfiguration()))
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.EgressConfiguration translateToEgressConfiguration(
            final EgressConfiguration egressConfiguration) {
        return egressConfiguration == null ? null
                : software.amazon.awssdk.services.apprunner.model.EgressConfiguration.builder()
                .egressType(egressConfiguration.getEgressType())
                .vpcConnectorArn(egressConfiguration.getVpcConnectorArn())
                .build();
    }

    private static EgressConfiguration translateFromEgressConfiguration(
            final software.amazon.awssdk.services.apprunner.model.EgressConfiguration egressConfiguration) {
        return egressConfiguration == null ? null
                : EgressConfiguration.builder()
                .egressType(egressConfiguration.egressType().name())
                .vpcConnectorArn(egressConfiguration.vpcConnectorArn())
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.IngressConfiguration translateToIngressConfiguration(
            final IngressConfiguration igressConfiguration) {
        return igressConfiguration == null ? null
                : software.amazon.awssdk.services.apprunner.model.IngressConfiguration.builder()
                .isPubliclyAccessible(igressConfiguration.getIsPubliclyAccessible())
                .build();
    }

    private static IngressConfiguration translateFromIngressConfiguration(
            final software.amazon.awssdk.services.apprunner.model.IngressConfiguration ingressConfiguration) {
        return ingressConfiguration == null ? null
                : IngressConfiguration.builder()
                .isPubliclyAccessible(ingressConfiguration.isPubliclyAccessible())
                .build();
    }

    static ServiceObservabilityConfiguration translateFromServiceObservabilityConfiguration(
            final software.amazon.awssdk.services.apprunner.model.ServiceObservabilityConfiguration serviceObservabilityConfiguration) {
        return serviceObservabilityConfiguration == null ? null
                : ServiceObservabilityConfiguration.builder()
                .observabilityEnabled(serviceObservabilityConfiguration.observabilityEnabled())
                .observabilityConfigurationArn(serviceObservabilityConfiguration.observabilityConfigurationArn())
                .build();
    }

    private static software.amazon.awssdk.services.apprunner.model.ServiceObservabilityConfiguration translateToServiceObservabilityConfiguration(
            final ServiceObservabilityConfiguration serviceObservabilityConfiguration) {
        return serviceObservabilityConfiguration == null
                ? software.amazon.awssdk.services.apprunner.model.ServiceObservabilityConfiguration.builder()
                .observabilityEnabled(false)
                .build()
                : software.amazon.awssdk.services.apprunner.model.ServiceObservabilityConfiguration.builder()
                .observabilityEnabled(serviceObservabilityConfiguration.getObservabilityEnabled())
                .observabilityConfigurationArn(serviceObservabilityConfiguration.getObservabilityConfigurationArn())
                .build();
    }

    /**
     * Request to list the app runner services
     * @param nextToken next token
     * @return listServicesRequest the request to list the app runner services
     */
    static ListServicesRequest translateToListServicesRequest(final String nextToken) {
        return ListServicesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    /**
     * Converts the service summary type to resource model type
     * @param serviceSummary service summary
     * @return resourceModel the cloudformation resource model type
     */
    static ResourceModel translateServiceSummaryToResourceModel(final ServiceSummary serviceSummary) {
        return ResourceModel.builder()
                .serviceArn(serviceSummary.serviceArn())
                .serviceName(serviceSummary.serviceName())
                .serviceId(serviceSummary.serviceId())
                .serviceUrl(serviceSummary.serviceUrl())
                .status(serviceSummary.statusAsString())
                .build();
    }

    /**
     * Request to describe a app runner service resource
     * @param model resource model
     * @return describeServiceRequest the request to describe a app runner service resource
     */
    static DescribeServiceRequest translateToDescribeServiceRequest(final ResourceModel model) {
        return DescribeServiceRequest.builder()
                .serviceArn(model.getServiceArn())
                .build();
    }

    /**
     * Translates app runner service response object to resource model type
     * @param service the service resource
     * @return model  resource model
     */
    static ResourceModel translateServiceFromResponse(final Service service) {
        if (service == null) {
            return null;
        }
        return ResourceModel.builder()
                .serviceArn(service.serviceArn())
                .serviceName(service.serviceName())
                .serviceArn(service.serviceArn())
                .serviceId(service.serviceId())
                .serviceUrl(service.serviceUrl())
                .status(service.status().toString())
                .sourceConfiguration(translateFromSourceConfiguration(service.sourceConfiguration()))
                .healthCheckConfiguration(translateFromHealthCheckConfiguration(
                        service.healthCheckConfiguration()))
                .autoScalingConfigurationArn(service.autoScalingConfigurationSummary() != null
                                ? service.autoScalingConfigurationSummary()
                                .autoScalingConfigurationArn()
                                :null)
                .instanceConfiguration(translateFromInstanceConfiguration(service.instanceConfiguration()))
                .encryptionConfiguration(translateFromEncryptionConfiguration(service.encryptionConfiguration()))
                .networkConfiguration(translateFromNetworkConfiguration(service.networkConfiguration()))
                .observabilityConfiguration(translateFromServiceObservabilityConfiguration(service.observabilityConfiguration()))
                .build();
    }

    /**
     * Request to update a app runner service resource
     * @param model resource model
     * @return updateServiceRequest the request to update a app runner service resource
     */
    public static UpdateServiceRequest translateToUpdateServiceRequest(ResourceModel model) {
        return UpdateServiceRequest.builder()
                .serviceArn(model.getServiceArn())
                .instanceConfiguration(translateToInstanceConfiguration(model.getInstanceConfiguration()))
                .sourceConfiguration(translateToSourceConfiguration(model.getSourceConfiguration()))
                .healthCheckConfiguration(translateToHealthCheckConfiguration(model.getHealthCheckConfiguration()))
                .networkConfiguration(translateToNetworkConfigurationForUpdate(model.getNetworkConfiguration()))
                .observabilityConfiguration(translateToServiceObservabilityConfiguration(model.getObservabilityConfiguration()))
                .autoScalingConfigurationArn(model.getAutoScalingConfigurationArn())
                .build();
    }

    /**
     * Request to delete a app runner service resource
     * @param model resource model
     * @return deleteServiceRequest the request to delete a app runner service resource
     */
    static DeleteServiceRequest translateToDeleteServiceRequest(ResourceModel model) {
        return DeleteServiceRequest.builder()
                .serviceArn(model.getServiceArn())
                .build();
    }
}
