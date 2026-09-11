package com.example.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequests {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String baseUrl;

    public HttpRequests(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ---------- GET ----------

    public JsonNode get(String path) throws Exception {
        return get(path, null);
    }

    public JsonNode get(String path, String bearerToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET();

        attachAuthIfPresent(builder, bearerToken);

        return send(builder.build());
    }

    // ---------- POST ----------

    public JsonNode post(String path) throws Exception {
        return post(path, null, null);
    }

    public JsonNode post(String path, String bearerToken) throws Exception {
        return post(path, bearerToken, null);
    }

    public JsonNode post(String path, String bearerToken, Object body) throws Exception {
        BodyPublisher bodyPublisher = (body == null)
                ? BodyPublishers.noBody()
                : BodyPublishers.ofString(mapper.writeValueAsString(body));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(bodyPublisher);

        attachAuthIfPresent(builder, bearerToken);

        return send(builder.build());
    }

    // ---------- shared internals ----------

    private void attachAuthIfPresent(HttpRequest.Builder builder, String bearerToken) {
        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }
    }

    private JsonNode send(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + " from "
                    + request.uri() + " - body: " + response.body());
        }

        return mapper.readTree(response.body());
    }
}