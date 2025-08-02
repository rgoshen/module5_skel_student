---
inclusion: always
---

# Project Structure

## Architectural Philosophy

This project follows **Clean Architecture** principles with clear separation of concerns, implementing **SOLID principles** throughout. The structure evolves from the educational skeleton to a professional, maintainable codebase suitable for enterprise environments.

## Enhanced Project Structure

```
ssl-server/
├── pom.xml                                 # Maven configuration & dependencies
├── mvnw, mvnw.cmd                         # Maven wrapper scripts
├── README.md                              # Comprehensive project documentation
├── .kiro/
│   └── steering/                          # Kiro project guidance
│       ├── product.md                     # Business context and goals
│       ├── structure.md                   # Architecture and organization
│       └── tech.md                        # Technology stack guidance
├── src/
│   ├── main/
│   │   ├── java/com/snhu/sslserver/
│   │   │   ├── ServerApplication.java      # Main application entry point
│   │   │   ├── controller/                # Presentation layer
│   │   │   │   └── HashController.java    # REST endpoint handling
│   │   │   ├── service/                   # Business logic layer
│   │   │   │   ├── IHashService.java      # Hash service interface
│   │   │   │   ├── HashServiceImpl.java   # Hash service implementation
│   │   │   │   ├── IInputValidator.java   # Validation interface
│   │   │   │   └── SecurityInputValidator.java # Security validation impl
│   │   │   ├── provider/                  # Infrastructure layer
│   │   │   │   ├── ICryptographicProvider.java # Crypto interface
│   │   │   │   └── CryptographicProvider.java  # Crypto implementation
│   │   │   ├── strategy/                  # Algorithm strategies
│   │   │   │   ├── HashAlgorithmStrategy.java  # Strategy interface
│   │   │   │   ├── SHA256Strategy.java    # SHA-256 implementation
│   │   │   │   └── SHA3Strategy.java      # SHA-3 implementation
│   │   │   ├── factory/                   # Factory patterns
│   │   │   │   └── HashAlgorithmFactory.java   # Algorithm factory
│   │   │   ├── util/                      # Utility classes
│   │   │   │   ├── CryptographicUtils.java     # Crypto utilities
│   │   │   │   └── ResponseFormatter.java      # Response formatting
│   │   │   ├── exception/                 # Custom exceptions
│   │   │   │   ├── CryptographicException.java # Crypto errors
│   │   │   │   └── SecurityException.java      # Security violations
│   │   │   └── config/                    # Configuration classes
│   │   │       └── SecurityConfig.java   # Security configuration
│   │   └── resources/
│   │       ├── application.properties      # SSL & server configuration
│   │       ├── keystore.p12               # PKCS12 SSL certificate
│   │       ├── static/                    # Static web assets
│   │       └── templates/                 # View templates
│   └── test/
│       └── java/com/snhu/sslserver/
│           ├── controller/                # Controller tests
│           │   └── HashControllerTest.java
│           ├── service/                   # Service layer tests
│           │   ├── HashServiceTest.java
│           │   └── InputValidatorTest.java
│           ├── provider/                  # Infrastructure tests
│           │   └── CryptographicProviderTest.java
│           ├── integration/               # Integration tests
│           │   └── HashEndpointIntegrationTest.java
│           └── ServerApplicationTests.java # Application tests
```

## Package Organization Principles

### Layer-Based Organization
Packages are organized by architectural layer, not by feature, to maintain clear separation of concerns:

- **`controller/`** - Presentation layer (HTTP concerns only)
- **`service/`** - Business logic layer (domain operations)
- **`provider/`** - Infrastructure layer (technical concerns)
- **`strategy/`** - Strategy pattern implementations
- **`factory/`** - Factory pattern implementations
- **`util/`** - Cross-cutting utility functions
- **`exception/`** - Custom exception hierarchy
- **`config/`** - Configuration and setup classes

### Naming Conventions

#### Interface Naming
```java
// Interfaces use 'I' prefix for clarity
public interface IHashService { }
public interface IInputValidator { }
public interface ICryptographicProvider { }

// Strategy interfaces use descriptive names
public interface HashAlgorithmStrategy { }
```

#### Implementation Naming
```java
// Implementations use descriptive, purpose-driven names
public class HashServiceImpl implements IHashService { }
public class SecurityInputValidator implements IInputValidator { }
public class CryptographicProvider implements ICryptographicProvider { }

// Strategy implementations include algorithm name
public class SHA256Strategy implements HashAlgorithmStrategy { }
public class SHA3_256Strategy implements HashAlgorithmStrategy { }
```

#### Class and Method Naming
```java
// Classes use PascalCase with descriptive names
public class CryptographicUtils { }
public class HashAlgorithmFactory { }
public class SecurityException extends RuntimeException { }

// Methods use camelCase with verb-noun pattern
public String computeHash(String input) { }
public ValidationResult validateInput(String data) { }
public MessageDigest createDigest(String algorithm) { }

// Constants use UPPER_SNAKE_CASE
private static final String DEFAULT_ALGORITHM = "SHA-256";
private static final int MAX_INPUT_LENGTH = 1000;
```

