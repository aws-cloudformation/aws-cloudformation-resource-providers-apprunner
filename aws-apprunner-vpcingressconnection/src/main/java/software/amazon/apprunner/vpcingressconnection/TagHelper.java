package software.amazon.apprunner.vpcingressconnection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.services.apprunner.model.Tag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TagHelper {
    /**
     * convertToMap
     *
     * Converts a collection of Tag objects to a tag-name -> tag-value map.
     *
     * Note: Tag objects with null tag values will not be included in the output
     * map.
     *
     * @param tags Collection of tags to convert
     * @return Converted Map of tags
     */
    public static Map<String, String> convertToMap(final Collection<software.amazon.apprunner.vpcingressconnection.Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyMap();
        }
        return tags.stream()
                .filter(tag -> tag.getValue() != null)
                .collect(Collectors.toMap(
                        software.amazon.apprunner.vpcingressconnection.Tag::getKey,
                        software.amazon.apprunner.vpcingressconnection.Tag::getValue,
                        (oldValue, newValue) -> newValue));
    }

    /**
     * convertToSet
     *
     * Converts a tag map to a set of Tag objects.
     *
     * Note: Like convertToMap, convertToSet filters out value-less tag entries.
     *
     * @param tagMap Map of tags to convert
     * @return Set of Tag objects
     */
    public static Set<Tag> convertToSet(final Map<String, String> tagMap) {
        if (MapUtils.isEmpty(tagMap)) {
            return Collections.emptySet();
        }
        return tagMap.entrySet().stream()
                .filter(tag -> tag.getValue() != null)
                .map(tag -> Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * convertToList
     *
     * Converts a tag map to a set of Tag objects.
     *
     * Note: Like convertToMap, convertToList filters out value-less tag entries.
     *
     * @param tagMap Map of tags to convert
     * @return List of Tag objects
     */
    public static List<Tag> convertToList(final Map<String, String> tagMap) {
        if (MapUtils.isEmpty(tagMap)) {
            return Collections.emptyList();
        }
        return tagMap.entrySet().stream()
                .filter(tag -> tag.getValue() != null)
                .map(tag -> Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * generateTagsForCreate
     *
     * Generate tags to put into resource creation request.
     * This includes user defined tags and system tags as well.
     */
    public final Map<String, String> generateTagsForCreate(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> tagMap = new HashMap<>();

        if (handlerRequest.getDesiredResourceTags() != null) {
            tagMap.putAll(handlerRequest.getDesiredResourceTags());
        }

        tagMap.putAll(convertToMap(resourceModel.getTags()));
        return Collections.unmodifiableMap(tagMap);
    }

    /**
     * shouldUpdateTags
     *
     * Determines whether user defined tags have been changed during update.
     */
    public final boolean shouldUpdateTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        final Map<String, String> desiredTags = getNewDesiredTags(resourceModel, handlerRequest);
        return ObjectUtils.notEqual(previousTags, desiredTags);
    }

    /**
     * getPreviouslyAttachedTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get previous attached user defined tags from both handlerRequest.getPreviousResourceTags (stack tags)
     * and handlerRequest.getPreviousResourceState (resource tags).
     */
    public Map<String, String> getPreviouslyAttachedTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        // get previous stack level tags from handlerRequest
        final Map<String, String> previousTags = handlerRequest.getPreviousResourceTags() != null ?
                handlerRequest.getPreviousResourceTags() : Collections.emptyMap();

        previousTags.putAll(convertToMap(handlerRequest.getPreviousResourceState().getTags()));
        return previousTags;
    }

    /**
     * getNewDesiredTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get new user defined tags from both resource model and previous stack tags.
     */
    public Map<String, String> getNewDesiredTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        // get new stack level tags from handlerRequest
        final Map<String, String> desiredTags = handlerRequest.getDesiredResourceTags() != null ?
                handlerRequest.getDesiredResourceTags() : Collections.emptyMap();

        desiredTags.putAll(convertToMap(resourceModel.getTags()));
        return desiredTags;
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public Map<String, String> generateTagsToAdd(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        return desiredTags.entrySet().stream()
                .filter(e -> !previousTags.containsKey(e.getKey()) || !Objects.equals(previousTags.get(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    /**
     * getTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public Set<String> generateTagsToRemove(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        final Set<String> desiredTagNames = desiredTags.keySet();

        return previousTags.keySet().stream()
                .filter(tagName -> !desiredTagNames.contains(tagName))
                .collect(Collectors.toSet());
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public Set<Tag> generateTagsToAdd(final Set<Tag> previousTags, final Set<Tag> desiredTags) {
        return Sets.difference(new HashSet<>(desiredTags), new HashSet<>(previousTags));
    }

    /**
     * getTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public Set<Tag> generateTagsToRemove(final Set<Tag> previousTags, final Set<Tag> desiredTags) {
        return Sets.difference(new HashSet<>(previousTags), new HashSet<>(desiredTags));
    }


}
