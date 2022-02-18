# AWS::AppRunner::Service CodeConfigurationValues

Code Configuration Values

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#runtime" title="Runtime">Runtime</a>" : <i>String</i>,
    "<a href="#buildcommand" title="BuildCommand">BuildCommand</a>" : <i>String</i>,
    "<a href="#startcommand" title="StartCommand">StartCommand</a>" : <i>String</i>,
    "<a href="#port" title="Port">Port</a>" : <i>String</i>,
    "<a href="#runtimeenvironmentvariables" title="RuntimeEnvironmentVariables">RuntimeEnvironmentVariables</a>" : <i>[ <a href="keyvaluepair.md">KeyValuePair</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#runtime" title="Runtime">Runtime</a>: <i>String</i>
<a href="#buildcommand" title="BuildCommand">BuildCommand</a>: <i>String</i>
<a href="#startcommand" title="StartCommand">StartCommand</a>: <i>String</i>
<a href="#port" title="Port">Port</a>: <i>String</i>
<a href="#runtimeenvironmentvariables" title="RuntimeEnvironmentVariables">RuntimeEnvironmentVariables</a>: <i>
      - <a href="keyvaluepair.md">KeyValuePair</a></i>
</pre>

## Properties

#### Runtime

Runtime

_Required_: Yes

_Type_: String

_Allowed Values_: <code>PYTHON_3</code> | <code>NODEJS_12</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BuildCommand

Build Command

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

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
