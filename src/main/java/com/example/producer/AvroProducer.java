package com.example.producer;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.example.services.ApiFetchService;
import com.example.services.AvroSchemaService;
import com.example.services.RecordMapService;
import com.fasterxml.jackson.databind.JsonNode;

public class AvroProducer {
    final KafkaProducer<String,GenericRecord> producer;
    public ApiFetchService apiFetchService;
    public AvroProducer( Properties kafkaRegistryProps, ApiFetchService apiFetchService){ 

        Properties properties = kafkaRegistryProps;
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
       
        this.producer= new KafkaProducer<>(properties);
        this.apiFetchService=apiFetchService;

    }

    public void produce() throws Exception {

            JsonNode data=apiFetchService.fetchData();
            
            int min=Math.min(data.size(),10);
            List<JsonNode> sample=new ArrayList<>();

            for(int i=0;i<min;i++){
                sample.add(data.get(i));
            }


            AvroSchemaService avroSchemaService=new AvroSchemaService();
            Schema s=avroSchemaService.inferSchemaFromSamples(sample, "events", "com.example.avro");
            System.out.println(s.toString(true));

            RecordMapService recordMapService=new RecordMapService();

            
            for(JsonNode eventNode: data){
                GenericRecord record=recordMapService.map(eventNode,s);
                String key=eventNode.get("event_id").asText();

                producer.send(new ProducerRecord<>("Events",key,record),(metadata, exception) -> {
                    if (exception != null) {
                        System.out.println("FAILED id=" + key);
                        exception.printStackTrace();
                    } else {
                        System.out.println("Sent id=" + key + " -> partition=" + metadata.partition()
                                + " offset=" + metadata.offset());
                    }
                });
            }


        
    


    }

}
