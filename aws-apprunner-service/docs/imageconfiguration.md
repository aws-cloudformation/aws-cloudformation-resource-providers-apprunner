# AWS::AppRunner::Service ImageConfiguration

Image Configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#startcommand" title="StartCommand">StartCommand</a>" : <i>String</i>,
    "<a href="#port" title="Port">Port</a>" : <i>String</i>,
    "<a href="#runtimeenvironmentvariables" title="RuntimeEnvironmentVariables">RuntimeEnvironmentVariables</a>" : <i>[ <a href="keyvaluepair.md">KeyValuePair</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#startcommand" title="StartCommand">StartCommand</a>: <i>String</i>
<a href="#port" title="Port">Port</a>: <i>String</i>
<a href="#runtimeenvironmentvariables" title="RuntimeEnvironmentVariables">RuntimeEnvironmentVariables</a>: <i>
      - <a href="keyvaluepair.md">KeyValuePair</a></i>
</pre>

## Properties

#### StartCommand

Start Command

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Port

Port

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuntimeEnvironmentVariables

_Required_: No

_Type_: List of <a href="keyvaluepair.md">KeyValuePair</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
