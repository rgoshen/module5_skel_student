# CLAUDE.md

This file provides comprehensive guidance to Claude Code (claude.ai/code) when working with the CS305 Checksum Verification Project.

> **📊 Project Status**: Before starting development, check [PROJECT_STATUS.md](PROJECT_STATUS.md) to see current progress and priority tasks.

## 🚀 Quick Start

### Current Development Focus
> **⚠️ Priority Tasks**: See [PROJECT_STATUS.md](PROJECT_STATUS.md) for current incomplete tasks requiring attention.

## 🎯 Project Overview

**CS-305 Module Five Checksum Verification Project** - A secure, Spring Boot-based web application implementing cryptographic hash generation for file integrity verification. This project demonstrates real-world security practices while meeting academic requirements.

### Business Context
- Organizations need to securely distribute public keys and sensitive files
- Clients require reliable file integrity verification to detect tampering
- Solution provides cryptographically secure checksum generation via RESTful API

### Educational Objectives
- Demonstrate cryptographic hash function implementation
- Show secure coding practices and architecture patterns
- Integrate student personalization (name) into hash computation
- Deploy over HTTPS with SSL/TLS encryption

## 🚀 Quick Start Commands

### Core Development Workflow
```bash
# Initial setup
./mvnw clean compile

# Run tests
./mvnw test

# Start development server
./mvnw spring-boot:run

# Access application
# URL: https://localhost:8443/api/v1/hash
# Accept SSL certificate warning (self-signed)

# Package for deployment
./mvnw package
java -jar target/ssl-server-0.0.1-SNAPSHOT.jar
```

### Code Quality and Formatting
```bash
# Check formatting (runs automatically on build)
./mvnw spotless:check

# Auto-format all code
./mvnw spotless:apply

# Setup pre-commit hooks (first time only)
chmod +x setup-pre-commit.sh
./setup-pre-commit.sh
```

### Testing Commands
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=HashServiceTest

# Run with coverage
./mvnw test jacoco:report

# Integration tests
./mvnw verify
```

## 🏗️ Architecture Overview

### Clean Architecture Implementation
The project follows Clean Architecture with clear separation of concerns:

```
Presentation Layer (controller/)
    ↓
Business Logic Layer (service/)
    ↓
Infrastructure Layer (provider/, strategy/)
```

### Key Components

**Core Interfaces**
- `IHashService` - Main business logic interface
- `ICryptographicProvider` - Cryptographic operations abstraction
- `IInputValidator` - Input validation and sanitization
- `HashAlgorithmStrategy` - Strategy pattern for algorithms

**Main Implementations**
- `HashServiceImpl` - Orchestrates hash generation workflow
- `CryptographicProvider` - Handles MessageDigest operations
- `SecurityInputValidator` - Comprehensive input validation
- `HashController` - RESTful API with content negotiation

**Security Features**
- Algorithm validation (only secure algorithms allowed)
- Input sanitization and validation
- Secure error handling without information leakage
- SSL/TLS encryption on port 8443

### Request Flow
```
Client Request → HashController → HashService → CryptographicProvider
                     ↓                ↓              ↓
              Content Negotiation → Validation → MessageDigest
                     ↓                ↓              ↓
              HTML/JSON Response ← Hash Result ← Byte Array
```

## 🔐 Security Implementation

### Supported Algorithms (Secure)
```java
// Approved secure algorithms
"SHA-256"    // Default, excellent performance/security balance
"SHA-3-256"  // Latest NIST standard, quantum-resistant design
"SHA-512"    // Higher security margin
"SHA-3-512"  // Maximum security
```

### Rejected Algorithms (Security Risk)
```java
// NEVER USE - Cryptographically broken
"MD5"        // Collision vulnerabilities
"SHA-1"      // Deprecated, collision attacks possible
```

### Input Data Format
Hash input follows pattern: `"StudentName: [name] Data: [input]"`
- Student name configured via `app.student.name=Rick Goshen`
- Additional data can be provided via API parameter

### SSL Configuration
```properties
# HTTPS only on port 8443
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=snhu4321
server.ssl.key-alias=tomcat
server.ssl.key-store-type=PKCS12
```

## 📊 API Endpoints

### Primary Endpoint
**GET** `/api/v1/hash`
- **Query Parameters**:
  - `algorithm` (optional) - Hash algorithm to use (default: SHA-256)
  - `data` (optional) - Additional data to include in hash
- **Content Negotiation**:
  - `Accept: text/html` → HTML response for browsers
  - `Accept: application/json` → JSON response for APIs

### Response Formats

**HTML Response** (for browsers):
```html
<div>
    <h2>Checksum Verification</h2>
    <p><strong>Original Data:</strong> StudentName: Rick Goshen Data: [input]</p>
    <p><strong>Algorithm:</strong> SHA-256</p>
    <p><strong>Hash Value:</strong> a1b2c3d4...</p>
