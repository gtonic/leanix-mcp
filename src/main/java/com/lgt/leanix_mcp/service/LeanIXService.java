package com.lgt.leanix_mcp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lgt.leanix_mcp.client.LeanIXClient;
import com.lgt.leanix_mcp.model.FactSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeanIXService {

  private final LeanIXClient leanIXClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Get all fact sheets of a specific type, safely mapped to DTOs
   *
   * @param factSheetType The type of fact sheet (e.g., "Application",
   *                      "DataObject", etc.)
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getFactSheetsByType", description = "Get all factsheets of a given type (string), returns List<FactSheet>")
  public java.util.List<FactSheet> getFactSheetsByType(String factSheetType) {
    if (factSheetType == null || factSheetType.trim().isEmpty()) {
      throw new IllegalArgumentException("factSheetType parameter is required");
    }
    log.info("Fetching fact sheets of type: {}", factSheetType);
    JsonNode result = getFactSheetsByTypeInternal(factSheetType);
    try {
      JsonNode edges = result.path("data").path("allFactSheets").path("edges");
      if (edges.isMissingNode() || !edges.isArray()) {
        return Collections.emptyList();
      }
      java.util.List<FactSheet> factSheets = new ArrayList<>();
      for (JsonNode edge : edges) {
        JsonNode node = edge.path("node");
        FactSheet fs = objectMapper.treeToValue(node, FactSheet.class);
        factSheets.add(fs);
      }
      log.info("Successfully fetched {} fact sheets of type: {}", factSheets.size(), factSheetType);
      return factSheets;
    } catch (Exception e) {
      log.error("Error mapping fact sheets by type", e);
      throw new RuntimeException("Error mapping fact sheets: " + e.getMessage(), e);
    }
  }

  /**
   * Internal method to get all fact sheets of a specific type
   * 
   * @param factSheetType The type of fact sheet (e.g., "Application",
   *                      "DataObject", etc.)
   * @return JsonNode containing the query result
   */
  public JsonNode getFactSheetsByTypeInternal(String factSheetType) {
    String query = """
        query GetFactSheetsByType($type: FactSheetType!) {
          allFactSheets(factSheetType: $type) {
            edges {
              node {
                id
                name
                displayName
                description
                type
                ... on Application {
                  lifecycle {
                    phase
                  }
                }
              }
            }
          }
        }
        """;

    Map<String, Object> variables = Map.of("type", factSheetType);

    log.info("Fetching fact sheets of type: {}", factSheetType);
    JsonNode result = leanIXClient.query(query, variables);
    return result;
  }

  /**
   * Search for fact sheets by name, safely mapped to DTOs
   *
   * @param searchTerm The search term to look for in fact sheet names
   * @return List of FactSheet DTOs
   */
  @Tool(name = "searchFactSheetsByName", description = "Search for factsheets by name (string), returns List<FactSheet>")
  public java.util.List<FactSheet> searchFactSheetsByName(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      throw new IllegalArgumentException("searchTerm parameter is required");
    }
    log.info("Searching for fact sheets with term: {}", searchTerm);
    JsonNode result = searchFactSheetsByNameInternal(searchTerm);
    try {
      JsonNode edges = result.path("data").path("allFactSheets").path("edges");
      if (edges.isMissingNode() || !edges.isArray()) {
        return Collections.emptyList();
      }
      java.util.List<FactSheet> factSheets = new ArrayList<>();
      for (JsonNode edge : edges) {
        JsonNode node = edge.path("node");
        FactSheet fs = objectMapper.treeToValue(node, FactSheet.class);
        factSheets.add(fs);
      }
      log.info("Successfully found {} fact sheets for search term: {}", factSheets.size(), searchTerm);
      return factSheets;
    } catch (Exception e) {
      log.error("Error mapping fact sheets by name", e);
      throw new RuntimeException("Error mapping fact sheets: " + e.getMessage(), e);
    }
  }

  /**
   * Internal method to search for fact sheets by name
   * 
   * @param searchTerm The search term to look for in fact sheet names
   * @return JsonNode containing the query result
   */
  public JsonNode searchFactSheetsByNameInternal(String searchTerm) {
    String query = """
        query searchFactSheetByName($name: String!) {
          allFactSheets(filter: {
            fullTextSearch: $name
          }) {
            edges {
              node {
                id
                name
                displayName
                fullName
                type
                description
                status
                lxState
                completion {
                  completion
                  percentage
                }
                updatedAt
                createdAt
                tags {
                  name
                }
                ... on Application {
                  lifecycle {
                    asString
                  }
                  businessCriticality
                  technicalSuitability
                  functionalSuitability
                }
              }
            }
          }
        }
        """;

    Map<String, Object> variables = Map.of("name", searchTerm);

    log.info("Searching for fact sheets with term: {}", searchTerm);
    JsonNode result = leanIXClient.query(query, variables);
    return result;
  }

  /**
   * Get workspace information
   * 
   * @return String containing the workspace information
   */
  @Tool(name = "getWorkspaceInfo", description = "Get information regarding the workspace")
  public String getWorkspaceInfo() {
    try {
      log.info("Fetching workspace information");
      JsonNode result = getWorkspaceInfoInternal();
      log.info("Successfully fetched workspace information");
      return "Workspace information: " + result.toString();
    } catch (Exception e) {
      log.error("Error fetching workspace information", e);
      return "Error fetching workspace information: " + e.getMessage();
    }
  }

  /**
   * Internal method to get workspace information
   * 
   * @return JsonNode containing the workspace information
   */
  public JsonNode getWorkspaceInfoInternal() {
    String query = """
        query {
          allFactSheets {
            totalCount
            filterOptions {
              facets {
                facetKey
                results {
                  name
                  key
                  count
                }
              }
            }
          }
        }
        """;

    log.info("Fetching workspace information (fact sheet counts and overview)");
    JsonNode result = leanIXClient.query(query);
    return result;
  }
}
