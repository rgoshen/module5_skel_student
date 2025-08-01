# Project Structure

## Maven Standard Layout
This project follows Maven's standard directory layout for Java projects.

## Source Organization
```
src/
├── main/
│   ├── java/com/snhu/sslserver/
│   │   └── ServerApplication.java          # Main application class and REST controller
│   └── resources/
│       ├── application.properties          # Spring Boot configuration
│       ├── keystore.p12                   # SSL certificate keystore
│       ├── static/                        # Static web assets (empty)
│       └── templates/                     # View templates (empty)
└── test/
    └── java/com/snhu/server/
        └── ServerApplicationTests.java     # Basic Spring Boot tests
```

## Package Structure
- **Base package**: `com.snhu.sslserver`
- **Main class**: `ServerApplication` - Contains both `@SpringBootApplication` and `@RestController`
- **Test package**: `com.snhu.server` (note: different from main package)

## Key Files
- **pom.xml** - Maven project configuration and dependencies
- **application.properties** - SSL and server configuration
- **keystore.p12** - PKCS12 certificate store for SSL
- **mvnw/mvnw.cmd** - Maven wrapper scripts

## Configuration Notes
- SSL keystore located in `src/main/resources/`
- Server configured for HTTPS on port 8443
- REST endpoints defined in the main application class
- Static resources and templates directories exist but are empty

## Code Organization
- Single-file application structure (main class contains controller)
- REST controller embedded in main application class
- Minimal separation of concerns (educational/skeleton project)