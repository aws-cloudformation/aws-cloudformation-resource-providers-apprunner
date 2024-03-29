package software.amazon.apprunner.vpcingressconnection;

import software.amazon.cloudformation.proxy.StdCallbackContext;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {
    private boolean createStarted;
    private boolean deleteStarted;
    private boolean updateStarted;
}
