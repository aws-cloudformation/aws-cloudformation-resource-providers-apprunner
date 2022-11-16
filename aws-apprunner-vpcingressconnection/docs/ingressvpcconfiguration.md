# AWS::AppRunner::VpcIngressConnection IngressVpcConfiguration

The configuration of customer’s VPC and related VPC endpoint

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#vpcid" title="VpcId">VpcId</a>" : <i>String</i>,
    "<a href="#vpcendpointid" title="VpcEndpointId">VpcEndpointId</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#vpcid" title="VpcId">VpcId</a>: <i>String</i>
<a href="#vpcendpointid" title="VpcEndpointId">VpcEndpointId</a>: <i>String</i>
</pre>

## Properties

#### VpcId

The ID of the VPC that the VPC endpoint is used in.

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### VpcEndpointId

The ID of the VPC endpoint that your App Runner service connects to.

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

