# REQUIREMENTS.md

## Business Requirements Overview

This specification defines the requirements for implementing a secure checksum verification system using cryptographic hash functions. The system addresses the business need for secure public key distribution by providing integrity verification capabilities that detect tampering and malicious file substitution attacks.

### Primary Business Problem
- **Challenge**: Organizations need to distribute public keys and sensitive files to clients securely
- **Risk**: Downloaded files can be tampered with during transmission or storage
- **Threat**: Attackers may substitute malicious files with identical names
- **Solution**: Cryptographically secure checksum verification system

### Educational Context
This implementation serves CS305 Module Five assignment objectives while demonstrating real-world cryptographic security practices used in production systems.

## Functional Requirements

### FR-1: Secure Checksum Generation

**User Story**: As a business distributing public keys to clients, I want to provide checksum verification for downloaded public keys, so that clients can verify file integrity and detect tampering.

#### Acceptance Criteria
- **FR-1.1**: System SHALL provide secure checksum generation mechanism using collision-resistant cryptographic hash algorithms
- **FR-1.2**: System SHALL use java.security.MessageDigest library for hash computation
- **FR-1.3**: System SHALL generate consistent hash values for identical inputs
- **FR-1.4**: System SHALL detect any changes in input data through hash value differences

#### Implementation Requirements
```java
// Required MessageDigest usage pattern
MessageDigest digest = MessageDigest.getInstance("ALGORITHM_NAME");
digest.update(inputData.getBytes(StandardCharsets.UTF_8));
byte[] hashBytes = digest.digest();
String hexHash = bytesToHex(hashBytes);
```

### FR-2: Algorithm Security Validation

**User Story**: As a security-conscious developer, I want to select an appropriate cryptographic hash algorithm, so that the system is resistant to collision attacks and meets current security standards.

#### Acceptance Criteria
- **FR-2.1**: System SHALL use algorithms from Oracle's Java Security Standard Algorithm Names
- **FR-2.2**: System SHALL prioritize collision resistance over performance considerations
- **FR-2.3**: System SHALL explicitly reject deprecated algorithms (MD5, SHA-1)
- **FR-2.4**: System SHALL provide technical justification for selected algorithm

#### Approved Algorithms
```java
// Secure algorithms (APPROVED)
"SHA-256"    // Primary choice: excellent security/performance balance
"SHA-3-256"  // Alternative: latest NIST standard, quantum-resistant
"SHA-512"    // High security: stronger but slower
"SHA-3-512"  // Maximum security: latest and strongest

// Deprecated algorithms (REJECTED)
"MD5"        // FORBIDDEN: collision vulnerabilities
"SHA-1"      // FORBIDDEN: deprecated, collision attacks
```

### FR-3: RESTful API Implementation

**User Story**: As a client application, I want to access checksum verification via a RESTful endpoint, so that I can integrate checksum verification into automated workflows.

#### Acceptance Criteria
- **FR-3.1**: System SHALL provide GET endpoint at `/api/v1/hash`
- **FR-3.2**: System SHALL support content negotiation (HTML/JSON responses)
- **FR-3.3**: System SHALL accept algorithm parameter with SHA-256 as default
- **FR-3.4**: System SHALL return original data, algorithm name, and hexadecimal hash value
- **FR-3.5**: System SHALL handle cryptographic exceptions gracefully

#### API Specification
```yaml
# OpenAPI specification excerpt
/api/v1/hash:
  get:
    parameters:
      - name: algorithm
        in: query
        required: false
        default: "SHA-256"
        schema:
          type: string
          enum: ["SHA-256", "SHA-3-256", "SHA-512", "SHA-3-512"]
      - name: data
        in: query
        required: false
        schema:
          type: string
          maxLength: 10000
    responses:
      200:
        description: Successful hash generation
        content:
          text/html:
            schema:
              type: string
          application/json:
            schema:
              $ref: '#/components/schemas/HashResult'
```

### FR-4: Student Name Integration

