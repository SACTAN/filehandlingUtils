package org.json.utils.yamlutils;

import java.util.Map;

/**
 * Utility for validating YAML file content.
 */
public class YamlFileValidator {
    /**
     * Validates that all keys in the map are non-empty strings.
     */
    public static boolean allKeysNonEmpty(Map<?, ?> map) {
        for (Object key : map.keySet()) {
            if (!(key instanceof String) || ((String) key).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
