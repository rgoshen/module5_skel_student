---
inclusion: always
---

# Technology Stack

## Core Framework & Dependencies

### Spring Boot Ecosystem
- **Spring Boot 2.2.4.RELEASE** - Main application framework with auto-configuration
- **Spring Web** - Web MVC and RESTful capabilities with embedded Tomcat
- **Spring Data REST** - RESTful web services with HATEOAS support
- **Spring Boot Starter Validation** - Bean validation with Hibernate Validator
- **Spring Boot Starter Security** - Security framework integration (for future enhancements)

**When to use**: Spring Boot is the primary framework for all web service functionality, dependency injection, and configuration management.

**Why chosen**: Provides comprehensive ecosystem, excellent documentation, and industry-standard practices for enterprise Java applications.

### Java Platform & Libraries
- **Java 8+** - Target Java version with modern language features
- **java.security.MessageDigest** - Cryptographic hash function library (JCE)
- **java.nio.charset.StandardCharsets** - Character encoding utilities
- **java.util.concurrent** - Thread-safe collections and utilities

**When to use**: Java 8 features (streams, lambdas, Optional) for clean, functional programming style. Use JCE for all cryptographic operations.

**Why chosen**: Java 8 provides good balance of modern features while maintaining educational environment compatibility.

## Build System & Tools

### Maven Ecosystem
- **Maven 3.6+** - Dependency management and build automation
- **Spring Boot Maven Plugin** - Application packaging and execution
- **Maven Wrapper** - Included scripts (mvnw/mvnw.cmd) for consistent builds
- **Maven Surefire Plugin** - Unit test execution
- **Maven Failsafe Plugin** - Integration test execution

**When to use**: Maven for all build operations, dependency management, and test execution.

**Configuration patterns**:
```xml
<!-- Prefer specific versions over version ranges -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.2.4.RELEASE</version>
</dependency>

<!-- Use test scope appropriately -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## Security & Cryptography Stack

### Cryptographic Libraries
- **Oracle Java Security Standard Algorithms** - Collision-resistant hash functions
- **Preferred Algorithms**: SHA-256, SHA-3-256, SHA-512, SHA-3-512
- **Deprecated Algorithms**: MD5, SHA-1 (explicitly rejected)
- **Algorithm Selection Criteria**: NIST compliance, collision resistance, performance

**When to use each algorithm**:
```java
// Primary choice - excellent balance of security and performance
MessageDigest.getInstance("SHA-256");

// Alternative - latest NIST standard, future-proof
MessageDigest.getInstance("SHA-3-256");

// High security scenarios - stronger but slower
MessageDigest.getInstance("SHA-512");

// Maximum security - latest and strongest
MessageDigest.getInstance("SHA-3-512");

// NEVER USE - security vulnerabilities
// MessageDigest.getInstance("MD5");    // Collision vulnerable
// MessageDigest.getInstance("SHA-1");  // Deprecated
```

### SSL/TLS Configuration
- **PKCS12 Keystore** - Industry-standard certificate storage format
- **SSL/TLS Protocol** - Encrypted communication on port 8443
- **Self-signed Certificates** - Acceptable for educational environments
- **Certificate Management** - Automated loading via Spring Boot configuration

**Configuration approach**:
```properties
# SSL Configuration (application.properties)
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-alias=tomcat
server.ssl.key-store-password=snhu4321
server.port=8443
```

## Testing Framework & Libraries

### Primary Testing Stack
- **JUnit 5 (Jupiter)** - Main testing framework with modern features
- **Mockito** - Mocking framework for unit tests
- **AssertJ** - Fluent assertion library for readable tests
- **Spring Boot Test** - Integration testing with Spring context
- **TestContainers** - Integration testing with external dependencies (future)

**When to use each**:
```java
// Unit tests - fast, isolated
@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock private ICryptographicProvider provider;
    @InjectMocks private HashServiceImpl service;
    
    @Test
    void shouldComputeHashSuccessfully() {
        // Use Mockito for mocking dependencies
        // Use AssertJ for fluent assertions
    }
}

