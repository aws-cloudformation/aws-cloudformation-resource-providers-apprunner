# AWS::AppRunner::VpcIngressConnection

The AWS::AppRunner::VpcIngressConnection resource is an App Runner resource that specifies an App Runner VpcIngressConnection.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AppRunner::VpcIngressConnection",
    "Properties" : {
        "<a href="#vpcingressconnectionname" title="VpcIngressConnectionName">VpcIngressConnectionName</a>" : <i>String</i>,
        "<a href="#servicearn" title="ServiceArn">ServiceArn</a>" : <i>String</i>,
        "<a href="#ingressvpcconfiguration" title="IngressVpcConfiguration">IngressVpcConfiguration</a>" : <i><a href="ingressvpcconfiguration.md">IngressVpcConfiguration</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AppRunner::VpcIngressConnection
Properties:
    <a href="#vpcingressconnectionname" title="VpcIngressConnectionName">VpcIngressConnectionName</a>: <i>String</i>
    <a href="#servicearn" title="ServiceArn">ServiceArn</a>: <i>String</i>
    <a href="#ingressvpcconfiguration" title="IngressVpcConfiguration">IngressVpcConfiguration</a>: <i><a href="ingressvpcconfiguration.md">IngressVpcConfiguration</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### VpcIngressConnectionName

The customer-provided Vpc Ingress Connection name.

_Required_: No

_Type_: String

_Minimum_: <code>4</code>

_Maximum_: <code>40</code>

_Pattern_: <code>[A-Za-z0-9][A-Za-z0-9\-_]{3,39}</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ServiceArn

The Amazon Resource Name (ARN) of the service.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1011</code>

_Pattern_: <code>arn:aws(-[\w]+)*:[a-z0-9-\.]{0,63}:[a-z0-9-\.]{0,63}:[0-9]{12}:(\w|/|-){1,1011}</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### IngressVpcConfiguration

The configuration of customer’s VPC and related VPC endpoint

_Required_: Yes

_Type_: <a href="ingressvpcconfiguration.md">IngressVpcConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the VpcIngressConnectionArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### VpcIngressConnectionArn

The Amazon Resource Name (ARN) of the VpcIngressConnection.

#### DomainName

The Domain name associated with the VPC Ingress Connection.

#### Status

The current status of the VpcIngressConnection.

