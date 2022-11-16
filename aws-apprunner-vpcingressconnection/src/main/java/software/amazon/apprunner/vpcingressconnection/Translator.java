package software.amazon.apprunner.vpcingressconnection;

import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcIngressConnectionsRequest;
import software.amazon.awssdk.services.apprunner.model.Tag;
import software.amazon.awssdk.services.apprunner.model.UpdateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnection;
import software.amazon.awssdk.services.apprunner.model.VpcIngressConnectionSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is a centralized placeholder for the CFN VpcIngressConnection resource
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {
    /**
     * Translate IngressVpcConfiguration object to App Runner IngressVpcConfiguration resource
     * @param ingressVpcConfiguration IngressVpcConfiguration resource
     * @return resourceModel the cloudformation resource model type
     */
    static software.amazon.awssdk.services.apprunner.model.IngressVpcConfiguration translateToIngressVpcConfiguration(
            final IngressVpcConfiguration ingressVpcConfiguration) {
        return software.amazon.awssdk.services.apprunner.model.IngressVpcConfiguration.builder()
                .vpcId(ingressVpcConfiguration.getVpcId())
                .vpcEndpointId(ingressVpcConfiguration.getVpcEndpointId())
                .build();
    }

    /**
     * Translate IngressVpcConfiguration resource model to an object
     * @param ingressVpcConfiguration resourceModel the cloudformation resource model type
     * @return IngressVpcConfiguration resource
     */

    private static IngressVpcConfiguration translateFromIngressVpcConfiguration(
            final software.amazon.awssdk.services.apprunner.model.IngressVpcConfiguration ingressVpcConfiguration) {
        return IngressVpcConfiguration.builder()
                .vpcId(ingressVpcConfiguration.vpcId())
                .vpcEndpointId(ingressVpcConfiguration.vpcEndpointId())
                .build();
    }


    /**
     * Request to create a App Runner VpcIngressConnection resource
     *
     * @param model AppRunner VpcIngressConnection resource model
     * @return createVpcIngressConnectionRequest the request to create a AppRunner VpcIngressConnection resource
     */


    static CreateVpcIngressConnectionRequest translateToCreateVpcIngressConnectionRequest(final ResourceModel model, final Map<String, String> tags) {
        return CreateVpcIngressConnectionRequest.builder()
                .serviceArn(model.getServiceArn())
                .vpcIngressConnectionName(model.getVpcIngressConnectionName())
                .ingressVpcConfiguration(translateToIngressVpcConfiguration(model.getIngressVpcConfiguration()))
                .tags(translateToTagsList(tags))
                .build();
    }



    /**
     * Request to delete a App Runner VpcIngressConnection resource
     * @param model AppRunner VpcIngressConnection resource model
     * @return deleteVpcIngressConnectionRequest the request to delete a AppRunner VpcIngressConnection resource
     */
    static DeleteVpcIngressConnectionRequest translateToDeleteVpcIngressConnectionRequest(ResourceModel model) {
        return DeleteVpcIngressConnectionRequest.builder()
                .vpcIngressConnectionArn(model.getVpcIngressConnectionArn())
                .build();
    }

    /**
     * Request to describe a App Runner VpcIngressConnection resource
     * @param model AppRunner VpcIngressConnection resource model
     * @return describeVpcIngressConnectionRequest the request to describe a AppRunner VpcIngressConnection resource
     */
    static DescribeVpcIngressConnectionRequest translateToDescribeVpcIngressConnectionRequest(final ResourceModel model) {
        return DescribeVpcIngressConnectionRequest.builder()
                .vpcIngressConnectionArn(model.getVpcIngressConnectionArn())
                .build();
    }

    /**
     * Request to list the App Runner VpcIngressConnection resource
     * @param nextToken next token
     * @return listVpcIngressConnectionRequest the request to list AppRunner VpcIngressConnection resources
     */
    static ListVpcIngressConnectionsRequest translateToListVpcIngressConnectionsRequest(final String nextToken) {
        return ListVpcIngressConnectionsRequest.builder()
                .nextToken(nextToken)
                .build();
    }


    /**
     * Request to update the App Runner VpcIngressConnection resource
     * @param model resource model
     * @return UpdateVpcIngressConnectionRequest the request to update AppRunner VpcIngressConnection resources
     */
    static UpdateVpcIngressConnectionRequest translateToUpdateVpcIngressConnectionRequest(ResourceModel model) {
        return UpdateVpcIngressConnectionRequest.builder()
                .vpcIngressConnectionArn(model.getVpcIngressConnectionArn())
                .ingressVpcConfiguration(translateToIngressVpcConfiguration(model.getIngressVpcConfiguration()))
                .build();
    }

    /**
     * Converts the VpcIngressConnection summary type to resource model type
     * @param vpcIngressConnectionSummary VpcIngressConnection configuration summary
     * @return resourceModel the cloudformation resource model type
     */

    static ResourceModel translateVpcIngressConnectionSummaryToResourceModel(final VpcIngressConnectionSummary vpcIngressConnectionSummary) {
        return ResourceModel.builder()
                .vpcIngressConnectionArn(vpcIngressConnectionSummary.vpcIngressConnectionArn())
                .serviceArn(vpcIngressConnectionSummary.serviceArn())
                .build();
    }

    /**
     * Translates App Runner VpcIngressConnection response object to resource model type
     * @param vpcIngressConnection the VpcIngressConnection resource
     * @return model resource model
     */
    static ResourceModel translateVpcIngressConnectionFromResponse(final VpcIngressConnection vpcIngressConnection) {
        return vpcIngressConnection == null ? null
                : ResourceModel.builder()
                .vpcIngressConnectionArn(vpcIngressConnection.vpcIngressConnectionArn())
                .vpcIngressConnectionName(vpcIngressConnection.vpcIngressConnectionName())
                .serviceArn(vpcIngressConnection.serviceArn())
                .status(vpcIngressConnection.statusAsString())
                .domainName(vpcIngressConnection.domainName())
                .ingressVpcConfiguration(translateFromIngressVpcConfiguration(vpcIngressConnection.ingressVpcConfiguration()))
                .build();
    }

    private static List<Tag> translateToTagsList(final Map<String, String> tags) {
        return tags == null ? null : tags.keySet().stream()
                .map(key -> software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(key)
                        .value(tags.get(key))
                        .build())
                .collect(Collectors.toList());
    }

}
