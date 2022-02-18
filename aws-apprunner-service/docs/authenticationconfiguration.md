# AWS::AppRunner::Service AuthenticationConfiguration

Authentication Configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#connectionarn" title="ConnectionArn">ConnectionArn</a>" : <i>String</i>,
    "<a href="#accessrolearn" title="AccessRoleArn">AccessRoleArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#connectionarn" title="ConnectionArn">ConnectionArn</a>: <i>String</i>
<a href="#accessrolearn" title="AccessRoleArn">AccessRoleArn</a>: <i>String</i>
</pre>

## Properties

#### ConnectionArn

Connection Arn

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1011</code>

_Pattern_: <code>arn:aws(-[\w]+)*:[a-z0-9-\\.]{0,63}:[a-z0-9-\\.]{0,63}:[0-9]{12}:(\w|\/|-){1,1011}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AccessRoleArn

_Required_: No

_Type_: String

_Minimum_: <code>29</code>

_Maximum_: <code>102</code>

_Pattern_: <code>arn:(aws|aws-us-gov|aws-cn|aws-iso|aws-iso-b):iam::[0-9]{12}:role/[\w+=,.@-]{1,64}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
