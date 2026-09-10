package com.example.connections;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;

public class RegistryConnection {
    public static Properties getProperties() {

        Dotenv dotenv = Dotenv.load();
        String schemaRegistryUrl = dotenv.get("SCHEMA_REGISTRY_URL");
        String schemaRegistryApiKey = dotenv.get("SCHEMA_REGISTRY_API_KEY");
        String schemaRegistryApiSecret = dotenv.get("SCHEMA_REGISTRY_API_SECRET");

        Properties props = new Properties();

        props.put(
                "schema.registry.url",
                schemaRegistryUrl
        );

        props.put(
                "basic.auth.credentials.source",
                "USER_INFO"
        );

        props.put(
                "basic.auth.user.info",
                schemaRegistryApiKey + ":" + schemaRegistryApiSecret
        );

        return props;
    }
}