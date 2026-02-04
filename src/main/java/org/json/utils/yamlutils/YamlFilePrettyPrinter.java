package com.example.yamlutils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.StringWriter;
import java.util.Map;

/**
 * Utility for pretty-printing YAML file content.
 */
public class YamlFilePrettyPrinter {
    private static final Yaml yaml;
    static {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    /**
     * Returns a pretty-printed YAML string from a map or list.
     */
    public static String prettyPrint(Object data) {
        StringWriter sw = new StringWriter();
        yaml.dump(data, sw);
        return sw.toString();
    }
}
