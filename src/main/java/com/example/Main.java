package com.example;

import java.util.Properties;

import com.example.connections.KafkaConnection;
import com.example.producer.ProducerDefault;

public class Main {

    public static void main(String[] args) {
        Properties kafkaProps = KafkaConnection.getProperties();

        ProducerDefault producer = new ProducerDefault(kafkaProps);
        producer.run();
    }
}