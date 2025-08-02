# DEVELOPMENT.md

## Development Environment Setup

### Prerequisites

#### Required Software
- **Java 8 or higher** - Target runtime environment
- **Maven 3.6+** - Build and dependency management
- **Git** - Version control
- **IDE** - Eclipse (recommended), IntelliJ IDEA, or VS Code

#### Recommended Tools
- **Postman** - API testing and development
- **OpenSSL** - Certificate management and testing
- **Curl** - Command-line HTTP testing
- **Browser Developer Tools** - Frontend debugging

### Initial Project Setup

#### 1. Clone and Import Project
```bash
# Clone the repository
git clone <repository-url>
cd ssl-server

# Verify project structure
ls -la src/main/java/com/snhu/sslserver/
ls -la src/main/resources/

# Import into Eclipse
# File → Import → Existing Maven Projects
# Browse to project directory and import
```

#### 2. Verify Maven Configuration
```bash
# Check Maven installation
mvn --version

# Verify project dependencies
./mvnw dependency:tree

# Clean and compile
./mvnw clean compile
```

#### 3. IDE Configuration

**Eclipse Setup:**
```bash
# Import Google Java Format settings
# Window → Preferences → Java → Code Style → Formatter
# Import: eclipse-java-google-style.xml (if available)

# Configure compiler compliance
# Project Properties → Java Build Path → Libraries
# Verify JRE version is 1.8 or higher
```

**IntelliJ IDEA Setup:**
```bash
# Install Google Java Format plugin
# File → Settings → Plugins → Browse repositories → "google-java-format"

# Configure code style
# File → Settings → Editor → Code Style → Java
# Scheme: GoogleStyle
```

## Development Workflow

### 1. Feature Development Process

#### Branch Strategy
```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes with atomic commits
git add .
git commit -m "feat: add secure hash validation"

# Push branch
git push origin feature/your-feature-name

# Create pull request via GitHub
```

#### Code Development Cycle
```bash
# 1. Write failing test
./mvnw test -Dtest=NewFeatureTest

# 2. Implement feature
# Edit source files in src/main/java

# 3. Run tests to verify
./mvnw test

# 4. Check code formatting
./mvnw spotless:check

# 5. Auto-format if needed
./mvnw spotless:apply

# 6. Commit changes
git add .
git commit -m "feat: implement secure algorithm validation"
```

### 2. Testing Workflow

#### Unit Testing
```bash
# Run all unit tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=HashServiceTest

# Run specific test method
./mvnw test -Dtest=HashServiceTest#shouldComputeHashSuccessfully

# Run tests with coverage report
./mvnw test jacoco:report
# View coverage: target/site/jacoco/index.html
```

#### Integration Testing
```bash
# Run integration tests only
./mvnw test -Dtest="*IntegrationTest"

# Run all tests (unit + integration)
./mvnw verify

# Test with different profiles
./mvnw test -Dspring.profiles.active=test
```

#### Manual Testing
```bash
# Start application
./mvnw spring-boot:run

# Test endpoints with curl
curl -k https://localhost:8443/api/v1/hash
curl -k https://localhost:8443/api/v1/hash?algorithm=SHA-256
curl -k -H "Accept: application/json" https://localhost:8443/api/v1/hash

# Test SSL certificate
openssl s_client -connect localhost:8443 -servername localhost
```

### 3. Code Quality Workflow

#### Pre-commit Checks
```bash
# Setup pre-commit hook (first time only)
chmod +x setup-pre-commit.sh
./setup-pre-commit.sh

# Manual pre-commit validation
./mvnw spotless:check
./mvnw test
```

#### Code Review Checklist
- [ ] All tests pass
- [ ] Code follows formatting standards
- [ ] Security considerations addressed
- [ ] Documentation updated
- [ ] No hardcoded secrets or credentials
- [ ] Error handling implemented properly
- [ ] Input validation in place

#### Static Analysis
```bash
# Run SpotBugs (if configured)
./mvnw spotbugs:check

# Run Checkstyle (if configured)
./mvnw checkstyle:check

# Dependency vulnerability check
./mvnw org.owasp:dependency-check-maven:check
```

