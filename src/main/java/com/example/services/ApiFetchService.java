package com.example.services;

import com.example.auth.TokenManager;
import com.example.http.HttpRequests;
import com.fasterxml.jackson.databind.JsonNode;

public class ApiFetchService {

    private final HttpRequests http;
    private final TokenManager tokenManager;
    private final String dataPath;

    public ApiFetchService(HttpRequests http, TokenManager tokenManager, String dataPath) {
        this.http = http;
        this.tokenManager = tokenManager;
        this.dataPath = dataPath;
    }

    public  JsonNode fetchData() throws Exception {
        String token = tokenManager.getValidToken();

        try {
            JsonNode root= http.get(dataPath, token);
            return root;

        } catch (RuntimeException e) {
            // HttpRequests throws on 4xx/5xx - check if it was specifically a 401
            // (token expired unexpectedly, e.g. clock drift), and retry once with a forced refresh
            if (e.getMessage() != null && e.getMessage().contains("HTTP 401")) {
                System.out.println("Got 401, forcing token refresh and retrying once...");
                String freshToken = tokenManager.forceRefresh();
                return http.get(dataPath, freshToken);
            }
            throw e; // any other failure, don't swallow it - let it propagate
        }
    }
}