**User Story**: As a CS305 student, I want to implement a working checksum verification system, so that I can demonstrate understanding of cryptographic hash functions and secure coding practices.

#### Acceptance Criteria
- **FR-4.1**: System SHALL include student's first and last name in hash input data
- **FR-4.2**: System SHALL display original data string containing student name
- **FR-4.3**: System SHALL format input as "StudentName: [name] Data: [additional]"
- **FR-4.4**: System SHALL use UTF-8 encoding for name data in hash computation

#### Data Format Requirements
```java
// Required input format pattern
String studentName = "Rick Goshen"; // Configurable via properties
String additionalData = requestParameter; // Optional additional data
String hashInput = String.format("StudentName: %s Data: %s", studentName, additionalData);
```

## Security Requirements

### SR-1: Cryptographic Security

**User Story**: As a security auditor, I want the system to follow security best practices, so that it is resistant to common attacks and vulnerabilities.

#### Acceptance Criteria
- **SR-1.1**: System SHALL use only collision-resistant hash algorithms
- **SR-1.2**: System SHALL validate algorithm security before use
- **SR-1.3**: System SHALL prevent hash collision attacks through algorithm selection
- **SR-1.4**: System SHALL implement proper random number generation if needed

#### Security Validation Logic
```java
public class SecurityValidator {
    private static final Set<String> SECURE_ALGORITHMS = Set.of(
        "SHA-256", "SHA-384", "SHA-512",
        "SHA-3-256", "SHA-3-384", "SHA-3-512"
    );

    private static final Set<String> DEPRECATED_ALGORITHMS = Set.of(
        "MD5", "SHA-1"
    );

    public boolean isAlgorithmSecure(String algorithm) {
        if (DEPRECATED_ALGORITHMS.contains(algorithm.toUpperCase())) {
            throw new SecurityException("Deprecated algorithm not allowed: " + algorithm);
        }
        return SECURE_ALGORITHMS.contains(algorithm.toUpperCase());
    }
}
```

### SR-2: Input Validation and Sanitization

**User Story**: As a security-conscious developer, I want comprehensive input validation, so that the system prevents injection attacks and handles malformed data safely.

#### Acceptance Criteria
- **SR-2.1**: System SHALL implement comprehensive input sanitization
- **SR-2.2**: System SHALL validate input length and content
- **SR-2.3**: System SHALL prevent code injection through input validation
- **SR-2.4**: System SHALL handle special characters and encoding properly

#### Validation Requirements
```java
public class InputValidator {
    private static final int MAX_INPUT_LENGTH = 10000;
    private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[\\w\\s\\-\\.@]+$");

    public ValidationResult validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return ValidationResult.failure("Input cannot be empty");
        }

        if (input.length() > MAX_INPUT_LENGTH) {
            return ValidationResult.failure("Input exceeds maximum length");
        }

        String sanitized = input.trim().replaceAll("[<>\"'&]", "");
        return ValidationResult.success(sanitized);
    }
}
```

### SR-3: Error Handling Security

**User Story**: As a security auditor, I want secure error handling, so that sensitive information is not exposed through error messages or stack traces.

#### Acceptance Criteria
- **SR-3.1**: System SHALL not expose sensitive information in error messages
- **SR-3.2**: System SHALL not return stack traces to clients
- **SR-3.3**: System SHALL log detailed errors server-side with correlation IDs
- **SR-3.4**: System SHALL implement secure logging without sensitive data

