# AWS::AppRunner::ObservabilityConfiguration

The AWS::AppRunner::ObservabilityConfiguration resource  is an AWS App Runner resource type that specifies an App Runner observability configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AppRunner::ObservabilityConfiguration",
    "Properties" : {
        "<a href="#observabilityconfigurationname" title="ObservabilityConfigurationName">ObservabilityConfigurationName</a>" : <i>String</i>,
        "<a href="#traceconfiguration" title="TraceConfiguration">TraceConfiguration</a>" : <i><a href="traceconfiguration.md">TraceConfiguration</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AppRunner::ObservabilityConfiguration
Properties:
    <a href="#observabilityconfigurationname" title="ObservabilityConfigurationName">ObservabilityConfigurationName</a>: <i>String</i>
    <a href="#traceconfiguration" title="TraceConfiguration">TraceConfiguration</a>: <i><a href="traceconfiguration.md">TraceConfiguration</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### ObservabilityConfigurationName

A name for the observability configuration. When you use it for the first time in an AWS Region, App Runner creates revision number 1 of this name. When you use the same name in subsequent calls, App Runner creates incremental revisions of the configuration.

_Required_: No

_Type_: String

_Minimum_: <code>4</code>

_Maximum_: <code>32</code>

_Pattern_: <code>[A-Za-z0-9][A-Za-z0-9\-_]{3,31}</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TraceConfiguration

Describes the configuration of the tracing feature within an AWS App Runner observability configuration.

_Required_: No

_Type_: <a href="traceconfiguration.md">TraceConfiguration</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

A list of metadata items that you can associate with your observability configuration resource. A tag is a key-value pair.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ObservabilityConfigurationArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ObservabilityConfigurationArn

The Amazon Resource Name (ARN) of this ObservabilityConfiguration

#### ObservabilityConfigurationRevision

The revision of this observability configuration. It's unique among all the active configurations ('Status': 'ACTIVE') that share the same ObservabilityConfigurationName.

#### Latest

It's set to true for the configuration with the highest Revision among all configurations that share the same Name. It's set to false otherwise.

