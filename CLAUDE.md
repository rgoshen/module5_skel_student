# CLAUDE.md

> **Claude Code Documentation Hub** - CS305 Checksum Verification Project

## 🚀 Quick Start

```bash
# Start development server
./mvnw spring-boot:run

# Run tests
./mvnw test

# Format code
./mvnw spotless:apply

# Access application
https://localhost:8443/api/v1/hash
```

## 📚 Complete Documentation

For detailed guidance, see the comprehensive documentation in `.claude/`:

- **[📖 CLAUDE.md](.claude/CLAUDE.md)** - Complete development guide with commands, architecture, and troubleshooting
- **[🏗️ ARCHITECTURE.md](.claude/ARCHITECTURE.md)** - Design patterns, interfaces, and component relationships  
- **[📋 REQUIREMENTS.md](.claude/REQUIREMENTS.md)** - Business requirements, security specs, and acceptance criteria
- **[⚙️ DEVELOPMENT.md](.claude/DEVELOPMENT.md)** - Setup, workflows, debugging, and CI/CD integration
- **[🔐 SECURITY.md](.claude/SECURITY.md)** - Cryptographic guidelines, validation patterns, and compliance

## 🎯 Project Context

**CS-305 Module Five Checksum Verification Project** - Secure Spring Boot web application implementing cryptographic hash generation for file integrity verification.

### Key Components
- **RESTful API**: `/api/v1/hash` endpoint with content negotiation
- **Security**: HTTPS-only, secure algorithms (SHA-256, SHA-3-256, SHA-512, SHA-3-512)
- **Architecture**: Clean Architecture with Strategy pattern for algorithms
- **Student Integration**: Personalized hash input with "Rick Goshen" name

### Security Requirements
- ✅ Collision-resistant algorithms only
- ❌ Deprecated algorithms rejected (MD5, SHA-1)
- 🔒 Input validation and sanitization
- 🛡️ Secure error handling without information leakage

---

**For comprehensive development guidance, start with [.claude/CLAUDE.md](.claude/CLAUDE.md)**