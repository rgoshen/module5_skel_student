# CS305 Module Five Checksum Verification Project

## 📋 Project Overview

**Purpose**: Implement a secure checksum verification system using cryptographic hash functions to ensure data integrity and prevent malicious file substitution attacks. This project demonstrates the practical application of cryptographic hash functions in a real-world scenario where businesses need to securely distribute public keys to clients.

**Business Scenario**: A business needs to distribute its public key to clients via website download. Clients must be able to verify the integrity of the downloaded public key using checksum verification to ensure it hasn't been tampered with during transmission.

**Security Objective**: Deploy collision-resistant cryptographic hash algorithms to generate checksums that cannot be easily forged by attackers attempting to substitute malicious files.

---

## 🏗️ Project Structure

```
ssl-server/
├── pom.xml                                 # Maven configuration & dependencies
├── mvnw, mvnw.cmd                         # Maven wrapper scripts
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
└── README.md                              # Project documentation
```

---

## 🛠️ Technology Stack

### Framework & Libraries
- **Spring Boot 2.2.4.RELEASE** - Main application framework
- **Spring Web** - Web MVC and RESTful capabilities
- **Spring Data REST** - RESTful web services
- **Java 8** - Target Java version
- **JUnit 5** - Testing framework

### Build System
- **Maven** - Dependency management and build automation
- **Spring Boot Maven Plugin** - Application packaging and execution

### Security & SSL
- **PKCS12 Keystore** - Certificate storage format
- **SSL/TLS** - Encrypted communication on port 8443
- **java.security.MessageDigest** - Cryptographic hash function library
- Default keystore password: `snhu4321`
- Key alias: `tomcat`

---

## 🎯 Technical Requirements

### Core Implementation Specifications

#### 1. Algorithm Selection
- Research and select an appropriate cryptographic hash algorithm from Oracle's Java Security Standard Algorithm Names
- Algorithm must be **collision-resistant** to prevent attackers from creating malicious files with identical checksums
- Document algorithm choice with technical justification

#### 2. Checksum Generation Implementation
```java
// Required implementation components:
// 1. MessageDigest object creation
MessageDigest digest = MessageDigest.getInstance("SELECTED_ALGORITHM");

// 2. Hash generation from personalized data string
String data = "FirstName LastName"; // Replace with your actual name
byte[] hashBytes = digest.digest(data.getBytes());

// 3. Byte-to-hex conversion
String hexHash = bytesToHex(hashBytes);

// 4. RESTful endpoint implementation
@RequestMapping("/hash")
public String generateChecksum() {
    // Implementation details
}
```

#### 3. RESTful API Requirements
- **Endpoint**: `/hash`
- **Method**: GET request mapping
- **Response Format**: HTML displaying:
  - Original data string (containing your first and last name)
  - Selected algorithm name
  - Generated checksum hash value
- **Access URL**: `https://localhost:8443/hash`

#### 4. Security Validation
- Hash function must use secure, industry-standard algorithms
- Implementation must prevent collision attacks
- SSL/TLS encryption for secure data transmission
- Proper error handling for cryptographic operations

---

## 🔧 Setup & Installation

### Prerequisites
- Java 8 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Text editor or IDE (Eclipse recommended)

### Installation Steps

1. **Download & Import Project**
   ```bash
   # Download the Module Five Coding Assignment Checksum Verification Code Base
   # Import into Eclipse as new Maven project
   ```

2. **Verify Project Structure**
   ```bash
   # Ensure all required files are present
   ls -la src/main/java/com/snhu/sslserver/
   ls -la src/main/resources/
   ```

3. **Build Project**
   ```bash
   # Clean and compile
   ./mvnw clean compile

   # Run tests
   ./mvnw test

   # Package application
   ./mvnw package
   ```

---

## 🚀 Usage Instructions

### Development Workflow

1. **Implement Checksum Functionality**
   - Open `src/main/java/com/snhu/sslserver/ServerApplication.java`
   - Locate the `//FIXME` comment in the `myHash()` method
   - Replace placeholder implementation with your checksum generation code

2. **Personalize Data String**
   ```java
   // Replace "Joe Smith" with your actual first and last name
   String data = "YourFirstName YourLastName";
   ```

3. **Run Application**
   ```bash
   # Start the SSL server
   ./mvnw spring-boot:run

   # Alternative: Run packaged JAR
   java -jar target/ssl-server-0.0.1-SNAPSHOT.jar
   ```

4. **Access & Test**
   - Navigate to: `https://localhost:8443/hash`
   - Accept SSL certificate warning (self-signed certificate)
   - Verify output displays your name, algorithm, and hash value

5. **Capture Verification Screenshot**
   - Take screenshot of browser showing complete output
   - Include in assignment submission template

---

## 📋 Assignment Deliverables

### Required Submissions

