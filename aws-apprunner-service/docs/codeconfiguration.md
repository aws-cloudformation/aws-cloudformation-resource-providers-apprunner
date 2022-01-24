# AWS::AppRunner::Service CodeConfiguration

Code Configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#configurationsource" title="ConfigurationSource">ConfigurationSource</a>" : <i>String</i>,
    "<a href="#codeconfigurationvalues" title="CodeConfigurationValues">CodeConfigurationValues</a>" : <i><a href="codeconfigurationvalues.md">CodeConfigurationValues</a></i>
}
</pre>

### YAML

<pre>
<a href="#configurationsource" title="ConfigurationSource">ConfigurationSource</a>: <i>String</i>
<a href="#codeconfigurationvalues" title="CodeConfigurationValues">CodeConfigurationValues</a>: <i><a href="codeconfigurationvalues.md">CodeConfigurationValues</a></i>
</pre>

## Properties

#### ConfigurationSource

Configuration Source

_Required_: Yes

_Type_: String

_Allowed Values_: <code>REPOSITORY</code> | <code>API</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### CodeConfigurationValues

Code Configuration Values

_Required_: No

_Type_: <a href="codeconfigurationvalues.md">CodeConfigurationValues</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
