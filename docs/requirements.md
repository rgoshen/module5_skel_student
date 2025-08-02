# CS305 Checksum Verification System - Requirements

## Project Overview
This specification defines the requirements for implementing a secure checksum verification system using cryptographic hash functions to ensure data integrity and prevent malicious file substitution attacks for CS305 Module Five assignment.

## User Stories

### Story 1: Business Public Key Distribution
**As a** business distributing public keys to clients  
**I want** to provide checksum verification for downloaded public keys  
**So that** clients can verify file integrity and detect tampering

#### Acceptance Criteria
- WHEN a business needs to distribute a public key THE SYSTEM SHALL provide a secure checksum generation mechanism
- WHEN the checksum is generated THE SYSTEM SHALL use a collision-resistant cryptographic hash algorithm
- WHEN clients download the public key THE SYSTEM SHALL provide both the file and its corresponding checksum

### Story 2: Student Implementation Assignment
**As a** CS305 student  
**I want** to implement a working checksum verification system  
**So that** I can demonstrate understanding of cryptographic hash functions and secure coding practices

#### Acceptance Criteria
- WHEN implementing the hash function THE SYSTEM SHALL use the java.security.MessageDigest library
- WHEN generating a checksum THE SYSTEM SHALL include the student's first and last name in the data string
- WHEN the application runs THE SYSTEM SHALL display the original data, algorithm name, and generated hash value
- WHEN accessed via web browser THE SYSTEM SHALL serve content over HTTPS on port 8443

### Story 3: Algorithm Selection and Security
**As a** security-conscious developer  
**I want** to select an appropriate cryptographic hash algorithm  
**So that** the system is resistant to collision attacks and meets current security standards

#### Acceptance Criteria
- WHEN selecting a hash algorithm THE SYSTEM SHALL use algorithms from Oracle's Java Security Standard Algorithm Names
- WHEN evaluating algorithms THE SYSTEM SHALL prioritize collision resistance over performance
- WHEN implementing the algorithm THE SYSTEM SHALL avoid deprecated algorithms (MD5, SHA-1)
- WHEN documenting the choice THE SYSTEM SHALL provide technical justification for the selected algorithm

### Story 4: RESTful Web Service
**As a** client application  
**I want** to access checksum verification via a RESTful endpoint  
**So that** I can integrate checksum verification into automated workflows

#### Acceptance Criteria
- WHEN accessing the /hash endpoint THE SYSTEM SHALL respond with HTML containing verification data
- WHEN the endpoint is called THE SYSTEM SHALL return the original data string
- WHEN the endpoint is called THE SYSTEM SHALL return the algorithm name used
- WHEN the endpoint is called THE SYSTEM SHALL return the hexadecimal representation of the hash
- WHEN errors occur THE SYSTEM SHALL handle cryptographic exceptions gracefully

### Story 5: SSL/TLS Security
**As a** security-conscious user  
**I want** all communications to be encrypted  
**So that** data integrity is maintained during transmission

#### Acceptance Criteria
- WHEN the server starts THE SYSTEM SHALL only accept HTTPS connections
- WHEN configuring SSL THE SYSTEM SHALL use the provided PKCS12 keystore
- WHEN clients connect THE SYSTEM SHALL serve content exclusively on port 8443
- WHEN SSL errors occur THE SYSTEM SHALL provide appropriate error handling

### Story 6: Academic Submission Requirements
**As a** CS305 instructor  
**I want** to evaluate student understanding of cryptographic concepts  
**So that** I can assess learning objectives and provide appropriate feedback

#### Acceptance Criteria
- WHEN submitting the assignment THE SYSTEM SHALL include refactored source code
- WHEN documenting the implementation THE SYSTEM SHALL include algorithm justification
- WHEN demonstrating functionality THE SYSTEM SHALL include a browser screenshot showing the working application
- WHEN citing sources THE SYSTEM SHALL follow APA format guidelines

## Functional Requirements

### FR-1: Hash Generation
- WHEN a hash is requested THE SYSTEM SHALL generate a cryptographic hash of the personalized data string
- WHEN converting hash output THE SYSTEM SHALL convert byte arrays to hexadecimal representation using optimized algorithms
- WHEN processing data THE SYSTEM SHALL use UTF-8 encoding for string to byte conversion
- WHEN generating hashes THE SYSTEM SHALL implement proper resource management for cryptographic objects

### FR-2: Data Personalization
- WHEN generating test data THE SYSTEM SHALL include the student's actual first and last name
- WHEN formatting the data string THE SYSTEM SHALL validate and sanitize input data
- WHEN displaying data THE SYSTEM SHALL show the original input string alongside the hash
- WHEN handling user input THE SYSTEM SHALL implement proper input validation patterns

### FR-3: Web Interface
- WHEN serving content THE SYSTEM SHALL provide HTML-formatted responses with proper escaping
- WHEN displaying results THE SYSTEM SHALL show data, algorithm, and hash in a readable format
- WHEN handling requests THE SYSTEM SHALL respond to GET requests on the /hash endpoint
- WHEN processing requests THE SYSTEM SHALL implement proper HTTP status code handling

### FR-4: Code Quality and Documentation
- WHEN implementing methods THE SYSTEM SHALL include comprehensive Javadoc documentation
- WHEN writing code THE SYSTEM SHALL follow consistent naming conventions and formatting
- WHEN creating classes THE SYSTEM SHALL implement single responsibility principle
- WHEN handling complex logic THE SYSTEM SHALL include inline comments explaining business logic