#### 1. Refactored Source Code
- Modified `ServerApplication.java` with implemented checksum functionality
- Working RESTful endpoint generating personalized hash values
- Clean, commented code following secure coding practices

#### 2. Documentation Template
Complete the **Module Five Coding Assignment Checksum Verification Template** with:

- **Algorithm Cipher**: Selected hash algorithm name and specification
- **Justification**: Technical reasoning for algorithm choice, collision resistance explanation
- **Generate Checksum**: Code implementation details and methodology
- **Verification**: Screenshot of working application in web browser

#### 3. Academic Citations
- APA format citations for all sources
- Reference to Oracle Java Security Standard Algorithm Names
- Any additional cryptographic resources consulted

---

## 🔐 Security Considerations

### Hash Algorithm Selection Criteria
- **Collision Resistance**: Algorithm must make it computationally infeasible to find two inputs producing the same hash
- **Current Standards**: Use modern, uncompromised algorithms (avoid MD5, SHA-1)
- **Performance**: Balance security strength with computational efficiency
- **Oracle Support**: Algorithm must be available in Java Security Standard Algorithm Names

### Common Security Anti-Patterns to Avoid

```java
// ❌ DON'T: Use deprecated algorithms
MessageDigest.getInstance("MD5");        // Vulnerable to collisions
MessageDigest.getInstance("SHA-1");      // Deprecated, collision vulnerabilities

// ✅ DO: Use secure, modern algorithms
MessageDigest.getInstance("SHA-256");    // Secure, widely supported
MessageDigest.getInstance("SHA-3-256");  // Latest standard, excellent security
```

### SSL/TLS Configuration
- Server runs exclusively on HTTPS (port 8443)
- PKCS12 keystore provides certificate-based authentication
- Self-signed certificate acceptable for educational purposes
- Production systems require CA-signed certificates

---

## 🧪 Testing & Validation

### Functional Testing Checklist
- [ ] Application starts without errors
- [ ] HTTPS endpoint accessible at `https://localhost:8443/hash`
- [ ] Response includes personalized data string
- [ ] Algorithm name displayed correctly
- [ ] Hash value generated and displayed in hex format
- [ ] Hash value changes when input data changes
- [ ] Hash value remains consistent for identical inputs

### Security Validation
- [ ] Selected algorithm is collision-resistant
- [ ] Implementation uses secure coding practices
- [ ] No sensitive data exposed in logs or error messages
- [ ] SSL certificate properly configured
- [ ] Cryptographic operations handle errors gracefully

---

## 📖 Learning Objectives

### Core Concepts Demonstrated
- **Cryptographic Hash Functions**: Practical implementation and security properties
- **Collision Resistance**: Understanding and preventing hash collision attacks
- **Data Integrity**: Using checksums to verify file authenticity
- **Secure Web Services**: SSL/TLS implementation in Spring Boot applications
- **RESTful API Design**: Creating secure, functional web service endpoints

### Real-World Applications
- Software distribution and verification
- Digital signatures and certificate validation
- Blockchain and cryptocurrency systems
- Password storage and authentication
- File integrity monitoring and forensics

---

## 📚 Resources & References

### Technical Documentation
- [Oracle Java Security Standard Algorithm Names](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html)
- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-configure-ssl)
- [Java MessageDigest Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/MessageDigest.html)

### Security Guidelines
- OWASP Secure Coding Practices
- NIST Cryptographic Standards and Guidelines
- Java Secure Coding Guidelines

### Academic Support
- CS-305 Course Materials and Lectures
- Module Five Learning Resources
- Assignment Guidelines and Rubric

---

## 🏆 Success Criteria

### Grade A Implementation Should Include
- ✅ **Secure Algorithm Selection**: Modern, collision-resistant hash function
- ✅ **Complete Implementation**: Fully functional checksum generation and display
- ✅ **Personalization**: Uses student's actual name in data string
- ✅ **Documentation**: Thorough justification and technical explanation
- ✅ **Security Awareness**: Demonstrates understanding of collision resistance importance
- ✅ **Professional Code**: Clean, commented, secure implementation
- ✅ **Working Demo**: Screenshot proves functional web application

---

## 📞 Support & Troubleshooting

### Common Issues & Solutions

**SSL Certificate Warnings**
- Expected behavior with self-signed certificates
- Click "Advanced" → "Proceed to localhost" in browser
- Certificate warning doesn't affect functionality

**Port 8443 Already in Use**
- Stop any running instances of the application
- Check for other services using port 8443
- Restart your development environment

**Maven Build Failures**
- Verify Java 8+ is installed and configured
- Clear Maven repository: `./mvnw dependency:purge-local-repository`
- Check internet connectivity for dependency downloads

**Hash Generation Errors**
- Verify algorithm name matches Oracle standards exactly
- Check for typos in MessageDigest.getInstance() calls
- Ensure proper exception handling around cryptographic operations
