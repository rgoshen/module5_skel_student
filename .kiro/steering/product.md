# Product Overview

This is a **CS-305 Module Five Checksum Verification Project** - an SSL-enabled Spring Boot server application that implements a secure checksum verification system using cryptographic hash functions to ensure data integrity and prevent malicious file substitution attacks.

## Business Scenario
A business needs to distribute its public key to clients via website download. Clients must be able to verify the integrity of the downloaded public key using checksum verification to ensure it hasn't been tampered with during transmission.

## Key Features
- **Checksum Generation**: Collision-resistant cryptographic hash algorithms
- **RESTful API**: `/hash` endpoint for checksum verification
- **SSL/TLS Security**: Encrypted communication on port 8443
- **Data Integrity**: Prevents malicious file substitution attacks
- **PKCS12 Integration**: Certificate-based authentication

## Security Objectives
- Deploy collision-resistant cryptographic hash algorithms
- Generate checksums that cannot be easily forged by attackers
- Demonstrate practical application of cryptographic hash functions
- Implement secure coding practices for cryptographic operations

## Learning Goals
- Understanding cryptographic hash functions and collision resistance
- Implementing data integrity verification systems
- Secure web service development with SSL/TLS
- Real-world application of security principles