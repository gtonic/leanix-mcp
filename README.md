# LeanIX Java/Spring Boot Client

This project contains a Java/Spring Boot port of the JavaScript LeanIX GraphQL client. It provides a reactive, Spring Boot-compatible client for interacting with the LeanIX API.

## Features

The following methods in `LeanIXService.java` are exposed as tools for use with an AI agent:

- `getFactSheetsByType(String factSheetType)`: Get all fact sheets of a given type.
- `searchFactSheetsByName(String searchTerm)`: Search for fact sheets by name.
- `getWorkspaceInfo()`: Get information about the workspace.
- `getFactSheetsByTypePaginated(String factSheetType, Integer first, String after)`: Get paginated fact sheets of a given type.
- `getTypes()`: Get all available fact sheet types and their keys.
- `getApplications()`: Get all applications with default pagination.
- `getITComponents()`: Get all IT components with default pagination.
- `getBusinessCapabilities()`: Get all business capabilities with default pagination.
- `getProviders()`: Get all providers with default pagination.
- `getOrganizations()`: Get all organizations with default pagination.
- `getBusinessContexts()`: Get all business contexts with default pagination.
- `getInterfaces()`: Get all interfaces with default pagination.
- `getDataObjects()`: Get all data objects with default pagination.


## TODOs/know issues

- explicitely model parent-child relations (to get hold of the subfactsheets)
- support mutation (update, delete)


## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Docker (for containerization)
- [Task](https://taskfile.dev/installation/) (optional, for simplified commands)

### Configuration

Update `src/main/resources/application.properties` with your LeanIX credentials:

```properties
# LeanIX Configuration
leanix.subdomain=your-subdomain
leanix.api-token=your-api-token
leanix.pagination-default-size=50
```

- `leanix.subdomain`: The subdomain of your LeanIX workspace (e.g., `my-company`).
- `leanix.api-token`: Your LeanIX API token.
- `leanix.pagination-default-size`: The default number of items to return for paginated queries.

### Using Taskfile

The `Taskfile.yml` provides simplified commands for common operations:

- **Clean and build the project:**
  ```bash
  task clean-build
  ```

- **Build the Docker image:**
  ```bash
  task docker-build
  ```

- **Run the Docker container:**
  ```bash
  task docker-run
  ```

- **Publish the Docker image:**
  ```bash
  export DOCKER_REGISTRY=your-registry
  export IMAGE_NAME=your-image-name
  task docker-publish
  ```

### Manual Build and Run

If you don't have Task installed, you can use Maven directly:

- **Clean and build the project:**
  ```bash
  mvn clean package
  ```

- **Run the application:**
  ```bash
  mvn spring-boot:run
  ```
