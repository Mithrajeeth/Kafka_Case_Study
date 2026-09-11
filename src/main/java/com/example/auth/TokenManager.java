package com.example.auth;

import java.time.Instant;

import com.example.http.HttpRequests;
import com.fasterxml.jackson.databind.JsonNode;

public class TokenManager {

    private final HttpRequests http;
    private final String tokenPath;

    private String currentToken;
    private Instant expiresAt = Instant.MIN; // forces a fetch on first call

    public TokenManager(HttpRequests http, String tokenPath) {
        this.http = http;
        this.tokenPath = tokenPath;
    }

    public String getValidToken() throws Exception {
        // 5-second safety buffer before actual expiry
        if (currentToken == null || Instant.now().isAfter(expiresAt.minusSeconds(5))) {
            fetchNewToken();
        }
        return currentToken;
    }

    // Use when a 401 comes back unexpectedly - forces a refetch
    // regardless of what the local timer thinks.
    public String forceRefresh() throws Exception {
        fetchNewToken();
        return currentToken;
    }

    private void fetchNewToken() throws Exception {
        JsonNode json = http.post(tokenPath);

        currentToken = json.get("token").asText();

        // adjust this based on your server's actual response shape -
        // your Flask server returns "expires_in": "5 minutes" as a string,
        // so this is hardcoded here to match. If the server's expiry
        // config ever changes, this needs to change too.
        expiresAt = Instant.now().plusSeconds(5 * 60);

        System.out.println("Fetched new token, expires at: " + expiresAt);
    }
}