#### Secure Error Handling Pattern
```java
public class SecureErrorHandler {
    public ResponseEntity<ErrorResponse> handleCryptographicException(CryptographicException e) {
        // Generate correlation ID for debugging
        String errorId = UUID.randomUUID().toString();

        // Log detailed error server-side only
        log.error("Cryptographic operation failed [{}]: {}", errorId, e.getMessage(), e);

        // Return generic error to client
        ErrorResponse response = ErrorResponse.builder()
            .errorId(errorId)
            .message("Hash computation failed. Please check your input and try again.")
            .timestamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

## Technical Requirements

### TR-1: SSL/TLS Encryption

**User Story**: As a security-conscious user, I want all communications to be encrypted, so that data integrity is maintained during transmission.

#### Acceptance Criteria
- **TR-1.1**: System SHALL only accept HTTPS connections
- **TR-1.2**: System SHALL use provided PKCS12 keystore configuration
- **TR-1.3**: System SHALL serve content exclusively on port 8443
- **TR-1.4**: System SHALL implement proper SSL error handling

#### SSL Configuration Requirements
```properties
# Required SSL configuration
server.port=8443
server.ssl.enabled=true
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-alias=tomcat
server.ssl.key-store-password=snhu4321

# Security protocols
server.ssl.protocol=TLS
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3
server.ssl.ciphers=TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
```

### TR-2: Framework and Library Requirements

**User Story**: As a CS305 student, I want to use specified frameworks and libraries, so that my implementation meets educational requirements and industry standards.

#### Acceptance Criteria
- **TR-2.1**: System SHALL use Spring Boot 2.2.4.RELEASE framework
- **TR-2.2**: System SHALL use Java 8+ language features appropriately
- **TR-2.3**: System SHALL use Maven for dependency management
- **TR-2.4**: System SHALL include proper testing framework integration

#### Technology Stack Requirements
```xml
<!-- Required dependencies in pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.4.RELEASE</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-rest</artifactId>
    </dependency>
</dependencies>
```

### TR-3: Performance and Scalability

**User Story**: As a system administrator, I want the application to perform efficiently, so that it can handle reasonable load without degradation.

#### Acceptance Criteria
- **TR-3.1**: System SHALL compute hashes within reasonable time limits (< 100ms for typical inputs)
- **TR-3.2**: System SHALL handle concurrent requests safely
- **TR-3.3**: System SHALL implement efficient memory usage patterns
- **TR-3.4**: System SHALL provide appropriate caching where beneficial

## Quality Requirements

### QR-1: Code Quality and Documentation

**User Story**: As a developer maintaining the system, I want clean, well-documented code, so that the system is maintainable and follows professional standards.

#### Acceptance Criteria
- **QR-1.1**: System SHALL include comprehensive Javadoc documentation
- **QR-1.2**: System SHALL follow consistent naming conventions and formatting
- **QR-1.3**: System SHALL implement single responsibility principle
- **QR-1.4**: System SHALL include inline comments explaining business logic

#### Documentation Standards
```java
/**
 * Computes cryptographic hash for given input using specified algorithm.
 *
 * This method validates the input and algorithm, then delegates to the
 * cryptographic provider for actual hash computation. The result includes
 * metadata about the operation for audit and verification purposes.
 *
 * @param input The data string to hash, including student name and additional data
 * @param algorithm The hash algorithm name (must be from approved secure list)
 * @return HashResult containing original data, algorithm used, and hex hash value
 * @throws CryptographicException if algorithm is insecure or computation fails
 * @throws IllegalArgumentException if input validation fails
 *
 * @see CryptographicProvider#createDigest(String)
 * @see SecurityValidator#isAlgorithmSecure(String)
 * @since 1.0
 */
public HashResult computeHash(String input, String algorithm) throws CryptographicException {
    // Implementation with detailed comments
}
```

### QR-2: Testing Requirements

**User Story**: As a quality assurance engineer, I want comprehensive test coverage, so that the system behavior is verified and regressions are prevented.

#### Acceptance Criteria
- **QR-2.1**: System SHALL include unit tests for all major components
- **QR-2.2**: System SHALL include integration tests for API endpoints
- **QR-2.3**: System SHALL include security-focused test scenarios
- **QR-2.4**: System SHALL achieve minimum 80% code coverage

#### Test Categories Required
```java
// Unit Test Categories
@Test void shouldComputeHashWithValidInput() { }
@Test void shouldRejectInsecureAlgorithm() { }
@Test void shouldValidateInputSafely() { }
@Test void shouldHandleCryptographicErrors() { }

