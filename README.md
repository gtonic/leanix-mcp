# LeanIX Java/Spring Boot Client

This project contains a Java/Spring Boot port of the JavaScript LeanIX GraphQL client. It provides a reactive, Spring Boot-compatible client for interacting with the LeanIX API.

## Features

- **Reactive Programming**: Uses Spring WebFlux and Project Reactor for non-blocking operations
- **OAuth2 Authentication**: Handles client credentials flow automatically
- **GraphQL Support**: Execute GraphQL queries with variables
- **Spring Boot Integration**: Configured as Spring beans with property-based configuration
- **Error Handling**: Comprehensive error handling and logging
- **Type Safety**: Strongly typed with proper validation

## Project Structure

```
src/
├── main/java/com/lgt/leanix_mcp/
│   ├── client/
│   │   └── LeanIXClient.java          # Main GraphQL client
│   ├── config/
│   │   └── LeanIXClientConfig.java    # Spring configuration
│   ├── service/
│   │   └── LeanIXService.java         # Example service using the client
│   └── LeanixMcpApplication.java      # Spring Boot main class
├── test/java/com/lgt/leanix_mcp/
│   └── client/
│       └── LeanIXClientTest.java      # Unit tests
└── resources/
    └── application.properties         # Configuration properties
```

## Configuration

Update `src/main/resources/application.properties` with your LeanIX credentials:

```properties
# LeanIX Configuration
leanix.subdomain=your-subdomain
leanix.api-token=your-api-token

# Optional: Enable debug logging
logging.level.com.lgt.leanix_mcp.client.LeanIXClient=DEBUG
```

## Usage

### Basic Usage

The `LeanIXClient` is automatically configured as a Spring bean. Inject it into your services:

```java
@Service
@RequiredArgsConstructor
public class MyService {
    
    private final LeanIXClient leanIXClient;
    
    public Mono<JsonNode> getWorkspaceInfo() {
        String query = """
            query GetWorkspaceInfo {
              viewer {
                id
                email
                account {
                  id
                  name
                }
              }
            }
            """;
        
        return leanIXClient.query(query);
    }
}
```

### Query with Variables

```java
public Mono<JsonNode> getFactSheetsByType(String factSheetType) {
    String query = """
        query GetFactSheetsByType($type: FactSheetType!) {
          allFactSheets(factSheetType: $type) {
            edges {
              node {
                id
                name
                displayName
                type
              }
            }
          }
        }
        """;
    
    Map<String, Object> variables = Map.of("type", factSheetType);
    return leanIXClient.query(query, variables);
}
```

### Using the Example Service

The project includes `LeanIXService` with example methods:

```java
@Autowired
private LeanIXService leanIXService;

// Get all applications
leanIXService.getFactSheetsByType("Application")
    .subscribe(result -> {
        // Process the result
        System.out.println(result.toPrettyString());
    });

// Search for fact sheets
leanIXService.searchFactSheetsByName("MyApp")
    .subscribe(result -> {
        // Process search results
    });

// Get workspace information
leanIXService.getWorkspaceInfo()
    .subscribe(result -> {
        // Process workspace info
    });
```

## API Reference

### LeanIXClient

#### Constructor
```java
public LeanIXClient(String subdomain, String apiToken)
```

#### Methods

- `Mono<String> getAccessToken()` - Gets an OAuth2 access token
- `Mono<JsonNode> query(String query)` - Executes a GraphQL query without variables
- `Mono<JsonNode> query(String query, Map<String, Object> variables)` - Executes a GraphQL query with variables

#### Getters
- `String getSubdomain()` - Returns the configured subdomain
- `String getBaseUrl()` - Returns the base LeanIX URL
- `String getGraphqlEndpoint()` - Returns the GraphQL endpoint URL
- `String getTokenEndpoint()` - Returns the OAuth2 token endpoint URL

## Comparison with JavaScript Version

| Feature | JavaScript | Java/Spring Boot |
|---------|------------|------------------|
| HTTP Client | fetch() | Spring WebFlux WebClient |
| Async Pattern | async/await | Reactive Streams (Mono) |
| GraphQL Client | graphql-request | Custom implementation |
| Configuration | Constructor params | Spring @ConfigurationProperties |
| Error Handling | try/catch | Reactive error operators |
| Logging | console.log | SLF4J/Logback |

## Dependencies

The client uses the following key dependencies:

- **Spring Boot WebFlux** - For reactive HTTP client
- **Jackson** - For JSON processing
- **Lombok** - For reducing boilerplate code
- **SLF4J** - For logging

## Building and Running

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

## Error Handling

The client includes comprehensive error handling:

- **Authentication errors** - Logged with details about token endpoint and status
- **GraphQL errors** - Logged with query details and response status
- **Network errors** - Handled by WebClient with retry capabilities
- **Validation errors** - Constructor validates required parameters

## Logging

Enable debug logging to see detailed request/response information:

```properties
logging.level.com.lgt.leanix_mcp.client.LeanIXClient=DEBUG
```

This will log:
- Token requests and responses
- GraphQL query execution
- Error details with full context

## Testing

The project includes unit tests for:
- Constructor validation
- URL generation
- Method signatures and return types

For integration testing with real LeanIX APIs, update the test configuration with valid credentials.

## Contributing

1. Follow Spring Boot conventions
2. Use reactive patterns (Mono/Flux)
3. Include comprehensive error handling
4. Add unit tests for new functionality
5. Update documentation for API changes
