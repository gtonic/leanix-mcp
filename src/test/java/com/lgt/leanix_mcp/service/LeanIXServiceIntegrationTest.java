package com.lgt.leanix_mcp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.lgt.leanix_mcp.client.LeanIXClient;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@ActiveProfiles("test")
class LeanIXServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(LeanIXServiceIntegrationTest.class);

    @Autowired
    private LeanIXService leanIXService;

    @Autowired
    private LeanIXClient leanIXClient;

    @BeforeEach
    void authenticatePrecondition() {
        String token = leanIXClient.getAccessToken();
        assertThat(token)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void searchFactSheetsByName_returnsResults() {
        String searchTerm = "Azure";
        var result = leanIXService.searchFactSheetsByName(searchTerm);
        log.debug("searchFactSheetsByName results: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getWorkspaceInfo_returnsWorkspaceInformation() {
        String result = leanIXService.getWorkspaceInfo();
        log.debug("WorkspaceInfo: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains("Workspace information:")
                .contains("totalCount");
    }

    @Test
    void getDataObjects_returnsList() {
        var result = leanIXService.getDataObjects();
        log.debug("DataObjects: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getInterfaces_returnsList() {
        var result = leanIXService.getInterfaces();
        log.debug("Interfaces: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getBusinessContexts_returnsList() {
        var result = leanIXService.getBusinessContexts();
        log.debug("BusinessContexts: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getOrganizations_returnsList() {
        var result = leanIXService.getOrganizations();
        log.debug("Organizations: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getProviders_returnsList() {
        var result = leanIXService.getProviders();
        log.debug("Providers: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getBusinessCapabilities_returnsList() {
        var result = leanIXService.getBusinessCapabilities();
        log.debug("BusinessCapabilities: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getITComponents_returnsList() {
        var result = leanIXService.getITComponents();
        log.debug("ITComponents: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getApplications_returnsList() {
        var result = leanIXService.getApplications();
        log.debug("Applications: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void getTypes_returnsJsonNode() {
        var result = leanIXService.getTypes();
        log.debug("Types: {}", result);
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }
}
