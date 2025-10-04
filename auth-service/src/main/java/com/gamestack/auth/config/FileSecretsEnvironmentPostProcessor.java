package com.gamestack.auth.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads environment variables that end with `_FILE` and loads the file content
 * into Spring Environment properties so applications can support Docker secrets
 * (which are provided as files) and the usual ENV variables.
 *
 * Example: if JWT_INTERNAL_SECRET_KEY_FILE=/run/secrets/jwt_internal then this
 * will read that file and set properties `jwt.internal.secret.key` and also
 * `jwt.internal.secret-key` (the latter matches existing property names used
 * in code like `${jwt.internal.secret-key}`).
 */
public class FileSecretsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "file-secrets-property-source";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> props = new HashMap<>();

        try {
            Map<String, String> env = System.getenv();
            for (Map.Entry<String, String> e : env.entrySet()) {
                String key = e.getKey();
                if (key != null && key.endsWith("_FILE")) {
                    String filePath = e.getValue();
                    if (filePath == null || filePath.isBlank()) {
                        continue;
                    }
                    try {
                        String content = Files.readString(Path.of(filePath)).trim();
                        String baseKey = key.substring(0, key.length() - 5); // remove _FILE
                        String propDots = baseKey.toLowerCase().replace('_', '.');
                        // Primary mapping (dots)
                        props.put(propDots, content);
                        // If property ends with .secret.key also expose .secret-key (matches existing properties)
                        if (propDots.endsWith(".secret.key")) {
                            String alt = propDots.substring(0, propDots.length() - ".secret.key".length()) + ".secret-key";
                            props.put(alt, content);
                        }
                    } catch (IOException io) {
                        System.err.println("[FileSecrets] Failed to read secret file for " + key + " -> " + filePath + ": " + io.getMessage());
                    }
                }
            }
        } catch (Throwable t) {
            // Defensive: do not fail application startup because of this helper
            System.err.println("[FileSecrets] Unexpected error while loading *_FILE secrets: " + t.getMessage());
        }

        if (!props.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
        }
    }

    @Override
    public int getOrder() {
        // Run early
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
