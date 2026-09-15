package com.example.producer;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;

public class ProducerDefault {

    private final Properties kafkaProps;

    public ProducerDefault(Properties kafkaProps) {
        this.kafkaProps = kafkaProps;
    }

    public void run() {
        System.out.println("Producer called...");

        AdminClient admin = null;
        try {
            admin = AdminClient.create(kafkaProps);
            System.out.println("AdminClient created successfully.");

            // your produce logic here

        } catch (Exception e) {
            System.out.println("Producer run failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (admin != null) {
                admin.close(Duration.ofSeconds(5));
            }
        }
    }
}