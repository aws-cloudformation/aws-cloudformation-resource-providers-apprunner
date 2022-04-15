package software.amazon.apprunner.observabilityconfiguration;

import software.amazon.awssdk.services.apprunner.model.CreateObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeObservabilityConfigurationRequest;
import software.amazon.awssdk.services.apprunner.model.ListObservabilityConfigurationsRequest;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfiguration;
import software.amazon.awssdk.services.apprunner.model.ObservabilityConfigurationSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is a centralized placeholder for the CFN ObservabilityConfiguration resource:
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - model construction for read/list handlers
 */
public class Translator {

    /**
     * Request to create a App Runner ObservabilityConfiguration resource
     * @param model AppRunner ObservabilityConfiguration resource model
     * @return createObservabilityConfigurationRequest the request to create a AppRunner ObservabilityConfiguration resource
     */
    static CreateObservabilityConfigurationRequest translateToCreateObservabilityConfigurationRequest(final ResourceModel model, final Map<String, String> tags) {
        return CreateObservabilityConfigurationRequest.builder()
                .observabilityConfigurationName(model.getObservabilityConfigurationName())
                .tags(translateToTagsList(tags))
                .traceConfiguration(translateToTraceConfiguration(model.getTraceConfiguration()))
                .build();
    }

    /**
     * Request to delete a App Runner ObservabilityConfiguration resource
     * @param model AppRunner ObservabilityConfiguration resource model
     * @return deleteObservabilityConfigurationRequest the request to delete a AppRunner ObservabilityConfiguration resource
     */
    static DeleteObservabilityConfigurationRequest translateToDeleteObservabilityConfigurationRequest(ResourceModel model) {
        return DeleteObservabilityConfigurationRequest.builder()
                .observabilityConfigurationArn(model.getObservabilityConfigurationArn())
                .build();
    }

    /**
     * Request to describe a App Runner ObservabilityConfiguration resource
     * @param model AppRunner ObservabilityConfiguration resource model
     * @return describeObservabilityConfigurationRequest the request to describe a AppRunner ObservabilityConfiguration resource
     */
    static DescribeObservabilityConfigurationRequest translateToDescribeObservabilityConfigurationRequest(final ResourceModel model) {
        return DescribeObservabilityConfigurationRequest.builder()
                .observabilityConfigurationArn(model.getObservabilityConfigurationArn())
                .build();
    }

    /**
     * Request to list the App Runner ObservabilityConfiguration resource
     * @param nextToken next token
     * @return listObservabilityConfigurationsRequest the request to list AppRunner ObservabilityConfiguration resources
     */
    static ListObservabilityConfigurationsRequest translateToListObservabilityConfigurationsRequest(final String nextToken) {
        return ListObservabilityConfigurationsRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    /**
     * Converts the observability configuration summary type to resource model type
     * @param observabilityConfigurationSummary observability configuration summary
     * @return resourceModel the cloudformation resource model type
     */
    static ResourceModel translateObservabilityConfigurationSummaryToResourceModel(final ObservabilityConfigurationSummary observabilityConfigurationSummary) {
        return ResourceModel.builder()
                .observabilityConfigurationArn(observabilityConfigurationSummary.observabilityConfigurationArn())
                .observabilityConfigurationName(observabilityConfigurationSummary.observabilityConfigurationName())
                .observabilityConfigurationRevision(observabilityConfigurationSummary.observabilityConfigurationRevision())
                .build();
    }

    /**
     * Translates App Runner ObservabilityConfiguration response object to resource model type
     * @param observabilityConfiguration the observabilityConfiguration resource
     * @return model resource model
     */
    static ResourceModel translateObservabilityConfigurationFromResponse(final ObservabilityConfiguration observabilityConfiguration) {
        return observabilityConfiguration == null ? null
                : ResourceModel.builder()
                .latest(observabilityConfiguration.latest())
                .observabilityConfigurationArn(observabilityConfiguration.observabilityConfigurationArn())
                .observabilityConfigurationName(observabilityConfiguration.observabilityConfigurationName())
                .observabilityConfigurationRevision(observabilityConfiguration.observabilityConfigurationRevision())
                .traceConfiguration(translateFromTraceConfiguration(observabilityConfiguration.traceConfiguration()))
                .build();
    }

    /**
     * Translate TraceConfiguration object to App Runner TraceConfiguration resource
     * @param traceConfiguration TraceConfiguration resource
     * @return resourceModel the cloudformation resource model type
     */
    static software.amazon.awssdk.services.apprunner.model.TraceConfiguration translateToTraceConfiguration(
            final TraceConfiguration traceConfiguration) {
        return traceConfiguration == null ? null
                : software.amazon.awssdk.services.apprunner.model.TraceConfiguration.builder()
                .vendor(traceConfiguration.getVendor())
                .build();
    }

    private static TraceConfiguration translateFromTraceConfiguration(
            final software.amazon.awssdk.services.apprunner.model.TraceConfiguration traceConfiguration) {
        return traceConfiguration == null ? null
                : TraceConfiguration.builder()
                .vendor(traceConfiguration.vendorAsString())
                .build();
    }

    private static List<software.amazon.awssdk.services.apprunner.model.Tag> translateToTagsList(final Map<String, String> tags) {
        return tags == null ? null : tags.keySet().stream()
                .map(key -> software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(key)
                        .value(tags.get(key))
                        .build())
                .collect(Collectors.toList());
    }
}