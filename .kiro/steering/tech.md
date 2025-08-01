# Technology Stack

## Framework & Libraries
- **Spring Boot 2.2.4.RELEASE** - Main application framework
- **Spring Web** - Web MVC and REST capabilities
- **Spring Data REST** - RESTful web services
- **JUnit 5** - Testing framework
- **Java 8** - Target Java version

## Build System
- **Maven** - Dependency management and build tool
- **Spring Boot Maven Plugin** - Application packaging and execution

## Security & SSL
- **PKCS12 Keystore** - Certificate storage format
- **SSL/TLS** - Encrypted communication on port 8443
- Default keystore password: `snhu4321`
- Key alias: `tomcat`

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

### Development
- Application runs on HTTPS port 8443
- Access via: https://localhost:8443
- Hot reload available in development mode