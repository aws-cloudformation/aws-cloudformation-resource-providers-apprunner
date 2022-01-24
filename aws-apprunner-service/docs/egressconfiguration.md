# AWS::AppRunner::Service EgressConfiguration

Network egress configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#egresstype" title="EgressType">EgressType</a>" : <i>String</i>,
    "<a href="#vpcconnectorarn" title="VpcConnectorArn">VpcConnectorArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#egresstype" title="EgressType">EgressType</a>: <i>String</i>
<a href="#vpcconnectorarn" title="VpcConnectorArn">VpcConnectorArn</a>: <i>String</i>
</pre>

## Properties

#### EgressType

Network egress type.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>DEFAULT</code> | <code>VPC</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### VpcConnectorArn

The Amazon Resource Name (ARN) of the App Runner VpcConnector.

_Required_: No

_Type_: String

_Minimum_: <code>44</code>

_Maximum_: <code>1011</code>

_Pattern_: <code>arn:aws(-[\w]+)*:[a-z0-9-\\.]{0,63}:[a-z0-9-\\.]{0,63}:[0-9]{12}:(\w|\/|-){1,1011}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
