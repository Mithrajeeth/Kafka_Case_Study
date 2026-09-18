package com.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import com.example.auth.TokenManager;
import com.example.connections.KafkaConnection;
import com.example.connections.KafkaRegistryConnection;
import com.example.services.ApiFetchService;
import com.example.http.HttpRequests;
import com.example.producer.AvroProducer;
import com.example.producer.ProducerDefault;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;


public class Main {

    private static final int PORT = 8484;

    public static void main(String[] args) throws IOException {

        // ---- Wire up dependencies once, at startup ----
        HttpRequests app = new HttpRequests("http://localhost:5000");
        TokenManager tokenManager = new TokenManager(app, "/token");
        ApiFetchService apiFetchService = new ApiFetchService(app, tokenManager, "/events");

        var kafkaProps = KafkaConnection.getProperties();
        var kafkaRegistryProps = KafkaRegistryConnection.getProperties();

        // Merge: AvroProducer needs BOTH bootstrap.servers/SASL config (kafkaProps)
        // AND schema.registry.url/auth (kafkaRegistryProps) to work correctly
        Properties mergedProps = new Properties();
        mergedProps.putAll(kafkaProps);
        mergedProps.putAll(kafkaRegistryProps);

        ProducerDefault producer = new ProducerDefault(kafkaProps);
        AvroProducer avroProducer = new AvroProducer(mergedProps, apiFetchService);

        // ---- Start the HTTP server ----
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

        server.createContext("/produce", exchange -> handleProduce(exchange, producer, apiFetchService, avroProducer));
        server.createContext("/health", Main::handleHealth);
        server.createContext("/consume", a -> handleConsume(a));

        server.setExecutor(null);
        server.start();

        System.out.println("Server listening on port " + PORT + "...");
        System.out.println("Trigger the job:  GET http://localhost:" + PORT + "/produce");
    }

    private static void handleProduce(HttpExchange exchange, ProducerDefault producer, ApiFetchService apiFetchService, AvroProducer avroProcuder) throws IOException {
        String responseText;
        int statusCode;

        try {
           // avroProcuder.produce();
            responseText = "Producer job completed successfully.";
            statusCode = 200;

        } catch (Exception e) {
            responseText = "Producer job failed: " + e.getMessage();
            statusCode = 500;
            e.printStackTrace();
        }

        sendResponse(exchange, statusCode, responseText);
    }

    private static void handleConsume(HttpExchange a) throws IOException {
        sendResponse(a, 200, "Consumer invoked");
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, "OK");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}