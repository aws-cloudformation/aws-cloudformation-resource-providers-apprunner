# AWS::AppRunner::Service EncryptionConfiguration

Encryption configuration (KMS key)

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#kmskey" title="KmsKey">KmsKey</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#kmskey" title="KmsKey">KmsKey</a>: <i>String</i>
</pre>

## Properties

#### KmsKey

The KMS Key

_Required_: Yes

_Type_: String

_Maximum_: <code>256</code>

_Pattern_: <code>arn:aws(-[\w]+)*:kms:[a-z\-]+-[0-9]{1}:[0-9]{12}:key\/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
