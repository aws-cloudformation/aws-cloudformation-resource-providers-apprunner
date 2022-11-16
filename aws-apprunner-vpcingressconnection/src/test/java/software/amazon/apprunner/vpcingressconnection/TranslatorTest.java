package software.amazon.apprunner.vpcingressconnection;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import software.amazon.awssdk.services.apprunner.model.CreateVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcIngressConnectionRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcIngressConnectionsRequest;

import static software.amazon.apprunner.vpcingressconnection.TestData.INGRESS_VPC_CONFIGURATION;
import static software.amazon.apprunner.vpcingressconnection.TestData.NEXT_TOKEN;
import static software.amazon.apprunner.vpcingressconnection.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.vpcingressconnection.TestData.SERVICE_ARN;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_ARN;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_NAME;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_PENDING_CREATION;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_INGRESS_CONNECTION_SUMMARY;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_ENDPOINT_ID;
import static software.amazon.apprunner.vpcingressconnection.TestData.VPC_ID;

import static software.amazon.apprunner.vpcingressconnection.Translator.translateVpcIngressConnectionSummaryToResourceModel;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateToCreateVpcIngressConnectionRequest;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateToDeleteVpcIngressConnectionRequest;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateToDescribeVpcIngressConnectionRequest;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateToIngressVpcConfiguration;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateToListVpcIngressConnectionsRequest;
import static software.amazon.apprunner.vpcingressconnection.Translator.translateVpcIngressConnectionFromResponse;


public class TranslatorTest {

    private static final String TAG_KEY = "key";
    private static final String TAG_VALUE = "value";

    @Test
    public void translateToIngressVpcConfiguration_Success() {

        final software.amazon.awssdk.services.apprunner.model.IngressVpcConfiguration ingressVpcConfiguration = translateToIngressVpcConfiguration(INGRESS_VPC_CONFIGURATION);

        assertThat(ingressVpcConfiguration).isNotNull();
        assertThat(ingressVpcConfiguration.vpcEndpointId()).isEqualTo(VPC_ENDPOINT_ID);
        assertThat(ingressVpcConfiguration.vpcId()).isEqualTo(VPC_ID);
    }

    @Test
    public void translateToCreateVpcIngressConnectionRequest_Success() {

        final Map<String, String> tagsMap = Collections.singletonMap(TAG_KEY, TAG_VALUE);
        final CreateVpcIngressConnectionRequest createVpcIngressConnectionRequest = translateToCreateVpcIngressConnectionRequest(RESOURCE_MODEL, tagsMap);

        assertThat(createVpcIngressConnectionRequest).isNotNull();
        assertThat(createVpcIngressConnectionRequest.vpcIngressConnectionName()).isEqualTo(VPC_INGRESS_CONNECTION_NAME);
        assertThat(createVpcIngressConnectionRequest.serviceArn()).isEqualTo(SERVICE_ARN);
        assertThat(createVpcIngressConnectionRequest.ingressVpcConfiguration()).isEqualTo(translateToIngressVpcConfiguration(INGRESS_VPC_CONFIGURATION));

        assertThat(createVpcIngressConnectionRequest.hasTags()).isTrue();
        assertThat(createVpcIngressConnectionRequest.tags()).contains(
                software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(TAG_KEY)
                        .value(TAG_VALUE)
                        .build());
    }

    @Test
    public void translateToDeleteVpcIngressConnectionRequest_Success() {

        final DeleteVpcIngressConnectionRequest deleteVpcIngressConnectionRequest = translateToDeleteVpcIngressConnectionRequest(RESOURCE_MODEL);

        assertThat(deleteVpcIngressConnectionRequest).isNotNull();
        assertThat(deleteVpcIngressConnectionRequest.vpcIngressConnectionArn()).isNotNull();
        assertThat(deleteVpcIngressConnectionRequest.vpcIngressConnectionArn()).isEqualTo(VPC_INGRESS_CONNECTION_ARN);
    }

    @Test
    public void translateToDescribeVpcIngressConnectionRequest_Success() {

        final DescribeVpcIngressConnectionRequest describeVpcIngressConnectionRequest = translateToDescribeVpcIngressConnectionRequest(RESOURCE_MODEL);

        assertThat(describeVpcIngressConnectionRequest).isNotNull();
        assertThat(describeVpcIngressConnectionRequest.vpcIngressConnectionArn()).isNotNull();
        assertThat(describeVpcIngressConnectionRequest.vpcIngressConnectionArn()).isEqualTo(VPC_INGRESS_CONNECTION_ARN);
    }

    @Test
    public void translateToListVpcIngressConnectionsRequest_Success() {

        final ListVpcIngressConnectionsRequest listVpcIngressConnectionsRequest = translateToListVpcIngressConnectionsRequest(NEXT_TOKEN);

        assertThat(listVpcIngressConnectionsRequest).isNotNull();
        assertThat(listVpcIngressConnectionsRequest.nextToken()).isNotNull();
        assertThat(listVpcIngressConnectionsRequest.nextToken()).isEqualTo(NEXT_TOKEN);

        final ListVpcIngressConnectionsRequest listVpcIngressConnectionsRequestNullNextToken = translateToListVpcIngressConnectionsRequest(null);

        assertThat(listVpcIngressConnectionsRequestNullNextToken).isNotNull();
        assertThat(listVpcIngressConnectionsRequestNullNextToken.nextToken()).isNull();
    }

    @Test
    public void translateVpcIngressConnectionSummaryToResourceModel_Success() {

        final ResourceModel resourceModel = translateVpcIngressConnectionSummaryToResourceModel(VPC_INGRESS_CONNECTION_SUMMARY);

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel.getVpcIngressConnectionArn()).isNotNull();
        assertThat(resourceModel.getVpcIngressConnectionArn()).isEqualTo(VPC_INGRESS_CONNECTION_ARN);
        assertThat(resourceModel.getServiceArn()).isNotNull();
        assertThat(resourceModel.getServiceArn()).isEqualTo(SERVICE_ARN);
    }

    @Test
    public void translateVpcIngressConnectionFromResponse_Success() {

        final ResourceModel resourceModel = translateVpcIngressConnectionFromResponse(VPC_INGRESS_CONNECTION_PENDING_CREATION);

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel).isEqualTo(RESOURCE_MODEL);
    }

    @Test
    public void translateVpcIngressConnectionFromResponse_Null() {

        final ResourceModel resourceModel = translateVpcIngressConnectionFromResponse(null);

        assertThat(resourceModel).isNull();
    }
}