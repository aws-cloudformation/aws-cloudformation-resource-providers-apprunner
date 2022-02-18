# AWS::AppRunner::Service

The AWS::AppRunner::Service resource specifies an AppRunner Service.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AppRunner::Service",
    "Properties" : {
        "<a href="#servicename" title="ServiceName">ServiceName</a>" : <i>String</i>,
        "<a href="#sourceconfiguration" title="SourceConfiguration">SourceConfiguration</a>" : <i><a href="sourceconfiguration.md">SourceConfiguration</a></i>,
        "<a href="#instanceconfiguration" title="InstanceConfiguration">InstanceConfiguration</a>" : <i><a href="instanceconfiguration.md">InstanceConfiguration</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#encryptionconfiguration" title="EncryptionConfiguration">EncryptionConfiguration</a>" : <i><a href="encryptionconfiguration.md">EncryptionConfiguration</a></i>,
        "<a href="#healthcheckconfiguration" title="HealthCheckConfiguration">HealthCheckConfiguration</a>" : <i><a href="healthcheckconfiguration.md">HealthCheckConfiguration</a></i>,
        "<a href="#autoscalingconfigurationarn" title="AutoScalingConfigurationArn">AutoScalingConfigurationArn</a>" : <i>String</i>,
        "<a href="#networkconfiguration" title="NetworkConfiguration">NetworkConfiguration</a>" : <i><a href="networkconfiguration.md">NetworkConfiguration</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AppRunner::Service
Properties:
    <a href="#servicename" title="ServiceName">ServiceName</a>: <i>String</i>
    <a href="#sourceconfiguration" title="SourceConfiguration">SourceConfiguration</a>: <i><a href="sourceconfiguration.md">SourceConfiguration</a></i>
    <a href="#instanceconfiguration" title="InstanceConfiguration">InstanceConfiguration</a>: <i><a href="instanceconfiguration.md">InstanceConfiguration</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#encryptionconfiguration" title="EncryptionConfiguration">EncryptionConfiguration</a>: <i><a href="encryptionconfiguration.md">EncryptionConfiguration</a></i>
    <a href="#healthcheckconfiguration" title="HealthCheckConfiguration">HealthCheckConfiguration</a>: <i><a href="healthcheckconfiguration.md">HealthCheckConfiguration</a></i>
    <a href="#autoscalingconfigurationarn" title="AutoScalingConfigurationArn">AutoScalingConfigurationArn</a>: <i>String</i>
    <a href="#networkconfiguration" title="NetworkConfiguration">NetworkConfiguration</a>: <i><a href="networkconfiguration.md">NetworkConfiguration</a></i>
</pre>

## Properties

#### ServiceName

The AppRunner Service Name.

_Required_: No

_Type_: String

_Minimum_: <code>4</code>

_Maximum_: <code>40</code>

_Pattern_: <code>[A-Za-z0-9][A-Za-z0-9-_]{3,39}</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### SourceConfiguration

Source Code configuration

_Required_: Yes

_Type_: <a href="sourceconfiguration.md">SourceConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InstanceConfiguration

Instance Configuration

_Required_: No

_Type_: <a href="instanceconfiguration.md">InstanceConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### EncryptionConfiguration

Encryption configuration (KMS key)

_Required_: No

_Type_: <a href="encryptionconfiguration.md">EncryptionConfiguration</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### HealthCheckConfiguration

Health check configuration

_Required_: No

_Type_: <a href="healthcheckconfiguration.md">HealthCheckConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AutoScalingConfigurationArn

Autoscaling configuration ARN

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1011</code>

_Pattern_: <code>arn:aws(-[\w]+)*:[a-z0-9-\\.]{0,63}:[a-z0-9-\\.]{0,63}:[0-9]{12}:(\w|\/|-){1,1011}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### NetworkConfiguration

Network configuration

_Required_: No

_Type_: <a href="networkconfiguration.md">NetworkConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ServiceArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ServiceArn

The Amazon Resource Name (ARN) of the AppRunner Service.

#### ServiceId

The AppRunner Service Id

#### ServiceUrl

The Service Url of the AppRunner Service.

#### Status

AppRunner Service status.
