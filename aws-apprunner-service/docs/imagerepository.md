# AWS::AppRunner::Service ImageRepository

Image Repository

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#imageidentifier" title="ImageIdentifier">ImageIdentifier</a>" : <i>String</i>,
    "<a href="#imageconfiguration" title="ImageConfiguration">ImageConfiguration</a>" : <i><a href="imageconfiguration.md">ImageConfiguration</a></i>,
    "<a href="#imagerepositorytype" title="ImageRepositoryType">ImageRepositoryType</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#imageidentifier" title="ImageIdentifier">ImageIdentifier</a>: <i>String</i>
<a href="#imageconfiguration" title="ImageConfiguration">ImageConfiguration</a>: <i><a href="imageconfiguration.md">ImageConfiguration</a></i>
<a href="#imagerepositorytype" title="ImageRepositoryType">ImageRepositoryType</a>: <i>String</i>
</pre>

## Properties

#### ImageIdentifier

Image Identifier

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1024</code>

_Pattern_: <code>([0-9]{12}.dkr.ecr.[a-z\-]+-[0-9]{1}.amazonaws.com\/.*)|(^public\.ecr\.aws\/.+\/.+)</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ImageConfiguration

Image Configuration

_Required_: No

_Type_: <a href="imageconfiguration.md">ImageConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ImageRepositoryType

Image Repository Type

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ECR</code> | <code>ECR_PUBLIC</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
