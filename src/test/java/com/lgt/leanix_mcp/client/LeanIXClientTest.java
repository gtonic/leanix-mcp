package com.lgt.leanix_mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class LeanIXClientTest {

    private static final String SUBDOMAIN = "lgt-test";
    private static final String API_TOKEN = "test-token";
    private static final String BASE_URL = "https://lgt-test.leanix.net";
    private static final String GRAPHQL_ENDPOINT = BASE_URL + "/services/pathfinder/v1/graphql";
    private static final String TOKEN_ENDPOINT = BASE_URL + "/services/mtm/v1/oauth2/token";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private LeanIXClient client;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        client = new LeanIXClient(SUBDOMAIN, API_TOKEN);
        setField(client, "restTemplate", restTemplate);
        setField(client, "objectMapper", objectMapper);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    // Helper to set private fields via reflection, searching superclasses
    private static void setField(Object target, String fieldName, Object value) {
        try {
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(SUBDOMAIN, client.getSubdomain());
        assertEquals(BASE_URL, client.getBaseUrl());
        assertEquals(GRAPHQL_ENDPOINT, client.getGraphqlEndpoint());
        assertEquals(TOKEN_ENDPOINT, client.getTokenEndpoint());
    }

    @Test
    void testGetAccessTokenSuccess() throws Exception {
        String accessToken = "mock-access-token";
        String responseBody = "{\"access_token\":\"" + accessToken + "\"}";

        String expectedAuth = "Basic "
                + Base64.getEncoder().encodeToString(("apitoken:" + API_TOKEN).getBytes(StandardCharsets.UTF_8));

        mockServer.expect(requestTo(TOKEN_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, expectedAuth))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        String token = client.getAccessToken();
        assertEquals(accessToken, token);

        mockServer.verify();
    }

    @Test
    void testGetAccessTokenFailure() {
        String expectedAuth = "Basic "
                + Base64.getEncoder().encodeToString(("apitoken:" + API_TOKEN).getBytes(StandardCharsets.UTF_8));

        mockServer.expect(requestTo(TOKEN_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, expectedAuth))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("Unauthorized").contentType(MediaType.TEXT_PLAIN));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> client.getAccessToken());
        assertTrue(thrown.getMessage().contains("Failed to get access token"));

        mockServer.verify();
    }

    @Test
    void testQuerySuccess() throws Exception {
        String accessToken = "mock-access-token";
        String graphqlResponse = "{\"data\":{\"foo\":\"bar\"}}";
        String graphqlQuery = "query { foo }";
        Map<String, Object> variables = new HashMap<>();
        variables.put("var1", "value1");

        // Patch getAccessToken to return our mock token
        setField(client, "apiToken", API_TOKEN);
        LeanIXClient spyClient = new LeanIXClient(SUBDOMAIN, API_TOKEN) {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
        setField(spyClient, "restTemplate", restTemplate);
        setField(spyClient, "objectMapper", objectMapper);
        MockRestServiceServer localMockServer = MockRestServiceServer.createServer(restTemplate);

        localMockServer.expect(requestTo(GRAPHQL_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andRespond(withSuccess(graphqlResponse, MediaType.APPLICATION_JSON));

        JsonNode result = spyClient.query(graphqlQuery, variables);
        assertNotNull(result);
        assertEquals("bar", result.get("data").get("foo").asText());

        localMockServer.verify();
    }

    @Test
    void testQueryFailure() {
        String accessToken = "mock-access-token";
        String graphqlQuery = "query { foo }";

        LeanIXClient spyClient = new LeanIXClient(SUBDOMAIN, API_TOKEN) {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
        setField(spyClient, "restTemplate", restTemplate);
        setField(spyClient, "objectMapper", objectMapper);
        MockRestServiceServer localMockServer = MockRestServiceServer.createServer(restTemplate);

        localMockServer.expect(requestTo(GRAPHQL_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body("Bad Request").contentType(MediaType.TEXT_PLAIN));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> spyClient.query(graphqlQuery, null));
        assertTrue(thrown.getMessage().contains("GraphQL query failed"));

        localMockServer.verify();
    }

    @Test
    void testQueryWithoutVariablesDelegates() throws Exception {
        String accessToken = "mock-access-token";
        String graphqlResponse = "{\"data\":{\"foo\":\"bar\"}}";
        String graphqlQuery = "query { foo }";

        LeanIXClient spyClient = new LeanIXClient(SUBDOMAIN, API_TOKEN) {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
        setField(spyClient, "restTemplate", restTemplate);
        setField(spyClient, "objectMapper", objectMapper);
        MockRestServiceServer localMockServer = MockRestServiceServer.createServer(restTemplate);

        localMockServer.expect(requestTo(GRAPHQL_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andRespond(withSuccess(graphqlResponse, MediaType.APPLICATION_JSON));

        JsonNode result = spyClient.query(graphqlQuery);
        assertNotNull(result);
        assertEquals("bar", result.get("data").get("foo").asText());

        localMockServer.verify();
    }
}
