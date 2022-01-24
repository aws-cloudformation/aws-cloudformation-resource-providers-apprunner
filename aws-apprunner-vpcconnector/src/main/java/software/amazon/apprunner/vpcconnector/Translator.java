package software.amazon.apprunner.vpcconnector;

import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcConnectorsRequest;
import software.amazon.awssdk.services.apprunner.model.VpcConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static software.amazon.apprunner.vpcconnector.TagHelper.convertToList;

/**
 * This class is a centralized placeholder for the CFN VpcConnector resource:
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - model construction for read/list handlers
 */
public class Translator {

    /**
     * Request to create a App Runner VpcConnector resource
     * @param model AppRunner VpcConnector resource model
     * @return createVpcConnectorRequest the request to create a AppRunner VpcConnector resource
     */
    static CreateVpcConnectorRequest translateToCreateVpcConnectorRequest(final ResourceModel model, final Map<String, String> tags) {
        return CreateVpcConnectorRequest.builder()
                .vpcConnectorName(model.getVpcConnectorName())
                .subnets(model.getSubnets())
                .securityGroups(model.getSecurityGroups() == null ? null : new ArrayList<String>(model.getSecurityGroups()))
                .tags(convertToList(tags))
                .build();
    }

    /**
     * Request to delete a App Runner VpcConnector resource
     * @param model AppRunner VpcConnector resource model
     * @return deleteVpcConnectorRequest the request to delete a AppRunner VpcConnector resource
     */
    static DeleteVpcConnectorRequest translateToDeleteVpcConnectorRequest(ResourceModel model) {
        return DeleteVpcConnectorRequest.builder()
                .vpcConnectorArn(model.getVpcConnectorArn())
                .build();
    }

    /**
     * Request to describe a App Runner VpcConnector resource
     * @param model AppRunner VpcConnector resource model
     * @return describeVpcConnectorRequest the request to describe a AppRunner VpcConnector resource
     */
    static DescribeVpcConnectorRequest translateToDescribeVpcConnectorRequest(final ResourceModel model) {
        return DescribeVpcConnectorRequest.builder()
                .vpcConnectorArn(model.getVpcConnectorArn())
                .build();
    }

    /**
     * Request to list the App Runner VpcConnector resource
     * @param nextToken next token
     * @return listVpcConnectorsRequest the request to list AppRunner VpcConnector resources
     */
    static ListVpcConnectorsRequest translateToListVpcConnectorsRequest(final String nextToken) {
        return ListVpcConnectorsRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    /**
     * Translates App Runner VpcConnector response object to resource model type
     * @param vpcConnector the vpcConnector resource
     * @return model resource model
     */
    static ResourceModel translateVpcConnectorFromResponse(final VpcConnector vpcConnector) {
        if (vpcConnector == null) {
            return null;
        }
        return ResourceModel.builder()
                .vpcConnectorName(vpcConnector.vpcConnectorName())
                .vpcConnectorArn(vpcConnector.vpcConnectorArn())
                .vpcConnectorRevision(vpcConnector.vpcConnectorRevision())
                .securityGroups(vpcConnector.securityGroups().stream().collect(Collectors.toSet()))
                .subnets(vpcConnector.subnets().stream().collect(Collectors.toSet()))
                .build();
    }
}
