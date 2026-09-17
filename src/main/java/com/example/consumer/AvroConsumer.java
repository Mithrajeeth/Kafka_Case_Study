package com.example.consumer;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AvroConsumer {

    private final KafkaConsumer<String, GenericRecord> consumer;

    public AvroConsumer(Properties kafkaRegistryProps) {

        kafkaRegistryProps.put(
                "key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer"
        );

        kafkaRegistryProps.put(
                "value.deserializer",
                "io.confluent.kafka.serializers.KafkaAvroDeserializer"
        );

        kafkaRegistryProps.put(
                "group.id",
                "avro-consumer-group"
        );

        kafkaRegistryProps.put(
                "auto.offset.reset",
                "earliest"
        );

        kafkaRegistryProps.put(
                "specific.avro.reader",
                "false"
        );

        this.consumer = new KafkaConsumer<>(kafkaRegistryProps);

        this.consumer.subscribe(
                Collection.singletonList("Events")
        );
    }

    public void consume() {

        try {

            while (true) {

                ConsumerRecords<String, GenericRecord> records =
                        consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, GenericRecord> record : records) {

                    System.out.println("Topic: " + record.topic());
                    System.out.println("Partition: " + record.partition());
                    System.out.println("Offset: " + record.offset());
                    System.out.println("Key: " + record.key());
                    System.out.println("Value: " + record.value());

                    System.out.println("--------------------------------");
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            consumer.close();
        }
    }
}