## Import Organization Standards

### Import Ordering
1. **Java standard library** imports
2. **Third-party library** imports (Spring, etc.)
3. **Static imports** (if any)
4. **Project imports** (same package last)

```java
// Example proper import organization
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.snhu.sslserver.service.IHashService;
import com.snhu.sslserver.exception.CryptographicException;
```

### Import Preferences
- **Always use specific imports** - avoid wildcard imports (`import java.util.*`)
- **Group related imports** - keep same-package imports together
- **Use static imports sparingly** - only for frequently used constants or utilities

## File Organization Patterns

### Service Layer Pattern
```java
// Interface defines contract (in service package)
public interface IHashService {
    String computeHash(String input, String algorithm) throws CryptographicException;
    AlgorithmInfo getAlgorithmInfo(String algorithm);
}

// Implementation provides behavior (in service package)
@Service
@Slf4j
public class HashServiceImpl implements IHashService {
    private final ICryptographicProvider cryptoProvider;
    
    public HashServiceImpl(ICryptographicProvider cryptoProvider) {
        this.cryptoProvider = Objects.requireNonNull(cryptoProvider);
    }
    
    @Override
    public String computeHash(String input, String algorithm) {
        // Implementation
    }
}
```

### Controller Layer Pattern
```java
// Controllers focus solely on HTTP concerns
@RestController
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class HashController {
    
    private final IHashService hashService;
    private final IInputValidator validator;
    
    // Constructor injection for dependencies
    public HashController(IHashService hashService, IInputValidator validator) {
        this.hashService = Objects.requireNonNull(hashService);
        this.validator = Objects.requireNonNull(validator);
    }
    
    @GetMapping(value = "/hash", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> generateHash() {
        // HTTP-specific logic only
    }
}
```

### Strategy Pattern Organization
```java
// Abstract strategy interface
public interface HashAlgorithmStrategy {
    String computeHash(String input) throws CryptographicException;
    String getAlgorithmName();
    boolean isSecure();
}

// Concrete strategy implementations
@Component
public class SHA256Strategy implements HashAlgorithmStrategy {
    @Override
    public String computeHash(String input) {
        // SHA-256 specific implementation
    }
}

@Component  
public class SHA3_256Strategy implements HashAlgorithmStrategy {
    @Override
    public String computeHash(String input) {
        // SHA-3-256 specific implementation
    }
}
```

## Architectural Decision Records

### Why Layer-Based Over Feature-Based?
For this educational project, layer-based organization better demonstrates architectural principles and separation of concerns. Feature-based organization would be preferred in larger, multi-domain applications.

### Why Interface Segregation?
Small, focused interfaces make the code more testable, maintainable, and follow the Interface Segregation Principle. Each interface serves a specific client need.

### Why Constructor Injection?
Constructor injection ensures required dependencies are available at object creation time, makes dependencies explicit, and supports immutable field declarations.

### Why Strategy Pattern for Algorithms?
The Strategy pattern allows easy addition of new hash algorithms without modifying existing code, following the Open/Closed Principle and making the system extensible.

## Testing Structure Alignment

### Test Package Mirroring
Test packages mirror the main source structure for easy navigation:
```
src/test/java/com/snhu/sslserver/
├── controller/     # Tests for presentation layer
├── service/        # Tests for business logic
├── provider/       # Tests for infrastructure
├── strategy/       # Tests for algorithm strategies
├── integration/    # End-to-end integration tests
└── util/          # Tests for utility classes
```

### Test Naming Conventions
```java
// Test classes end with 'Test'
public class HashServiceTest { }
public class SecurityInputValidatorTest { }

// Test methods use descriptive names with 'should' prefix
@Test
@DisplayName("Should compute hash successfully with valid input")
void shouldComputeHashSuccessfully() { }

@Test
@DisplayName("Should throw SecurityException for insecure algorithm")
void shouldThrowSecurityExceptionForInsecureAlgorithm() { }
```

## Configuration and Resources Organization

### Properties File Structure
```properties
# Server Configuration
server.port=8443

# SSL Configuration  
server.ssl.key-alias=tomcat
server.ssl.key-store-password=snhu4321
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-type=PKCS12

# Application Properties
app.hash.default-algorithm=SHA-256
app.hash.max-input-length=1000

# Logging Configuration
logging.level.com.snhu.sslserver=INFO
logging.level.org.springframework.security=DEBUG
```

### Resource Organization
- **`keystore.p12`** - SSL certificate in PKCS12 format
- **`application.properties`** - Main configuration
- **`application-dev.properties`** - Development-specific overrides
- **`application-test.properties`** - Test environment configuration

This structure supports the evolution from educational skeleton to professional application while maintaining clear architectural boundaries and following established Java enterprise patterns.