// Integration tests - full Spring context
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HashControllerIntegrationTest {
    @Autowired private TestRestTemplate restTemplate;
    
    @Test
    void shouldReturnValidHashResponse() {
        // Test full HTTP request/response cycle
    }
}
```

### Testing Patterns & Strategies
- **Test Pyramid**: Many unit tests, fewer integration tests, minimal end-to-end tests
- **Naming Convention**: `shouldDoSomethingWhenCondition()` format
- **Test Organization**: Mirror main package structure in test packages
- **Security Testing**: Dedicated tests for input validation, error handling, algorithm security

## Design Patterns & Architectural Frameworks

### Implemented Design Patterns
- **Strategy Pattern** - Hash algorithm selection and implementation
- **Factory Pattern** - Algorithm instance creation and management
- **Template Method Pattern** - Common hash processing workflow
- **Dependency Injection** - Service layer composition and testing
- **Builder Pattern** - Complex object construction (responses, configurations)

**Pattern usage examples**:
```java
// Strategy Pattern for algorithm selection
public interface HashAlgorithmStrategy {
    String computeHash(String input) throws CryptographicException;
    String getAlgorithmName();
    boolean isSecure();
}

// Factory Pattern for strategy creation
@Component
public class HashAlgorithmFactory {
    public HashAlgorithmStrategy createStrategy(String algorithmName) {
        return switch (algorithmName.toUpperCase()) {
            case "SHA-256" -> new SHA256Strategy();
            case "SHA-3-256" -> new SHA3_256Strategy();
            default -> throw new IllegalArgumentException("Unsupported algorithm");
        };
    }
}

// Template Method for processing workflow
public abstract class AbstractHashProcessor {
    public final ProcessedHashResult processHash(String input) {
        String sanitized = validateAndSanitize(input);
        byte[] hashBytes = computeHashBytes(sanitized);
        String hexHash = bytesToHex(hashBytes);
        return createResult(sanitized, hexHash, getAlgorithmName());
    }
    
    protected abstract byte[] computeHashBytes(String input);
    protected abstract String getAlgorithmName();
}
```

### Architectural Principles
- **SOLID Principles** - All classes follow single responsibility, open/closed, etc.
- **Clean Architecture** - Clear separation between presentation, business, and infrastructure
- **Dependency Inversion** - High-level modules depend on abstractions
- **Domain-Driven Design** - Clear domain boundaries and ubiquitous language

## Development & Deployment Tools

### Local Development
- **Eclipse IDE** - Recommended development environment (educational standard)
- **IntelliJ IDEA** - Alternative professional IDE (optional)
- **Visual Studio Code** - Lightweight option with Java extensions
- **Git** - Version control with conventional commit messages

### Build & Runtime Commands
```bash
# Development workflow
./mvnw clean compile              # Clean and compile source
./mvnw test                      # Run unit tests only
./mvnw verify                    # Run all tests including integration
./mvnw spring-boot:run           # Start development server
./mvnw package                   # Create deployable JAR
java -jar target/ssl-server-*.jar # Run packaged application

# Quality assurance
./mvnw spotbugs:check            # Static analysis (if configured)
./mvnw checkstyle:check          # Code style validation (if configured)
./mvnw jacoco:report             # Test coverage report (if configured)

