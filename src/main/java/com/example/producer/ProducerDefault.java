package com.example.producer;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;

public class ProducerDefault {

    private final AdminClient admin;

    public ProducerDefault(Properties kafkaProps) {
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.admin = AdminClient.create(kafkaProps); // created ONCE, at startup, on main thread
    }

    public void run() {
        System.out.println("Producer called...");
        try {
            System.out.println("Topics visible: " + admin.listTopics().names().get());
            // your produce logic here

        } catch (Exception e) {
            System.out.println("Producer run failed: " + e.getMessage());
            e.printStackTrace();
        }
        // no admin.close() here anymore - it lives for the whole app lifetime now
    }
}