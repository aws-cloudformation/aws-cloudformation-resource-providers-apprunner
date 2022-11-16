package software.amazon.apprunner.vpcingressconnection;

//import software.amazon.apprunner.observabilityconfiguration.ResourceModel;
import software.amazon.awssdk.services.apprunner.model.*;

import static software.amazon.apprunner.vpcingressconnection.Translator.*;

public class TestData {

    public static final String NEXT_TOKEN = "NEXT_TOKEN";
    public static final String VPC_INGRESS_CONNECTION_ARN = "VPC_INGRESS_CONNECTION_ARN";
    public static final String SERVICE_ARN = "SERVICE_ARN";
    public static final String VPC_INGRESS_CONNECTION_NAME = "VPC_INGRESS_CONNECTION_NAME";
    public static final String DOMAIN_NAME = "DOMAIN_NAME";
    public static final String STATUS = "STATUS";
    public static final String VPC_ENDPOINT_ID = "VPC_ENDPOINT_ID";
    public static final String VPC_ID = "VPC_ID";
    public static final String VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION = "PENDING_CREATION";
    public static final String VPC_INGRESS_CONNECTION_STATUS_PENDING_DELETION = "PENDING_DELETION";
    public static final String VPC_INGRESS_CONNECTION_STATUS_PENDING_UPDATE = "PENDING_UPDATE";
    public static final String LOGICAL_RESOURCE_IDENTIFIER = "id12345678901011";
    public static final String CLIENT_REQUEST_TOKEN = "12345621652";
    public static final String VPC_INGRESS_CONNECTION_STATUS_AVAILABLE = "AVAILABLE";
    public static final String VPC_INGRESS_CONNECTION_STATUS_DELETED = "DELETED";



    public static final IngressVpcConfiguration INGRESS_VPC_CONFIGURATION = IngressVpcConfiguration.builder()
            .vpcEndpointId(VPC_ENDPOINT_ID)
            .vpcId(VPC_ID)
            .build();
    public static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
            .serviceArn(SERVICE_ARN)
            .domainName(DOMAIN_NAME)
            .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
            .ingressVpcConfiguration(INGRESS_VPC_CONFIGURATION)
            .build();
    public static final ResourceModel RESOURCE_MODEL_PENDING_DELETION = ResourceModel.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
            .serviceArn(SERVICE_ARN)
            .domainName(DOMAIN_NAME)
            .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_DELETION)
            .ingressVpcConfiguration(INGRESS_VPC_CONFIGURATION)
            .build();

    public static final VpcIngressConnection VPC_INGRESS_CONNECTION_PENDING_CREATION = VpcIngressConnection.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
            .serviceArn(SERVICE_ARN)
            .domainName(DOMAIN_NAME)
            .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_CREATION)
            .ingressVpcConfiguration(translateToIngressVpcConfiguration(INGRESS_VPC_CONFIGURATION))
            .build();
    public static final VpcIngressConnection VPC_INGRESS_CONNECTION_PENDING_DELETION = VpcIngressConnection.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
            .serviceArn(SERVICE_ARN)
            .domainName(DOMAIN_NAME)
            .status(VPC_INGRESS_CONNECTION_STATUS_PENDING_DELETION)
            .ingressVpcConfiguration(translateToIngressVpcConfiguration(INGRESS_VPC_CONFIGURATION))
            .build();

    public static final VpcIngressConnection VPC_INGRESS_CONNECTION_AVAILABLE = VpcIngressConnection.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .vpcIngressConnectionName(VPC_INGRESS_CONNECTION_NAME)
            .serviceArn(SERVICE_ARN)
            .domainName(DOMAIN_NAME)
            .status(VPC_INGRESS_CONNECTION_STATUS_AVAILABLE)
            .ingressVpcConfiguration(translateToIngressVpcConfiguration(INGRESS_VPC_CONFIGURATION))
            .build();

    public static final VpcIngressConnectionSummary VPC_INGRESS_CONNECTION_SUMMARY = VpcIngressConnectionSummary.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .serviceArn(SERVICE_ARN)
            .build();

    public static final ResourceModel RESOURCE_MODEL_SUMMARY = ResourceModel.builder()
            .vpcIngressConnectionArn(VPC_INGRESS_CONNECTION_ARN)
            .serviceArn(SERVICE_ARN)
            .build();
}
