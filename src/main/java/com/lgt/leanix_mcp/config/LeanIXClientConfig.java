package com.lgt.leanix_mcp.config;

import com.lgt.leanix_mcp.client.LeanIXClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "leanix")
public class LeanIXClientConfig {

    private String subdomain;
    private String apiToken;

    @Bean
    public LeanIXClient leanIXClient() {
        return new LeanIXClient(subdomain, apiToken);
    }

    // Getters and setters for configuration properties
    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
