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

  public enum FactSheetType {
    BUSINESS_CAPABILITY("BusinessCapability"),
    PROCESS("businesscontext"),
    USER_GROUP("usergroup"),
    APPLICATION("application"),
    INTERFACE("interface"),
    DATA_OBJECT("dataobject"),
    IT_COMPONENT("itcomponent"),
    PROVIDER("provider"),
    TECHNICAL_STACK("TechCategory"),
    PERSONA("persona");

    private final String searchName;

    FactSheetType(String searchName) {
      this.searchName = searchName;
    }

    public String getSearchName() {
      return searchName;
    }
  }

  private final LeanIXClient leanIXClient;
  private final com.lgt.leanix_mcp.config.LeanIXClientConfig leanIXClientConfig;
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

    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("GraphQL Variables: {}", variables);
    log.info("Fetching fact sheets of type: {}", factSheetType);
    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("GraphQL Variables: {}", variables);
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

    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("GraphQL Variables: {}", variables);
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

  // Explicit MCP tools for each supported factsheet type

  /**
   * Get paginated fact sheets of a specific type.
   *
   * @param factSheetType The type of fact sheet (e.g., "Application", "Persona",
   *                      etc.)
   * @param first         Number of items to return (page size)
   * @param after         Cursor for pagination (null for first page)
   * @return JsonNode containing pageInfo and edges
   */
  @Tool(name = "getFactSheetsByTypePaginated", description = "Get paginated factsheets of a given type. Params: factSheetType (string), first (int), after (string, optional). Returns pageInfo and edges.")
  public JsonNode getFactSheetsByTypePaginated(String factSheetType, Integer first, String after) {
    if (factSheetType == null || factSheetType.trim().isEmpty()) {
      throw new IllegalArgumentException("factSheetType parameter is required");
    }
    // Default pagination size to config value if not provided
    int pageSize = (first != null) ? first : leanIXClientConfig.getPaginationDefaultSize();
    String query = """
        query GetFactSheetsByTypePaginated($type: FactSheetType!, $first: Int, $after: String) {
          allFactSheets(factSheetType: $type, first: $first, after: $after) {
            pageInfo {
              hasNextPage
              endCursor
            }
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
              }
            }
          }
        }
        """;
    Map<String, Object> variables = new java.util.HashMap<>();
    variables.put("type", factSheetType);
    variables.put("first", pageSize);
    if (after != null)
      variables.put("after", after);

    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("GraphQL Variables: {}", variables);
    log.info("Fetching paginated fact sheets of type: {}, first: {}, after: {}", factSheetType, pageSize, after);
    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("GraphQL Variables: {}", variables);
    JsonNode result = leanIXClient.query(query, variables);
    return result.path("data").path("allFactSheets");
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

    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("Fetching workspace information (fact sheet counts and overview)");
    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    JsonNode result = leanIXClient.query(query);
    return result;
  }

  /**
   * Get all available fact sheet types and their keys from the workspace.
   * 
   * @return JsonNode containing the types and keys
   */
  @Tool(name = "getTypes", description = "Get all available fact sheet types and their keys from the workspace")
  public JsonNode getTypes() {
    try {
      log.info("Fetching all available fact sheet types and keys");
      JsonNode result = getTypesInternal();
      log.info("Successfully fetched types: {}", result.toString());
      return result.path("data").path("allFactSheets").path("filterOptions").path("facets");
    } catch (Exception e) {
      log.error("Error fetching types", e);
      throw new RuntimeException("Error fetching types: " + e.getMessage(), e);
    }
  }

  /**
   * Internal method to get all available fact sheet types and their keys.
   * 
   * @return JsonNode containing the query result
   */
  public JsonNode getTypesInternal() {
    String query = """
        query {
          allFactSheets {
            filterOptions {
              facets {
                facetKey
                results {
                  name
                  key
                }
              }
            }
          }
        }
        """;

    log.info("GraphQL Query: {}", query.replaceAll("\\s+", " "));
    log.info("Fetching all available fact sheet types and keys");
    JsonNode result = leanIXClient.query(query);
    return result;
  }

  /**
   * Get all applications with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getApplications", description = "Get all applications with default pagination")
  public java.util.List<FactSheet> getApplications() {
    return getFactSheetsWithDefaultPaging("Application");
  }

  /**
   * Get all IT components with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getITComponents", description = "Get all IT Components with default pagination")
  public java.util.List<FactSheet> getITComponents() {
    return getFactSheetsWithDefaultPaging("ITComponent");
  }

  /**
   * Get all business capabilities with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getBusinessCapabilities", description = "Get all Business Capabilities with default pagination")
  public java.util.List<FactSheet> getBusinessCapabilities() {
    return getFactSheetsWithDefaultPaging("BusinessCapability");
  }

  /**
   * Get all providers with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getProviders", description = "Get all Providers with default pagination")
  public java.util.List<FactSheet> getProviders() {
    return getFactSheetsWithDefaultPaging("Provider");
  }

  /**
   * Get all organizations with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getOrganizations", description = "Get all Organizations with default pagination")
  public java.util.List<FactSheet> getOrganizations() {
    return getFactSheetsWithDefaultPaging("UserGroup");
  }

  /**
   * Get all business contexts with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getBusinessContexts", description = "Get all Business Contexts with default pagination")
  public java.util.List<FactSheet> getBusinessContexts() {
    return getFactSheetsWithDefaultPaging("Process");
  }

  /**
   * Get all interfaces with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getInterfaces", description = "Get all Interfaces with default pagination")
  public java.util.List<FactSheet> getInterfaces() {
    return getFactSheetsWithDefaultPaging("Interface");
  }

  /**
   * Get all data objects with default pagination.
   *
   * @return List of FactSheet DTOs
   */
  @Tool(name = "getDataObjects", description = "Get all Data Objects with default pagination")
  public java.util.List<FactSheet> getDataObjects() {
    return getFactSheetsWithDefaultPaging("DataObject");
  }

  private java.util.List<FactSheet> getFactSheetsWithDefaultPaging(String factSheetType) {
    JsonNode result = getFactSheetsByTypePaginated(factSheetType, null, null);
    try {
      JsonNode edges = result.path("edges");
      if (edges.isMissingNode() || !edges.isArray()) {
        return Collections.emptyList();
      }
      java.util.List<FactSheet> factSheets = new ArrayList<>();
      for (JsonNode edge : edges) {
        JsonNode node = edge.path("node");
        FactSheet fs = objectMapper.treeToValue(node, FactSheet.class);
        factSheets.add(fs);
      }
      log.info("Successfully fetched {} fact sheets of type {}", factSheets.size(), factSheetType);
      return factSheets;
    } catch (Exception e) {
      log.error("Error mapping fact sheets of type {}", factSheetType, e);
      throw new RuntimeException("Error mapping " + factSheetType + ": " + e.getMessage(), e);
    }
  }
}
