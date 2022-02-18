package software.amazon.apprunner.vpcconnector;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.apprunner.model.CreateVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DeleteVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.DescribeVpcConnectorRequest;
import software.amazon.awssdk.services.apprunner.model.ListVpcConnectorsRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.apprunner.vpcconnector.TestData.RESOURCE_MODEL;
import static software.amazon.apprunner.vpcconnector.TestData.NEXT_TOKEN;
import static software.amazon.apprunner.vpcconnector.TestData.SECURITY_GROUP_ID;
import static software.amazon.apprunner.vpcconnector.TestData.SUBNET_ID;
import static software.amazon.apprunner.vpcconnector.TestData.VPC_CONNECTOR;
import static software.amazon.apprunner.vpcconnector.TestData.VPC_CONNECTOR_ARN;
import static software.amazon.apprunner.vpcconnector.TestData.VPC_CONNECTOR_NAME;
import static software.amazon.apprunner.vpcconnector.Translator.translateToCreateVpcConnectorRequest;
import static software.amazon.apprunner.vpcconnector.Translator.translateToDeleteVpcConnectorRequest;
import static software.amazon.apprunner.vpcconnector.Translator.translateToDescribeVpcConnectorRequest;
import static software.amazon.apprunner.vpcconnector.Translator.translateToListVpcConnectorsRequest;
import static software.amazon.apprunner.vpcconnector.Translator.translateVpcConnectorFromResponse;

public class TranslatorTest {

    private static final String TAG_KEY = "key";
    private static final String TAG_VALUE = "value";

    @Test
    public void translateToCreateVpcConnectorRequest_Success() {

        final Map<String, String> tagsMap = Collections.singletonMap(TAG_KEY, TAG_VALUE);
        final CreateVpcConnectorRequest createVpcConnectorRequest = translateToCreateVpcConnectorRequest(RESOURCE_MODEL, tagsMap);

        assertThat(createVpcConnectorRequest).isNotNull();
        assertThat(createVpcConnectorRequest.vpcConnectorName()).isNotNull();
        assertThat(createVpcConnectorRequest.vpcConnectorName()).isEqualTo(VPC_CONNECTOR_NAME);
        assertThat(createVpcConnectorRequest.securityGroups()).isNotNull();
        assertThat(createVpcConnectorRequest.securityGroups()).isEqualTo(Arrays.asList(SECURITY_GROUP_ID));
        assertThat(createVpcConnectorRequest.subnets()).isNotNull();
        assertThat(createVpcConnectorRequest.subnets()).isEqualTo(Arrays.asList(SUBNET_ID));
        assertThat(createVpcConnectorRequest.hasTags()).isTrue();
        assertThat(createVpcConnectorRequest.tags()).contains(
                software.amazon.awssdk.services.apprunner.model.Tag.builder()
                        .key(TAG_KEY)
                        .value(TAG_VALUE)
                        .build());
    }

    @Test
    public void translateToDeleteVpcConnectorRequest_Success() {

        final DeleteVpcConnectorRequest deleteVpcConnectorRequest = translateToDeleteVpcConnectorRequest(RESOURCE_MODEL);

        assertThat(deleteVpcConnectorRequest).isNotNull();
        assertThat(deleteVpcConnectorRequest.vpcConnectorArn()).isNotNull();
        assertThat(deleteVpcConnectorRequest.vpcConnectorArn()).isEqualTo(VPC_CONNECTOR_ARN);
    }

    @Test
    public void translateToDescribeVpcConnectorRequest_Success() {

        final DescribeVpcConnectorRequest describeVpcConnectorRequest = translateToDescribeVpcConnectorRequest(RESOURCE_MODEL);

        assertThat(describeVpcConnectorRequest).isNotNull();
        assertThat(describeVpcConnectorRequest.vpcConnectorArn()).isNotNull();
        assertThat(describeVpcConnectorRequest.vpcConnectorArn()).isEqualTo(VPC_CONNECTOR_ARN);
    }

    @Test
    public void translateToListVpcConnectorsRequest_Success() {

        final ListVpcConnectorsRequest listVpcConnectorsRequest = translateToListVpcConnectorsRequest(NEXT_TOKEN);

        assertThat(listVpcConnectorsRequest).isNotNull();
        assertThat(listVpcConnectorsRequest.nextToken()).isNotNull();
        assertThat(listVpcConnectorsRequest.nextToken()).isEqualTo(NEXT_TOKEN);

        final ListVpcConnectorsRequest listVpcConnectorsRequestNullNextToken = translateToListVpcConnectorsRequest(null);

        assertThat(listVpcConnectorsRequestNullNextToken).isNotNull();
        assertThat(listVpcConnectorsRequestNullNextToken.nextToken()).isNull();
    }

    @Test
    public void translateVpcConnectorFromResponse_Success() {

        final ResourceModel resourceModel = translateVpcConnectorFromResponse(VPC_CONNECTOR);

        assertThat(resourceModel).isNotNull();
        assertThat(resourceModel).isEqualTo(RESOURCE_MODEL);
    }

    @Test
    public void translateVpcConnectorFromResponse_Null() {

        final ResourceModel resourceModel = translateVpcConnectorFromResponse(null);

        assertThat(resourceModel).isNull();
    }
}
