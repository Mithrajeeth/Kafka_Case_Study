package com.example.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerDefault {

    private final KafkaConsumer<String, String> consumer;

    private static final String TOPIC = "orders";

    public ConsumerDefault(Properties kafkaProps) {

        kafkaProps.put(
            "key.deserializer",
            "org.apache.kafka.common.serialization.StringDeserializer"
        );

        kafkaProps.put(
            "value.deserializer",
            "org.apache.kafka.common.serialization.StringDeserializer"
        );

        kafkaProps.put(
            "group.id",
            "default-consumer-group"
        );

        kafkaProps.put(
            "auto.offset.reset",
            "earliest"
        );

        this.consumer = new KafkaConsumer<>(kafkaProps);
        this.consumer.subscribe(Collections.singletonList(TOPIC));
    }

    public void run() {

        System.out.println("Consumer called...");

        try {

            while (true) {

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {

                    System.out.println(
                        "Received key=" + record.key()
                        + ", value=" + record.value()
                        + ", partition=" + record.partition()
                        + ", offset=" + record.offset()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                "Consumer run failed: " + e.getMessage()
            );

            e.printStackTrace();

        } finally {

            consumer.close();
        }
    }
}