## Build and Deployment

### Local Development Builds

#### Development Mode
```bash
# Start with automatic reloading
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

# Debug mode with remote debugging
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

#### Production Build
```bash
# Create production-ready JAR
./mvnw clean package -Dspring.profiles.active=prod

# Verify JAR creation
ls -la target/ssl-server-*.jar

# Test production JAR
java -jar target/ssl-server-0.0.1-SNAPSHOT.jar
```

### Configuration Management

#### Environment-Specific Properties

**Development (`application-dev.properties`)**
```properties
# Development-specific settings
logging.level.com.snhu.sslserver=DEBUG
logging.level.org.springframework.security=DEBUG
app.security.detailed-error-messages=true

# Development SSL (relaxed for testing)
server.ssl.client-auth=none
```

**Test (`application-test.properties`)**
```properties
# Test environment settings
logging.level.com.snhu.sslserver=INFO
app.hash.default-algorithm=SHA-256
app.student.name=Test Student

# Test-specific configurations
spring.jpa.hibernate.ddl-auto=create-drop
```

**Production (`application-prod.properties`)**
```properties
# Production security settings
logging.level.com.snhu.sslserver=WARN
app.security.detailed-error-messages=false

# Production SSL configuration
server.ssl.client-auth=need
server.ssl.trust-store=${TRUST_STORE_PATH}
server.ssl.trust-store-password=${TRUST_STORE_PASSWORD}
```

#### Configuration Externalization
```bash
# Environment variables for production
export SSL_KEYSTORE_PASSWORD=your-secure-password
export STUDENT_NAME="Your Name"
export LOG_LEVEL=INFO

# Run with external configuration
java -jar ssl-server.jar --spring.profiles.active=prod
```

## Debugging and Troubleshooting

### Common Development Issues

#### SSL Certificate Problems
```bash
# Regenerate keystore if needed
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650 \
  -storepass snhu4321 -keypass snhu4321 \
  -dname "CN=localhost, OU=SNHU, O=SNHU, L=Manchester, ST=NH, C=US"

# Verify keystore contents
keytool -list -storetype PKCS12 -keystore src/main/resources/keystore.p12

# Test SSL connectivity
openssl s_client -connect localhost:8443 -verify_return_error
```

#### Port Binding Issues
```bash
# Check what's using port 8443
lsof -i :8443
netstat -tulpn | grep 8443

# Kill process using port
sudo kill -9 <PID>

# Use alternative port for testing
./mvnw spring-boot:run -Dserver.port=8444
```

#### Maven Build Problems
```bash
# Clear local repository
./mvnw dependency:purge-local-repository

# Force update dependencies
./mvnw clean install -U

# Skip tests during build (not recommended)
./mvnw clean package -DskipTests

# Verify Maven configuration
./mvnw help:effective-pom
```

#### Hash Generation Issues
```bash
# Test algorithm availability
java -cp target/classes com.snhu.sslserver.util.AlgorithmTester

# Debug hash computation
./mvnw spring-boot:run -Dlogging.level.com.snhu.sslserver.service=DEBUG

# Verify input encoding
echo -n "test input" | xxd -p  # Check hex encoding
```

### Debugging Techniques

#### Application Debugging
```java
// Add debug logging in service classes
@Slf4j
public class HashServiceImpl {
    public HashResult computeHash(String input, String algorithm) {
        log.debug("Computing hash for input length: {}, algorithm: {}", 
                 input.length(), algorithm);
        
        // Add breakpoint here for IDE debugging
        MessageDigest digest = cryptoProvider.createDigest(algorithm);
        
        log.debug("Successfully created digest instance for: {}", algorithm);
        return result;
    }
}
```

#### Network Debugging
```bash
# Monitor HTTP traffic
sudo tcpdump -i lo -A -s 0 port 8443

# Test with verbose curl
curl -k -v https://localhost:8443/api/v1/hash

# Check SSL handshake
openssl s_client -connect localhost:8443 -debug
```

#### Performance Debugging
```bash
# Profile application startup
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=app.jfr \
     -jar target/ssl-server-0.0.1-SNAPSHOT.jar

