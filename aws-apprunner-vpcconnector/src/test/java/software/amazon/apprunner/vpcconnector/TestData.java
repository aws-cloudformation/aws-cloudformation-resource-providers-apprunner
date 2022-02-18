package software.amazon.apprunner.vpcconnector;

import software.amazon.awssdk.services.apprunner.model.VpcConnector;
import software.amazon.awssdk.services.apprunner.model.VpcConnectorStatus;

import java.util.Arrays;
import java.util.HashSet;

public class TestData {

    public static final String NEXT_TOKEN = "NEXT_TOKEN";
    public static final String SECURITY_GROUP_ID = "SECURITY_GROUP_ID";
    public static final String SUBNET_ID = "SUBNET_ID";
    public static final String VPC_CONNECTOR_ARN = "VPC_CONNECTOR_ARN";
    public static final String VPC_CONNECTOR_NAME = "VPC_CONNECTOR_NAME";
    public static final Integer VPC_CONNECTOR_REVISION = 1;

    public static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .vpcConnectorName(VPC_CONNECTOR_NAME)
            .vpcConnectorArn(VPC_CONNECTOR_ARN)
            .vpcConnectorRevision(VPC_CONNECTOR_REVISION)
            .subnets(new HashSet<String>(Arrays.asList(SUBNET_ID)))
            .securityGroups(new HashSet<String>(Arrays.asList(SECURITY_GROUP_ID)))
            .build();
    public static final VpcConnector VPC_CONNECTOR = VpcConnector.builder()
            .vpcConnectorArn(VPC_CONNECTOR_ARN)
            .vpcConnectorName(VPC_CONNECTOR_NAME)
            .vpcConnectorRevision(VPC_CONNECTOR_REVISION)
            .subnets(Arrays.asList(SUBNET_ID))
            .securityGroups(Arrays.asList(SECURITY_GROUP_ID))
            .status(VpcConnectorStatus.ACTIVE)
            .build();
    public static final VpcConnector VPC_CONNECTOR_INACTIVE = VpcConnector.builder()
            .vpcConnectorArn(VPC_CONNECTOR_ARN)
            .vpcConnectorName(VPC_CONNECTOR_NAME)
            .vpcConnectorRevision(VPC_CONNECTOR_REVISION)
            .subnets(Arrays.asList(SUBNET_ID))
            .securityGroups(Arrays.asList(SECURITY_GROUP_ID))
            .status(VpcConnectorStatus.INACTIVE)
            .build();
}
