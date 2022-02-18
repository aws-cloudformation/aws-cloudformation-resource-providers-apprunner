# AWS::AppRunner::Service HealthCheckConfiguration

Health check configuration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#protocol" title="Protocol">Protocol</a>" : <i>String</i>,
    "<a href="#path" title="Path">Path</a>" : <i>String</i>,
    "<a href="#interval" title="Interval">Interval</a>" : <i>Integer</i>,
    "<a href="#timeout" title="Timeout">Timeout</a>" : <i>Integer</i>,
    "<a href="#healthythreshold" title="HealthyThreshold">HealthyThreshold</a>" : <i>Integer</i>,
    "<a href="#unhealthythreshold" title="UnhealthyThreshold">UnhealthyThreshold</a>" : <i>Integer</i>
}
</pre>

### YAML

<pre>
<a href="#protocol" title="Protocol">Protocol</a>: <i>String</i>
<a href="#path" title="Path">Path</a>: <i>String</i>
<a href="#interval" title="Interval">Interval</a>: <i>Integer</i>
<a href="#timeout" title="Timeout">Timeout</a>: <i>Integer</i>
<a href="#healthythreshold" title="HealthyThreshold">HealthyThreshold</a>: <i>Integer</i>
<a href="#unhealthythreshold" title="UnhealthyThreshold">UnhealthyThreshold</a>: <i>Integer</i>
</pre>

## Properties

#### Protocol

Health Check Protocol

_Required_: No

_Type_: String

_Allowed Values_: <code>TCP</code> | <code>HTTP</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Path

Health check Path

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Interval

Health check Interval

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Timeout

Health check Timeout

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### HealthyThreshold

Health check Healthy Threshold

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### UnhealthyThreshold

Health check Unhealthy Threshold

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
