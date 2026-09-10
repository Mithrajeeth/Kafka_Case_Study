package com.example.producer;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;

public class ProducerDefault {

    private final Properties kafkaProps;


    // constructor DI
    public ProducerDefault(Properties kafkaProps) {
        this.kafkaProps = kafkaProps; // just storing config, no I/O here
        
        /* Add Key Serializer and value serializer to kafkaProps */
    } 

    public void run() {
        try (AdminClient admin = AdminClient.create(kafkaProps)) {
            
            System.out.println("Producer called...");
            // add your logic to poduce data 
            

        } catch (Exception e) {
           
        }
    }
}