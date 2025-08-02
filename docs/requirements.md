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
- WHEN converting hash output THE SYSTEM SHALL convert byte arrays to hexadecimal representation
- WHEN processing data THE SYSTEM SHALL use UTF-8 encoding for string to byte conversion

### FR-2: Data Personalization
- WHEN generating test data THE SYSTEM SHALL include the student's actual first and last name
- WHEN formatting the data string THE SYSTEM SHALL use the format "FirstName LastName"
- WHEN displaying data THE SYSTEM SHALL show the original input string alongside the hash

### FR-3: Web Interface
- WHEN serving content THE SYSTEM SHALL provide HTML-formatted responses
- WHEN displaying results THE SYSTEM SHALL show data, algorithm, and hash in a readable format
- WHEN handling requests THE SYSTEM SHALL respond to GET requests on the /hash endpoint

## Non-Functional Requirements

### NFR-1: Security
- WHEN selecting algorithms THE SYSTEM SHALL use cryptographically secure, collision-resistant hash functions
- WHEN handling errors THE SYSTEM SHALL not expose sensitive information in error messages
- WHEN configuring SSL THE SYSTEM SHALL use strong cipher suites and protocols

### NFR-2: Performance
- WHEN generating hashes THE SYSTEM SHALL complete operations within reasonable time limits
- WHEN serving requests THE SYSTEM SHALL handle concurrent connections efficiently
- WHEN starting up THE SYSTEM SHALL initialize within 30 seconds

### NFR-3: Maintainability
- WHEN writing code THE SYSTEM SHALL follow secure coding practices and include appropriate comments
- WHEN implementing features THE SYSTEM SHALL use clear, descriptive variable and method names
- WHEN handling exceptions THE SYSTEM SHALL provide meaningful error messages for debugging

### NFR-4: Educational Value
- WHEN implementing solutions THE SYSTEM SHALL demonstrate understanding of cryptographic principles
- WHEN documenting choices THE SYSTEM SHALL provide clear technical rationale
- WHEN presenting results THE SYSTEM SHALL show practical application of security concepts

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