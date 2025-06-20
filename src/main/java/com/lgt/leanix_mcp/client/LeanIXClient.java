package com.lgt.leanix_mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class LeanIXClient {

    private final String subdomain;
    private final String baseUrl;
    private final String graphqlEndpoint;
    private final String tokenEndpoint;
    private final String apiToken;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LeanIXClient(String subdomain, String apiToken) {
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new IllegalArgumentException("Subdomain is required");
        }
        if (apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalArgumentException("API token is required");
        }

        this.subdomain = subdomain;
        this.apiToken = apiToken;
        this.baseUrl = String.format("https://%s.leanix.net", subdomain);
        this.graphqlEndpoint = String.format("%s/services/pathfinder/v1/graphql", baseUrl);
        this.tokenEndpoint = String.format("%s/services/mtm/v1/oauth2/token", baseUrl);
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets an access token using the client credentials flow
     *
     * @return Access token
     */
    public String getAccessToken() {
        String credentials = String.format("apitoken:%s", apiToken);
        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        log.info("[LeanIXClient] Attempting to get access token from: {}", tokenEndpoint);
        log.info("[LeanIXClient] Using API token: {}", apiToken != null ? "******** (present)" : "MISSING");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Basic %s", basicAuth));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("[LeanIXClient] Failed to get access token. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to get access token: " + response.getStatusCode());
            }
            JsonNode json = objectMapper.readTree(response.getBody());
            String token = json.get("access_token").asText();
            log.debug("[LeanIXClient] Successfully obtained access token");
            return token;
        } catch (HttpStatusCodeException e) {
            log.error("[LeanIXClient] Failed to get access token. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get access token: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("[LeanIXClient] Error getting access token", e);
            throw new RuntimeException("Error getting access token", e);
        }
    }

    /**
     * Executes a GraphQL query
     *
     * @param query     The GraphQL query string
     * @param variables Variables for the query (can be null)
     * @return Query result as JsonNode
     */
    public JsonNode query(String query, Map<String, Object> variables) {
        String accessToken = getAccessToken();
        GraphQLRequest requestPayload = new GraphQLRequest(query, variables);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GraphQLRequest> request = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(graphqlEndpoint, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("[LeanIXClient] GraphQL query failed. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("GraphQL query failed: " + response.getStatusCode());
            }
            JsonNode result = objectMapper.readTree(response.getBody());
            log.debug("[LeanIXClient] GraphQL query executed successfully");
            return result;
        } catch (HttpStatusCodeException e) {
            log.error("[LeanIXClient] GraphQL query failed. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("GraphQL query failed: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("[LeanIXClient] Error executing GraphQL query", e);
            throw new RuntimeException("Error executing GraphQL query", e);
        }
    }

    /**
     * Executes a GraphQL query without variables
     *
     * @param query The GraphQL query string
     * @return Query result as JsonNode
     */
    public JsonNode query(String query) {
        return query(query, null);
    }

    // Getters for testing and configuration
    public String getSubdomain() {
        return subdomain;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getGraphqlEndpoint() {
        return graphqlEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    /**
     * Inner class to represent GraphQL request payload
     */
    private static class GraphQLRequest {
        private final String query;
        private final Map<String, Object> variables;

        public GraphQLRequest(String query, Map<String, Object> variables) {
            this.query = query;
            this.variables = variables;
        }

        @SuppressWarnings("unused")
        public String getQuery() {
            return query;
        }

        @SuppressWarnings("unused")
        public Map<String, Object> getVariables() {
            return variables;
        }
    }
}
