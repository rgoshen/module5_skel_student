# Project Structure

## Maven Standard Layout
This project follows Maven's standard directory layout for Java projects with CS-305 specific requirements.

## Complete Project Structure
```
ssl-server/
├── pom.xml                                 # Maven configuration & dependencies
├── mvnw, mvnw.cmd                         # Maven wrapper scripts
├── README.md                              # Comprehensive project documentation
├── src/
│   ├── main/
│   │   ├── java/com/snhu/sslserver/
│   │   │   └── ServerApplication.java      # Main application & REST controller
│   │   └── resources/
│   │       ├── application.properties      # SSL & server configuration
│   │       ├── keystore.p12               # PKCS12 SSL certificate store
│   │       ├── static/                    # Static web assets (empty)
│   │       └── templates/                 # View templates (empty)
│   └── test/
│       └── java/com/snhu/server/
│           └── ServerApplicationTests.java # Spring Boot tests
```

## Package Structure
- **Base package**: `com.snhu.sslserver`
- **Main class**: `ServerApplication` - Contains both `@SpringBootApplication` and `@RestController`
- **Test package**: `com.snhu.server` (note: different from main package)

## Key Implementation Areas

### ServerApplication.java Structure
- **Main method**: Spring Boot application entry point
- **ServerController class**: Embedded REST controller
- **Critical method**: `myHash()` with `//FIXME` comment - **THIS IS THE MAIN IMPLEMENTATION TARGET**
- **Required endpoint**: `/hash` mapping for checksum generation

### Implementation Requirements
```java
// Required components in myHash() method:
// 1. Personalized data string with student's actual name
String data = "FirstName LastName"; // Replace with actual name

// 2. MessageDigest object creation
MessageDigest digest = MessageDigest.getInstance("SELECTED_ALGORITHM");

// 3. Hash generation and hex conversion
byte[] hashBytes = digest.digest(data.getBytes());
String hexHash = bytesToHex(hashBytes);

// 4. HTML response with data, algorithm, and hash
```

## Configuration Files
- **application.properties**: SSL configuration (port 8443, keystore settings)
- **keystore.p12**: Self-signed certificate for HTTPS
- **pom.xml**: Spring Boot dependencies and build configuration

## Development Workflow
1. **Primary task**: Implement checksum functionality in `myHash()` method
2. **Personalization**: Replace placeholder name with student's actual name
3. **Algorithm selection**: Choose collision-resistant hash algorithm
4. **Testing**: Verify via https://localhost:8443/hash
5. **Documentation**: Screenshot for assignment submission

## Code Organization Notes
- Single-file application structure (educational/skeleton project)
- REST controller embedded in main application class
- Minimal separation of concerns by design
- Focus on cryptographic implementation rather than architecture