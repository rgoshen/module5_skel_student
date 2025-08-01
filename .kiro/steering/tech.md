# Technology Stack

## Framework & Libraries
- **Spring Boot 2.2.4.RELEASE** - Main application framework
- **Spring Web** - Web MVC and RESTful capabilities
- **Spring Data REST** - RESTful web services
- **JUnit 5** - Testing framework
- **Java 8** - Target Java version
- **java.security.MessageDigest** - Cryptographic hash function library

## Build System
- **Maven** - Dependency management and build automation
- **Spring Boot Maven Plugin** - Application packaging and execution
- **Maven Wrapper** - Included mvnw/mvnw.cmd scripts

## Security & Cryptography
- **PKCS12 Keystore** - Certificate storage format
- **SSL/TLS** - Encrypted communication on port 8443
- **Collision-resistant hash algorithms** - SHA-256, SHA-3-256 recommended
- **Oracle Java Security Standard Algorithm Names** - Algorithm reference
- Default keystore password: `snhu4321`
- Key alias: `tomcat`

## Algorithm Requirements
- Must use collision-resistant algorithms (avoid MD5, SHA-1)
- Algorithms must be available in Oracle's Java Security Standard
- Focus on modern, uncompromised algorithms for security

## Common Commands

### Build & Run
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package

# Run application
./mvnw spring-boot:run

# Run packaged JAR
java -jar target/ssl-server-0.0.1-SNAPSHOT.jar
```

### Development & Testing
- Application runs on HTTPS port 8443
- Main endpoint: https://localhost:8443/hash
- Accept SSL certificate warning (self-signed certificate)
- Take screenshots for assignment verification

### Troubleshooting
- Port conflicts: Stop existing instances using port 8443
- Maven issues: Clear repository with `./mvnw dependency:purge-local-repository`
- SSL warnings: Expected with self-signed certificates