</div>
```

**JSON Response** (for APIs):
```json
{
    "originalData": "StudentName: Rick Goshen Data: [input]",
    "algorithm": "SHA-256",
    "hexHash": "a1b2c3d4...",
    "timestamp": "2025-01-15T10:30:00Z",
    "computationTimeMs": 5
}
```

## 🧪 Testing Strategy

### Test Structure
```
src/test/java/com/snhu/sslserver/
├── controller/HashControllerTest.java     # REST endpoint tests
├── service/HashServiceTest.java          # Business logic tests
├── provider/CryptographicProviderTest.java # Infrastructure tests
├── integration/HashEndpointIntegrationTest.java # Full HTTP tests
└── ServerApplicationTests.java           # Application context tests
```

### Key Test Scenarios
- **Algorithm Security**: Verify deprecated algorithms are rejected
- **Hash Consistency**: Same input produces same hash
- **Input Validation**: Proper sanitization and validation
- **Error Handling**: Secure error responses without information leakage
- **SSL Integration**: HTTPS connectivity and certificate handling

## 🔄 Development Workflow

### Adding New Hash Algorithms
1. Create strategy class implementing `HashAlgorithmStrategy`
```java
@Component
public class NewAlgorithmStrategy implements HashAlgorithmStrategy {
    @Override
    public byte[] computeHash(String input) throws CryptographicException {
        // Implementation
    }

    @Override
    public boolean isSecure() {
        return true; // Only if cryptographically secure
    }
}
```

2. Register in `HashAlgorithmFactory`
3. Add integration tests
4. Update documentation

### Modifying Input Validation
- Edit `SecurityInputValidator.validateAndSanitize()`
- Update `ValidationResult` if new fields needed
- Ensure error messages remain user-safe
- Add corresponding tests

### Error Handling Pattern
```java
try {
    // Cryptographic operation
} catch (NoSuchAlgorithmException e) {
    String errorId = UUID.randomUUID().toString();
    log.error("Algorithm error [{}]: {}", errorId, e.getMessage(), e);
    throw new CryptographicException("Hash computation failed: " + errorId, e);
}
```

## 📋 Configuration Properties

### Application Configuration (`application.properties`)
```properties
# Server Configuration
server.port=8443

# SSL Configuration
server.ssl.key-alias=tomcat
server.ssl.key-store-password=snhu4321
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-type=PKCS12

# Hash Service Configuration
app.hash.default-algorithm=SHA-256
app.student.name=Rick Goshen

# Security Configuration
app.security.reject-deprecated-algorithms=true
app.security.max-input-length=10000
```

### Environment Profiles
- `application-dev.properties` - Development settings (debug logging)
- `application-test.properties` - Test configuration
- `application-prod.properties` - Production settings (minimal logging)

## 🔧 Troubleshooting Guide

### Common Issues

**SSL Certificate Warnings**
- Expected with self-signed certificates
- Browser: Click "Advanced" → "Proceed to localhost"
- Testing: Use `-k` flag with curl: `curl -k https://localhost:8443/api/v1/hash`

**Port 8443 Already in Use**
```bash
# Find process using port
lsof -i :8443
# Kill process if needed
kill -9 <PID>
```

**Maven Build Failures**
```bash
# Clear local repository
./mvnw dependency:purge-local-repository
# Force update dependencies
./mvnw clean install -U
```

**Hash Generation Errors**
- Verify algorithm name spelling matches Oracle standards exactly
- Check MessageDigest.getInstance() calls
- Ensure proper exception handling

### Development Tips
- Use `@Slf4j` for logging (already configured)
- Follow naming conventions: interfaces start with 'I'
- All cryptographic operations should be in try-catch blocks
- Never log sensitive data or raw hash inputs
- Use constructor injection for dependencies

## 📚 Additional Resources

### Related Documentation
- `ARCHITECTURE.md` - Detailed design and architectural decisions
- `REQUIREMENTS.md` - Complete requirements specification
- `DEVELOPMENT.md` - Extended development workflows and setup
- `SECURITY.md` - Comprehensive security guidelines and cryptographic details

### External References
- [Oracle Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html)
- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-configure-ssl)
- [MessageDigest JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/MessageDigest.html)

### CI/CD Integration
- GitHub Actions workflows validate security and code quality
- Pre-commit hooks ensure formatting standards
- Branch protection requires all checks to pass before merge
- Comprehensive security validation including cryptographic standards

## 🎯 Success Criteria

**Functional Requirements**: ✅
- Hash generation with secure algorithms
- RESTful API with content negotiation
- SSL/TLS encryption
- Student name integration

**Security Requirements**: ✅
- Collision-resistant algorithms only
- Input validation and sanitization
- Secure error handling
- No information leakage

**Code Quality**: ✅
- Clean architecture patterns
- Comprehensive test coverage
- Professional documentation
- Consistent formatting standards
