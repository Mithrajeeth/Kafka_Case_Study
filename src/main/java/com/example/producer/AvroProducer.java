package com.example.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.avro.generic.GenericRecord;

public class AvroProducer {
    final KafkaProducer<String,GenericRecord> producer;
    AvroProducer( Properties kafkaRegistryProps) {

        Properties properties = kafkaRegistryProps;
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
       
        this.producer= new KafkaProducer<>(properties);

    }



}