// Integration Test Categories
@Test void shouldReturnHtmlResponseForBrowserRequest() { }
@Test void shouldReturnJsonResponseForApiRequest() { }
@Test void shouldEnforceHttpsOnlyAccess() { }
@Test void shouldHandleInvalidAlgorithmParameter() { }

// Security Test Categories
@Test void shouldPreventInformationLeakageInErrors() { }
@Test void shouldSanitizeInputProperlyForXSS() { }
@Test void shouldRejectDeprecatedAlgorithmsCompletely() { }
@Test void shouldLogSecurityEventsAppropriately() { }
```

## Academic Requirements

### AR-1: Assignment Submission Requirements

**User Story**: As a CS305 instructor, I want to evaluate student understanding of cryptographic concepts, so that I can assess learning objectives and provide appropriate feedback.

#### Acceptance Criteria
- **AR-1.1**: System SHALL include refactored source code with implemented functionality
- **AR-1.2**: System SHALL include algorithm selection justification documentation
- **AR-1.3**: System SHALL include browser screenshot demonstrating working application
- **AR-1.4**: System SHALL include APA format citations for all sources

#### Submission Deliverables
1. **Modified Source Code**
   - `ServerApplication.java` with implemented hash functionality
   - Working RESTful endpoint
   - Proper exception handling

2. **Documentation Template Completion**
   - Algorithm cipher selection and justification
   - Code implementation explanation
   - Security considerations discussion

3. **Verification Evidence**
   - Screenshot showing working application in browser
   - URL demonstration: `https://localhost:8443/api/v1/hash`
   - Output showing student name, algorithm, and hash value

4. **Academic Citations**
   - Oracle Java Security Standard Algorithm Names reference
   - Cryptographic research sources
   - Spring Boot documentation references

### AR-2: Learning Objective Demonstration

**User Story**: As a CS305 student, I want to demonstrate mastery of security concepts, so that I can show understanding of cryptographic principles and secure development practices.

#### Learning Objectives Assessed
- **LO-1**: Understanding of cryptographic hash function properties
- **LO-2**: Ability to select appropriate security algorithms
- **LO-3**: Implementation of secure coding practices
- **LO-4**: Integration of security controls in web applications
- **LO-5**: Recognition of common security vulnerabilities and mitigations

#### Assessment Criteria
```java
// Demonstrates understanding of hash function properties
MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Collision resistance
String hexHash = bytesToHex(digest.digest(input.getBytes())); // Deterministic output

// Shows algorithm selection reasoning
// "SHA-256 chosen for collision resistance and performance balance"
// "MD5 rejected due to known collision vulnerabilities"

// Implements secure coding practices
try {
    // Cryptographic operations in try-catch
} catch (NoSuchAlgorithmException e) {
    // Proper exception handling without information leakage
}

// Integrates security in web application
@RestController
public class HashController {
    // HTTPS-only endpoint with input validation
}
```

## Compliance and Standards

### CS-1: Industry Standards Compliance

#### Required Standards
- **Oracle Java Security Standard Algorithm Names**: All algorithms must be from approved list
- **OWASP Secure Coding Practices**: Input validation, error handling, logging
- **Spring Security Best Practices**: HTTPS enforcement, security headers
- **NIST Cryptographic Guidelines**: Current algorithm recommendations

### CS-2: Educational Institution Requirements

#### Academic Integrity
- Individual implementation required
- Original code development (no copying from external sources)
- Proper attribution for all references and sources
- Academic honesty in documentation and explanation

#### Assessment Standards
- Functional requirements: 40% of grade
- Security implementation: 30% of grade
- Code quality and documentation: 20% of grade
- Academic presentation: 10% of grade

This requirements specification provides comprehensive guidance for implementing a secure, educationally valuable checksum verification system that meets both technical and academic objectives.
