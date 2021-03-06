package software.amazon.apprunner.vpcconnector;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-apprunner-vpcconnector.json");
    }

    /**
     * Providers should implement this method if their resource has a 'Tags' property to define
     * resource-level tags
     */
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        if (resourceModel.getTags() == null) {
            return null;
        } else {
            return resourceModel.getTags().stream().collect(Collectors.toMap(tag -> tag.getKey(), tag -> tag.getValue()));
        }
    }
}
