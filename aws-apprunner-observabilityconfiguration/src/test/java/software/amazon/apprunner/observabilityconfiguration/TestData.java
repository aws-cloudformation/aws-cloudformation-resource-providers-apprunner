package software.amazon.apprunner.observabilityconfiguration;

import software.amazon.awssdk.services.apprunner.model.ObservabilityConfiguration;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationStatus;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationSummary;

import java.util.Arrays;
import java.util.HashSet;

import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToTraceConfiguration;

public class TestData {

    public static final boolean IS_LATEST = true;
    public static final boolean IS_NOT_LATEST = false;
    public static final Integer OBSERVABILITY_CONFIGURATION_REVISION = 1;
    public static final String NEXT_TOKEN = "NEXT_TOKEN";
    public static final String OBSERVABILITY_CONFIGURATION_ARN = "OBSERVABILITY_CONFIGURATION_ARN";
    public static final String OBSERVABILITY_CONFIGURATION_NAME = "OBSERVABILITY_CONFIGURATION_NAME";
    public static final String VENDOR = "AWSXRAY";

    public static final TraceConfiguration TRACE_CONFIGURATION = TraceConfiguration.builder()
            .vendor(VENDOR)
            .build();
    public static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .latest(IS_LATEST)
            .observabilityConfigurationName(OBSERVABILITY_CONFIGURATION_NAME)
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .observabilityConfigurationRevision(OBSERVABILITY_CONFIGURATION_REVISION)
            .traceConfiguration(TRACE_CONFIGURATION)
            .build();
    public static final ObservabilityConfiguration OBSERVABILITY_CONFIGURATION = ObservabilityConfiguration.builder()
            .latest(IS_LATEST)
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .observabilityConfigurationName(OBSERVABILITY_CONFIGURATION_NAME)
            .observabilityConfigurationRevision(OBSERVABILITY_CONFIGURATION_REVISION)
            .status(ObservabilityConfigurationStatus.ACTIVE)
            .traceConfiguration(translateToTraceConfiguration(TRACE_CONFIGURATION))
            .build();
    public static final ObservabilityConfiguration OBSERVABILITY_CONFIGURATION_INACTIVE = ObservabilityConfiguration.builder()
            .latest(IS_LATEST)
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .observabilityConfigurationName(OBSERVABILITY_CONFIGURATION_NAME)
            .observabilityConfigurationRevision(OBSERVABILITY_CONFIGURATION_REVISION)
            .status(ObservabilityConfigurationStatus.INACTIVE)
            .build();
    public static final ObservabilityConfigurationSummary OBSERVABILITY_CONFIGURATION_SUMMARY = ObservabilityConfigurationSummary.builder()
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .observabilityConfigurationName(OBSERVABILITY_CONFIGURATION_NAME)
            .observabilityConfigurationRevision(OBSERVABILITY_CONFIGURATION_REVISION)
            .build();
    public static final ResourceModel RESOURCE_MODEL_SUMMARY = ResourceModel.builder()
            .observabilityConfigurationName(OBSERVABILITY_CONFIGURATION_NAME)
            .observabilityConfigurationArn(OBSERVABILITY_CONFIGURATION_ARN)
            .observabilityConfigurationRevision(OBSERVABILITY_CONFIGURATION_REVISION)
            .build();
}