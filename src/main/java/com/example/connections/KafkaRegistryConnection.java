package com.example.connections;

import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class KafkaRegistryConnection {

    public static Properties getProperties() {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String registryUrl = getEnvOrDotenv("SCHEMA_REGISTRY_URL", dotenv);
        String registryApiKey = getEnvOrDotenv("SCHEMA_REGISTRY_API_KEY", dotenv);
        String registryApiSecret = getEnvOrDotenv("SCHEMA_REGISTRY_API_SECRET", dotenv);

        Properties props = new Properties();

        props.put("schema.registry.url", registryUrl);
        props.put("basic.auth.credentials.source", "USER_INFO");
        props.put("basic.auth.user.info", registryApiKey + ":" + registryApiSecret);

        return props;
    }

    private static String getEnvOrDotenv(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        return (value != null) ? value : dotenv.get(key);
    }
}