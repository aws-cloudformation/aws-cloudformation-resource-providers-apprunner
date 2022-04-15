# AWS::AppRunner::ObservabilityConfiguration TraceConfiguration

Describes the configuration of the tracing feature within an AWS App Runner observability configuration.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#vendor" title="Vendor">Vendor</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#vendor" title="Vendor">Vendor</a>: <i>String</i>
</pre>

## Properties

#### Vendor

The implementation provider chosen for tracing App Runner services.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>AWSXRAY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

