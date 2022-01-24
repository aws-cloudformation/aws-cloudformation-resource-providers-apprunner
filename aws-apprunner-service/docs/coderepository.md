# AWS::AppRunner::Service CodeRepository

Source Code Repository

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#repositoryurl" title="RepositoryUrl">RepositoryUrl</a>" : <i>String</i>,
    "<a href="#sourcecodeversion" title="SourceCodeVersion">SourceCodeVersion</a>" : <i><a href="sourcecodeversion.md">SourceCodeVersion</a></i>,
    "<a href="#codeconfiguration" title="CodeConfiguration">CodeConfiguration</a>" : <i><a href="codeconfiguration.md">CodeConfiguration</a></i>
}
</pre>

### YAML

<pre>
<a href="#repositoryurl" title="RepositoryUrl">RepositoryUrl</a>: <i>String</i>
<a href="#sourcecodeversion" title="SourceCodeVersion">SourceCodeVersion</a>: <i><a href="sourcecodeversion.md">SourceCodeVersion</a></i>
<a href="#codeconfiguration" title="CodeConfiguration">CodeConfiguration</a>: <i><a href="codeconfiguration.md">CodeConfiguration</a></i>
</pre>

## Properties

#### RepositoryUrl

Repository Url

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceCodeVersion

Source Code Version

_Required_: Yes

_Type_: <a href="sourcecodeversion.md">SourceCodeVersion</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### CodeConfiguration

Code Configuration

_Required_: No

_Type_: <a href="codeconfiguration.md">CodeConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