# Troubleshooting
./mvnw dependency:tree           # Analyze dependency conflicts
./mvnw dependency:purge-local-repository # Clear local Maven cache
./mvnw clean install -U         # Force update of all dependencies
```

## Logging & Monitoring

### Logging Framework
- **SLF4J** - Logging facade for consistent API
- **Logback** - Default logging implementation (Spring Boot)
- **Structured Logging** - JSON format for production environments
- **Security-Aware Logging** - No sensitive data in logs

**Logging patterns**:
```java
@Slf4j
public class HashService {
    public String computeHash(String input, String algorithm) {
        // Log operation start (no sensitive data)
        log.info("Computing hash with algorithm: {}", algorithm);
        
        try {
            String result = performHashOperation(input, algorithm);
            
            // Log success with context
            log.info("Hash computation successful, algorithm: {}, input length: {}", 
                algorithm, input.length());
            
            return result;
            
        } catch (Exception e) {
            // Log error with correlation ID for debugging
            String errorId = UUID.randomUUID().toString();
            log.error("Hash computation failed [{}]: {}", errorId, e.getMessage(), e);
            throw new CryptographicException("Hash computation failed: " + errorId, e);
        }
    }
}
```

### Application Monitoring
- **Spring Boot Actuator** - Production-ready features (health checks, metrics)
- **Micrometer** - Application metrics collection
- **Custom Health Indicators** - Algorithm availability, keystore status

## Configuration Management

### Property Management
- **application.properties** - Main configuration file
- **Profile-based Configuration** - Environment-specific settings
- **Type-safe Configuration** - `@ConfigurationProperties` classes
- **Externalized Configuration** - No hardcoded values in source code

**Configuration patterns**:
```java
@ConfigurationProperties(prefix = "app.hash")
@Component
@Validated
public class HashConfiguration {
    
    @NotBlank
    private String defaultAlgorithm = "SHA-256";
    
    @Min(1) @Max(10000)
    private int maxInputLength = 1000;
    
    @NotEmpty
    private Set<String> allowedAlgorithms = Set.of("SHA-256", "SHA-3-256", "SHA-512");
    
    // Getters and setters
}
```

## Performance & Optimization Libraries

### Optimization Utilities
- **Apache Commons Lang** - String utilities and validation helpers
- **Caffeine Cache** - High-performance caching library (for algorithm instances)
- **Concurrent Collections** - Thread-safe data structures
- **Memory-Efficient Operations** - Primitive collections where appropriate

**Performance patterns**:
```java
// Efficient hex conversion with lookup table
private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}

// Thread-safe algorithm cache
private static final Map<String, MessageDigest> ALGORITHM_CACHE = 
    new ConcurrentHashMap<>();
```

## Integration & API Standards

### HTTP & REST Standards
- **RESTful Design** - Resource-based URLs, appropriate HTTP methods
- **Content Negotiation** - Support for different response formats
- **HTTP Status Codes** - Semantic status code usage
- **Error Response Format** - Consistent error structure across endpoints

**API design patterns**:
```java
@RestController
@RequestMapping("/api/v1/hash")
public class HashController {
    
    @GetMapping(produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> generateHash(
            @RequestHeader(value = "Accept", defaultValue = "text/html") String acceptHeader) {
        
        if (acceptHeader.contains("application/json")) {
            return ResponseEntity.ok(createJsonResponse());
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(createHtmlResponse());
    }
}
```

### Security Integration Standards
- **Input Validation** - Bean Validation (JSR-303) annotations
- **HTTPS Enforcement** - All endpoints require SSL/TLS
- **Error Handling** - No sensitive information in responses
- **Audit Logging** - Security events logged appropriately

## Future Technology Considerations

### Scalability Enhancements
- **Spring Cloud** - Microservices architecture for scaling
- **Redis** - Distributed caching for hash results
- **PostgreSQL** - Persistent storage for audit trails
- **Docker** - Containerization for deployment consistency

### Security Enhancements
- **Spring Security** - Comprehensive security framework
- **OAuth 2.0 / JWT** - Token-based authentication
- **Rate Limiting** - Request throttling and abuse prevention
- **Certificate Management** - Automated certificate renewal

### Monitoring & Observability
- **Prometheus** - Metrics collection and alerting
- **Grafana** - Metrics visualization and dashboards
- **ELK Stack** - Centralized logging and analysis
- **Jaeger** - Distributed tracing for performance analysis

This technology stack provides a solid foundation for both educational objectives and potential production deployment, following industry best practices while maintaining focus on security and maintainability.