### FR-5: Architecture and Design Patterns
- WHEN organizing code THE SYSTEM SHALL implement separation of concerns through service layers
- WHEN managing dependencies THE SYSTEM SHALL use dependency injection patterns
- WHEN handling configuration THE SYSTEM SHALL externalize configuration properties
- WHEN implementing services THE SYSTEM SHALL follow interface-based design patterns

## Non-Functional Requirements

### NFR-1: Security Best Practices
- WHEN selecting algorithms THE SYSTEM SHALL use cryptographically secure, collision-resistant hash functions
- WHEN handling errors THE SYSTEM SHALL not expose sensitive information in error messages or stack traces
- WHEN configuring SSL THE SYSTEM SHALL use strong cipher suites and protocols
- WHEN validating input THE SYSTEM SHALL implement comprehensive input sanitization and validation
- WHEN logging information THE SYSTEM SHALL not log sensitive data or cryptographic keys
- WHEN handling exceptions THE SYSTEM SHALL implement secure error handling patterns

### NFR-2: Code Readability and Maintainability
- WHEN writing code THE SYSTEM SHALL follow consistent Java naming conventions (camelCase, PascalCase)
- WHEN organizing classes THE SYSTEM SHALL implement logical package structure and file organization
- WHEN implementing methods THE SYSTEM SHALL keep methods focused and under 20 lines when possible
- WHEN using variables THE SYSTEM SHALL use descriptive, self-documenting names
- WHEN formatting code THE SYSTEM SHALL maintain consistent indentation and spacing
- WHEN creating complex logic THE SYSTEM SHALL break down into smaller, testable units

### NFR-3: Documentation Standards
- WHEN creating public methods THE SYSTEM SHALL include comprehensive Javadoc with @param, @return, @throws
- WHEN implementing complex algorithms THE SYSTEM SHALL include inline comments explaining the logic
- WHEN defining classes THE SYSTEM SHALL include class-level Javadoc describing purpose and usage
- WHEN handling edge cases THE SYSTEM SHALL document assumptions and constraints
- WHEN using external libraries THE SYSTEM SHALL document dependencies and their purposes

### NFR-4: Architecture and Design Quality
- WHEN designing services THE SYSTEM SHALL follow Single Responsibility Principle (SRP)
- WHEN creating dependencies THE SYSTEM SHALL implement Dependency Inversion Principle (DIP)
- WHEN extending functionality THE SYSTEM SHALL follow Open/Closed Principle (OCP)
- WHEN designing interfaces THE SYSTEM SHALL follow Interface Segregation Principle (ISP)
- WHEN implementing inheritance THE SYSTEM SHALL follow Liskov Substitution Principle (LSP)
- WHEN writing code THE SYSTEM SHALL eliminate duplication following DRY principle

### NFR-5: Performance and Optimization
- WHEN generating hashes THE SYSTEM SHALL complete operations within reasonable time limits
- WHEN serving requests THE SYSTEM SHALL handle concurrent connections efficiently
- WHEN starting up THE SYSTEM SHALL initialize within 30 seconds
- WHEN processing strings THE SYSTEM SHALL use efficient string handling and avoid unnecessary object creation
- WHEN working with byte arrays THE SYSTEM SHALL implement memory-efficient operations
- WHEN caching results THE SYSTEM SHALL implement appropriate caching strategies where beneficial

### NFR-6: Error Handling and Resilience
- WHEN encountering exceptions THE SYSTEM SHALL implement graceful degradation patterns
- WHEN handling failures THE SYSTEM SHALL provide meaningful error messages for users
- WHEN logging errors THE SYSTEM SHALL include sufficient context for debugging without exposing sensitive data
- WHEN validating input THE SYSTEM SHALL provide clear feedback on validation failures
- WHEN processing requests THE SYSTEM SHALL implement proper HTTP status code responses

### NFR-7: Testing and Quality Assurance
- WHEN implementing functionality THE SYSTEM SHALL be designed for unit testability
- WHEN creating methods THE SYSTEM SHALL have clear, testable interfaces
- WHEN handling edge cases THE SYSTEM SHALL include appropriate test coverage
- WHEN implementing security features THE SYSTEM SHALL include security-focused test cases

## Constraints

### Technical Constraints
- MUST use Java 8 or higher
- MUST use Spring Boot framework as provided in skeleton code
- MUST use Maven for build management
- MUST implement using java.security.MessageDigest library
- MUST serve content on port 8443 with SSL/TLS encryption

### Academic Constraints
- MUST complete assignment individually
- MUST include personal name in data string for verification
- MUST submit both source code and documentation template
- MUST provide screenshot evidence of working application
- MUST cite all sources in APA format

### Security Constraints
- MUST NOT use deprecated hash algorithms (MD5, SHA-1)
- MUST use algorithms from Oracle's standard list
- MUST implement proper exception handling for cryptographic operations
- MUST maintain SSL/TLS encryption for all communications

## Assumptions

### Environmental Assumptions
- Development environment has Java 8+ installed
- Maven or Maven wrapper is available for builds
- Eclipse IDE is available for development (recommended)
- Web browser supports HTTPS and can handle self-signed certificates

### Knowledge Assumptions
- Student has basic understanding of cryptographic hash functions
- Student is familiar with Spring Boot framework basics
- Student understands RESTful web service concepts
- Student can work with Maven build tools

### Infrastructure Assumptions
- Local development machine can run Spring Boot applications
- Port 8443 is available for HTTPS server
- File system allows read/write access for keystore files
- Network connectivity allows access to Maven repositories for dependencies