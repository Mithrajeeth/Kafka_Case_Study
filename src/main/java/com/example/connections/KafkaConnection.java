package com.example.connections;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class KafkaConnection {
     public static Properties getProperties() {

        // ignoreIfMissing() prevents a crash when no .env file exists,
        // which is the case inside the Docker container
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Check real environment variables first (this is what Docker's
        // --env-file / -e flags, and later Azure Container Apps, provide).
        // Fall back to .env file values for local runs outside Docker.
        String bootstrapServers = getEnvOrDotenv("KAFKA_BOOTSTRAP_SERVERS", dotenv);
        String apiKey = getEnvOrDotenv("KAFKA_API_KEY", dotenv);
        String apiSecret = getEnvOrDotenv("KAFKA_API_SECRET", dotenv);

        System.out.println("Bootstrap: " + bootstrapServers);
        System.out.println("API Key present: " + (apiKey != null));

        Properties props = new Properties();

        props.put(
                "bootstrap.servers",
                bootstrapServers
        );

        props.put(
                "security.protocol",
                "SASL_SSL"
        );

        props.put(
                "sasl.mechanism",
                "PLAIN"
        );

        props.put(
                "sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + apiKey + "\" " +
                "password=\"" + apiSecret + "\";"
        );

        return props;
    }

    private static String getEnvOrDotenv(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        return (value != null) ? value : dotenv.get(key);
    }
}