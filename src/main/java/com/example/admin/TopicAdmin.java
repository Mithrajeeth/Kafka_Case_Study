package com.example.admin;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import com.example.connections.KafkaConnection;

public class TopicAdmin {

    public static void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {

        Properties props = KafkaConnection.getProperties();

        try (AdminClient admin = AdminClient.create(props)) {

            
            ListTopicsResult existing = admin.listTopics();
            System.out.println("Topics visible on this cluster: " + existing.names().get());

            if (existing.names().get().contains(topicName)) {
                System.out.println("Topic '" + topicName + "' already exists. Skipping creation.");
                return;
            }

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            admin.createTopics(Collections.singletonList(newTopic)).all().get();

            System.out.println("Topic '" + topicName + "' created successfully.");

        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error while creating/checking topic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createTopicIfNotExists("posts-topic", 1, (short) 3); 
    }
}