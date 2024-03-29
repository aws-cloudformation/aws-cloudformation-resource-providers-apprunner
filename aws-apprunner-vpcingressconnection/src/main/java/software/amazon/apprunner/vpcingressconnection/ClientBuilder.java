package software.amazon.apprunner.vpcingressconnection;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    public static AppRunnerClient getClient() {
        return AppRunnerClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
    }
}