# Monitor memory usage
jstat -gc -t <PID> 1s

# Check thread usage
jstack <PID>
```

## IDE-Specific Configuration

### Eclipse Configuration

#### Project Setup
```bash
# Import as Maven project
# File → Import → Existing Maven Projects
# Select project root directory

# Configure build path
# Project Properties → Java Build Path
# Verify source folders: src/main/java, src/test/java, src/main/resources

# Setup run configuration
# Run → Run Configurations → Spring Boot App
# Main class: com.snhu.sslserver.ServerApplication
# Profile: dev
```

#### Debugging Setup
```bash
# Debug configuration
# Run → Debug Configurations → Spring Boot App
# Main class: com.snhu.sslserver.ServerApplication
# VM arguments: -Dspring.profiles.active=dev -Dlogging.level.com.snhu.sslserver=DEBUG

# Set breakpoints in service classes
# Double-click line numbers in editor
```

### IntelliJ IDEA Configuration

#### Project Import
```bash
# Open project
# File → Open → Select pom.xml
# Import as Maven project

# Configure SDK
# File → Project Structure → Project Settings → Project
# Project SDK: 1.8 or higher
```

#### Run Configuration
```bash
# Create Spring Boot run configuration
# Run → Edit Configurations → Add New → Spring Boot
# Main class: com.snhu.sslserver.ServerApplication
# Active profiles: dev
# VM options: -Dspring.profiles.active=dev
```

### VS Code Configuration

#### Extensions
```json
// .vscode/extensions.json
{
    "recommendations": [
        "vscjava.vscode-java-pack",
        "pivotal.vscode-spring-boot",
        "redhat.java",
        "vscjava.vscode-maven"
    ]
}
```

#### Launch Configuration
```json
// .vscode/launch.json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug Spring Boot",
            "request": "launch",
            "mainClass": "com.snhu.sslserver.ServerApplication",
            "projectName": "ssl-server",
            "args": "--spring.profiles.active=dev",
            "vmArgs": "-Dlogging.level.com.snhu.sslserver=DEBUG"
        }
    ]
}
```

## Continuous Integration Setup

### GitHub Actions Integration

#### Local CI Testing
```bash
# Install act for local GitHub Actions testing
# macOS: brew install act
# Linux: curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run CI locally
act -j test
act -j security-scan
```

#### CI Configuration Validation
```bash
# Validate workflow syntax
./mvnw validate

# Test build in clean environment
docker run --rm -v "$(pwd)":/usr/src/app -w /usr/src/app maven:3.6-openjdk-8 \
  mvn clean verify
```

## Database and External Dependencies

### H2 Database (if needed for future enhancements)
```properties
# H2 in-memory database for testing
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### External API Integration (future)
```java
// External service integration pattern
@Component
public class ExternalHashValidationService {
    @Retryable(value = {Exception.class}, maxAttempts = 3)
    public ValidationResult validateWithExternalService(String hash) {
        // Implementation with retry logic
    }
}
```

## Security Development Practices

### Secure Coding Checklist
- [ ] Input validation on all user inputs
- [ ] Output encoding for web responses
- [ ] Proper exception handling without information leakage
- [ ] Secure random number generation when needed
- [ ] Cryptographic operations in try-catch blocks
- [ ] No hardcoded secrets or credentials
- [ ] Proper logging without sensitive data
- [ ] HTTPS enforcement
- [ ] Security headers implementation

### Security Testing
```bash
# Test for common vulnerabilities
# XSS testing
curl -k "https://localhost:8443/api/v1/hash?data=<script>alert('xss')</script>"

# SQL injection testing (if database used)
curl -k "https://localhost:8443/api/v1/hash?data='; DROP TABLE users; --"

# Algorithm validation testing
curl -k "https://localhost:8443/api/v1/hash?algorithm=MD5"
```

This development guide provides comprehensive workflows and setup instructions for effective development of the secure checksum verification system, ensuring consistent development practices and high code quality.