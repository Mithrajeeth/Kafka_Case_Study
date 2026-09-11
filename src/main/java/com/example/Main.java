package com.example;

import com.example.auth.TokenManager;
import com.example.http.HttpRequests;
import com.example.services.ApiFetchService;
import com.fasterxml.jackson.databind.JsonNode;

public class Main {

    public static void main(String[] args) throws Exception {
        HttpRequests app = new HttpRequests("http://localhost:5000");
        TokenManager tokenManager = new TokenManager(app, "/token");
        ApiFetchService fetchService = new ApiFetchService(app, tokenManager, "/events");

        JsonNode events = fetchService.fetchData();
        System.out.println(events);
    }
}