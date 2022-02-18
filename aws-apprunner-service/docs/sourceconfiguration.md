# AWS::AppRunner::Service SourceConfiguration

Source Code configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#coderepository" title="CodeRepository">CodeRepository</a>" : <i><a href="coderepository.md">CodeRepository</a></i>,
    "<a href="#imagerepository" title="ImageRepository">ImageRepository</a>" : <i><a href="imagerepository.md">ImageRepository</a></i>,
    "<a href="#autodeploymentsenabled" title="AutoDeploymentsEnabled">AutoDeploymentsEnabled</a>" : <i>Boolean</i>,
    "<a href="#authenticationconfiguration" title="AuthenticationConfiguration">AuthenticationConfiguration</a>" : <i><a href="authenticationconfiguration.md">AuthenticationConfiguration</a></i>
}
</pre>

### YAML

<pre>
<a href="#coderepository" title="CodeRepository">CodeRepository</a>: <i><a href="coderepository.md">CodeRepository</a></i>
<a href="#imagerepository" title="ImageRepository">ImageRepository</a>: <i><a href="imagerepository.md">ImageRepository</a></i>
<a href="#autodeploymentsenabled" title="AutoDeploymentsEnabled">AutoDeploymentsEnabled</a>: <i>Boolean</i>
<a href="#authenticationconfiguration" title="AuthenticationConfiguration">AuthenticationConfiguration</a>: <i><a href="authenticationconfiguration.md">AuthenticationConfiguration</a></i>
</pre>

## Properties

#### CodeRepository

Source Code Repository

_Required_: No

_Type_: <a href="coderepository.md">CodeRepository</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ImageRepository

Image Repository

_Required_: No

_Type_: <a href="imagerepository.md">ImageRepository</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AutoDeploymentsEnabled

Auto Deployment enabled

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AuthenticationConfiguration

Authentication Configuration

_Required_: No

_Type_: <a href="authenticationconfiguration.md">AuthenticationConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
