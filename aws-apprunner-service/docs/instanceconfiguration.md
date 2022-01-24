# AWS::AppRunner::Service InstanceConfiguration

Instance Configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#cpu" title="Cpu">Cpu</a>" : <i>String</i>,
    "<a href="#memory" title="Memory">Memory</a>" : <i>String</i>,
    "<a href="#instancerolearn" title="InstanceRoleArn">InstanceRoleArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#cpu" title="Cpu">Cpu</a>: <i>String</i>
<a href="#memory" title="Memory">Memory</a>: <i>String</i>
<a href="#instancerolearn" title="InstanceRoleArn">InstanceRoleArn</a>: <i>String</i>
</pre>

## Properties

#### Cpu

CPU

_Required_: No

_Type_: String

_Minimum_: <code>4</code>

_Maximum_: <code>6</code>

_Pattern_: <code>1024|2048|(1|2) vCPU</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Memory

Memory

_Required_: No

_Type_: String

_Minimum_: <code>4</code>

_Maximum_: <code>4</code>

_Pattern_: <code>2048|3072|4096|(2|3|4) GB</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InstanceRoleArn

_Required_: No

_Type_: String

_Minimum_: <code>29</code>

_Maximum_: <code>102</code>

_Pattern_: <code>arn:(aws|aws-us-gov|aws-cn|aws-iso|aws-iso-b):iam::[0-9]{12}:role/[\w+=,.@-]{1,64}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
