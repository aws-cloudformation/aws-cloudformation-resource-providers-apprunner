package software.amazon.apprunner.observabilityconfiguration;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.apprunner.model.CreateObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.ListObservabilityConfigurationsRequest;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationSummary;
import software.amazon.awssdk.services.apprunner.model.TracingVendor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.apprunner.observabilityconfiguration.TestData.NEXT_TOKEN;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION_ARN;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION_NAME;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION_REVISION;
import static software.amazon.apprunner.observabilityconfiguration.TestData.OBSERVABILITY_CONFIGURATION_SUMMARY;
import static software.amazon.apprunner.observabilityconfiguration.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.observabilityconfiguration.TestData.TRACE_CONFIGURATION;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToCreateObservabilityConfigurationRequest;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToDeleteObservabilityConfigurationRequest;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToDescribeObservabilityConfigurationRequest;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToListObservabilityConfigurationsRequest;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateToTraceConfiguration;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateObservabilityConfigurationFromResponse;
import static software.amazon.apprunner.observabilityconfiguration.Translator.translateObservabilityConfigurationSummaryToResourceModel;

public class TranslatorTest {

    private static final String TAG_KEY = "key";
    private static final String TAG_VALUE = "value";

    @Test
    public void translateToTraceConfiguration_Success() {

        final software.amazon.awssdk.services.apprunner.model.TraceConfiguration traceConfiguration = translateToTraceConfiguration(TRACE_CONFIGURATION);

        assertThat(traceConfiguration).isNotNull();
        assertThat(traceConfiguration.vendor()).isNotNull();
        assertThat(traceConfiguration.vendor()).isEqualTo(TracingVendor.AWSXRAY);
    }

    @Test
    public void translateToCreateObservabilityConfigurationRequest_Success() {

        final Map<String, String> tagsMap = Collections.singletonMap(TAG_KEY, TAG_VALUE);
        final CreateObservabilityConfigurationRequest createObservabilityConfigurationRequest = translateToCreateObservabilityConfigurationRequest(RESOURCE_MODEL, tagsMap);

        assertThat(createObservabilityConfigurationRequest).isNotNull();
        assertThat(createObservabilityConfigurationRequest.observabilityConfigurationName()).isNotNull();
        assertThat(createObservabilityConfigurationRequest.observabilityConfigurationName()).isEqualTo(OBSERVABILITY_CONFIGURATION_NAME);
        assertThat(createObservabilityConfigurationRequest.traceConfiguration()).isNotNull();
        assertThat(createObservabilityConfigurationRequest.traceConfiguration()).isEqualTo(translateToTraceConfiguration(TRACE_CONFIGURATION));
        assertThat(createObservabilityConfigurationRequest.hasTags()).isTrue();
        assertThat(createObservabilityConfigurationRequest.tags()).contains(
                software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(TAG_KEY)
                        .value(TAG_VALUE)
                        .build());
    }

    @Test
    public void translateToDeleteObservabilityConfigurationRequest_Success() {

        final DeleteObservabilityConfigurationRequest deleteObservabilityConfigurationRequest = translateToDeleteObservabilityConfigurationRequest(RESOURCE_MODEL);

        assertThat(deleteObservabilityConfigurationRequest).isNotNull();
        assertThat(deleteObservabilityConfigurationRequest.observabilityConfigurationArn()).isNotNull();
        assertThat(deleteObservabilityConfigurationRequest.observabilityConfigurationArn()).isEqualTo(OBSERVABILITY_CONFIGURATION_ARN);
    }

    @Test
    public void translateToDescribeObservabilityConfigurationRequest_Success() {

        final DescribeObservabilityConfigurationRequest describeObservabilityConfigurationRequest = translateToDescribeObservabilityConfigurationRequest(RESOURCE_MODEL);

        assertThat(describeObservabilityConfigurationRequest).isNotNull();
        assertThat(describeObservabilityConfigurationRequest.observabilityConfigurationArn()).isNotNull();
        assertThat(describeObservabilityConfigurationRequest.observabilityConfigurationArn()).isEqualTo(OBSERVABILITY_CONFIGURATION_ARN);
    }

    @Test
    public void translateToListObservabilityConfigurationsRequest_Success() {

        final ListObservabilityConfigurationsRequest listObservabilityConfigurationsRequest = translateToListObservabilityConfigurationsRequest(NEXT_TOKEN);

        assertThat(listObservabilityConfigurationsRequest).isNotNull();
        assertThat(listObservabilityConfigurationsRequest.nextToken()).isNotNull();
        assertThat(listObservabilityConfigurationsRequest.nextToken()).isEqualTo(NEXT_TOKEN);

        final ListObservabilityConfigurationsRequest listObservabilityConfigurationsRequestNullNextToken = translateToListObservabilityConfigurationsRequest(null);

        assertThat(listObservabilityConfigurationsRequestNullNextToken).isNotNull();
        assertThat(listObservabilityConfigurationsRequestNullNextToken.nextToken()).isNull();
    }

    @Test
    public void translateObservabilityConfigurationSummaryToResourceModel_Success() {

        final ResourceModel resourceModel = translateObservabilityConfigurationSummaryToResourceModel(OBSERVABILITY_CONFIGURATION_SUMMARY);

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel.getObservabilityConfigurationArn()).isNotNull();
        assertThat(resourceModel.getObservabilityConfigurationArn()).isEqualTo(OBSERVABILITY_CONFIGURATION_ARN);
        assertThat(resourceModel.getObservabilityConfigurationName()).isNotNull();
        assertThat(resourceModel.getObservabilityConfigurationName()).isEqualTo(OBSERVABILITY_CONFIGURATION_NAME);
        assertThat(resourceModel.getObservabilityConfigurationRevision()).isNotNull();
        assertThat(resourceModel.getObservabilityConfigurationRevision()).isEqualTo(OBSERVABILITY_CONFIGURATION_REVISION);
    }

    @Test
    public void translateObservabilityConfigurationFromResponse_Success() {

        final ResourceModel resourceModel = translateObservabilityConfigurationFromResponse(OBSERVABILITY_CONFIGURATION);

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel).isEqualTo(RESOURCE_MODEL);
    }

    @Test
    public void translateObservabilityConfigurationFromResponse_Null() {

        final ResourceModel resourceModel = translateObservabilityConfigurationFromResponse(null);

        assertThat(resourceModel).isNull();
    }
}