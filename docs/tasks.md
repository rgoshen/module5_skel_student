# CS305 Checksum Verification System - Implementation Tasks

## Task Overview

This implementation plan breaks down the CS305 checksum verification project into discrete, trackable tasks. Each task includes clear descriptions, expected outcomes, and dependencies.

**Project Timeline**: 3-5 days  
**Estimated Effort**: 8-12 hours total

---

## Phase 1: Project Setup and Environment Configuration

### Task 1.1: Download and Import Project Base
- **Description**: Download the Module Five Coding Assignment Checksum Verification Code Base and set up development environment
- **Priority**: High
- **Estimated Time**: 30 minutes
- **Dependencies**: None

#### Subtasks:
- [ ] Download skeleton project from course materials
- [ ] Import project into Eclipse as Maven project
- [ ] Verify project structure matches expected layout
- [ ] Confirm all required files are present (pom.xml, ServerApplication.java, keystore.p12, application.properties)

**Expected Outcome**: Functional Spring Boot skeleton project ready for development

**Verification Criteria**:
- [ ] Project imports without errors
- [ ] Maven dependencies resolve successfully
- [ ] Application starts and serves HTTPS on port 8443

---

### Task 1.2: Verify Development Environment
- **Description**: Ensure all required tools and dependencies are properly configured
- **Priority**: High
- **Estimated Time**: 20 minutes
- **Dependencies**: Task 1.1

#### Subtasks:
- [ ] Verify Java 8+ installation (`java -version`)
- [ ] Test Maven functionality (`./mvnw --version`)
- [ ] Confirm Eclipse IDE setup and project import
- [ ] Test initial application startup (`./mvnw spring-boot:run`)
- [ ] Verify HTTPS access at `https://localhost:8443`

**Expected Outcome**: Complete development environment ready for coding

**Verification Criteria**:
- [ ] Application compiles without errors
- [ ] HTTPS server starts successfully
- [ ] Browser can access application (with SSL warnings)

---

## Phase 2: Algorithm Research and Selection

### Task 2.1: Research Cryptographic Hash Algorithms
- **Description**: Research available hash algorithms and evaluate security characteristics
- **Priority**: High
- **Estimated Time**: 45 minutes
- **Dependencies**: None

#### Subtasks:
- [ ] Review Oracle Java Security Standard Algorithm Names documentation
- [ ] Research current NIST cryptographic standards and recommendations
- [ ] Compare algorithm security characteristics (collision resistance, performance)
- [ ] Identify deprecated algorithms to avoid (MD5, SHA-1)
- [ ] Document findings for algorithm justification

**Expected Outcome**: Comprehensive understanding of available algorithms and security implications

**Verification Criteria**:
- [ ] Can explain collision resistance concept
- [ ] Understands security differences between algorithms
- [ ] Has clear rationale for algorithm selection

---

### Task 2.2: Select and Justify Hash Algorithm
- **Description**: Choose specific algorithm and document technical justification
- **Priority**: High
- **Estimated Time**: 30 minutes
- **Dependencies**: Task 2.1

#### Subtasks:
- [ ] Select primary algorithm (recommended: SHA-256 or SHA-3-256)
- [ ] Document security characteristics of chosen algorithm
- [ ] Explain why selected algorithm meets collision resistance requirements
- [ ] Justify choice compared to alternatives
- [ ] Prepare justification text for assignment template

**Expected Outcome**: Selected algorithm with complete technical justification

**Verification Criteria**:
- [ ] Algorithm choice is from Oracle standard list
- [ ] Justification explains collision resistance importance
- [ ] Documentation ready for assignment submission

---

## Phase 3: Core Implementation

### Task 3.1: Implement Hash Generation Logic
- **Description**: Modify ServerApplication.java to implement cryptographic hash generation
- **Priority**: High
- **Estimated Time**: 60 minutes
- **Dependencies**: Task 2.2

#### Subtasks:
- [ ] Locate `//FIXME` comment in `myHash()` method
- [ ] Import required classes (`java.security.MessageDigest`, `java.nio.charset.StandardCharsets`)
- [ ] Replace placeholder data string with personalized name ("Rick [LastName]")
- [ ] Implement MessageDigest instance creation with selected algorithm
- [ ] Add hash generation logic using `.digest()` method
- [ ] Implement error handling for `NoSuchAlgorithmException`

**Expected Outcome**: Working hash generation with proper error handling

**Verification Criteria**:
- [ ] Code compiles without errors
- [ ] Hash generation logic is implemented
- [ ] Personal name is included in data string
- [ ] Appropriate exception handling is present

---

### Task 3.2: Implement Byte-to-Hex Conversion
- **Description**: Add utility method to convert hash bytes to hexadecimal string representation
- **Priority**: High
- **Estimated Time**: 30 minutes
- **Dependencies**: Task 3.1

#### Subtasks:
- [ ] Implement `bytesToHex()` utility method
- [ ] Handle proper hexadecimal formatting (leading zeros, lowercase)
- [ ] Integrate hex conversion into main hash generation flow
- [ ] Test conversion with sample data to verify correctness

**Expected Outcome**: Proper hexadecimal representation of hash values

**Verification Criteria**:
- [ ] Hex conversion produces correct format
- [ ] Leading zeros are preserved
- [ ] Output is consistent and repeatable

---

### Task 3.3: Enhance REST Endpoint Response
- **Description**: Modify response format to include all required information display
- **Priority**: High
- **Estimated Time**: 45 minutes
- **Dependencies**: Task 3.2

#### Subtasks:
- [ ] Update `myHash()` method return format
- [ ] Include original data string in response
- [ ] Display selected algorithm name
- [ ] Show generated hash value in readable format
- [ ] Format response as proper HTML for browser display
- [ ] Add appropriate HTML structure and styling

**Expected Outcome**: Complete, informative response displaying all required data

**Verification Criteria**:
- [ ] Response includes original data string
- [ ] Algorithm name is displayed
- [ ] Hash value is shown in hex format
- [ ] HTML formatting is proper and readable

---

## Phase 4: Testing and Validation

### Task 4.1: Functional Testing
- **Description**: Test all functionality to ensure correct operation
- **Priority**: High
- **Estimated Time**: 30 minutes
- **Dependencies**: Task 3.3

#### Subtasks:
- [ ] Test application startup and SSL initialization
- [ ] Verify HTTPS endpoint accessibility
- [ ] Test hash generation with personal name
- [ ] Verify consistent hash output for identical inputs
- [ ] Test hash changes when input data changes
- [ ] Validate HTML response format and content

**Expected Outcome**: Fully functional application with verified behavior

**Verification Criteria**:
- [ ] Application starts without errors
- [ ] Endpoint responds correctly
- [ ] Hash generation is consistent and deterministic
- [ ] All required data is displayed properly

---

### Task 4.2: Security Validation
- **Description**: Verify security aspects of implementation
- **Priority**: High
- **Estimated Time**: 20 minutes
- **Dependencies**: Task 4.1

#### Subtasks:
- [ ] Confirm selected algorithm is not deprecated
- [ ] Verify SSL/TLS encryption is active
- [ ] Test error handling doesn't expose sensitive information
- [ ] Validate proper character encoding (UTF-8)
- [ ] Check for proper exception handling

**Expected Outcome**: Secure implementation following best practices

**Verification Criteria**:
- [ ] Uses secure, non-deprecated algorithm
- [ ] HTTPS encryption is enforced
- [ ] Error messages are appropriate and safe
- [ ] No security vulnerabilities present

---

### Task 4.3: Screenshot Documentation
- **Description**: Capture verification screenshot for assignment submission
- **Priority**: Medium
- **Estimated Time**: 15 minutes
- **Dependencies**: Task 4.2

#### Subtasks:
- [ ] Ensure application is running and accessible
- [ ] Navigate to `https://localhost:8443/hash` in web browser
- [ ] Accept SSL certificate warnings if necessary
- [ ] Verify all required information is displayed
- [ ] Capture high-quality screenshot showing complete output
- [ ] Save screenshot in appropriate format for submission

**Expected Outcome**: Clear screenshot evidence of working application

**Verification Criteria**:
- [ ] Screenshot shows browser URL bar with HTTPS
- [ ] Personal name is visible in output
- [ ] Algorithm name is displayed
- [ ] Hash value is clearly shown
- [ ] Image quality is sufficient for grading

---

## Phase 5: Documentation and Submission

### Task 5.1: Complete Assignment Template
- **Description**: Fill out Module Five Coding Assignment Checksum Verification Template
- **Priority**: High
- **Estimated Time**: 60 minutes
- **Dependencies**: Task 2.2, Task 4.3

#### Subtasks:
- [ ] Open assignment template document
- [ ] Complete "Algorithm Cipher" section with selected algorithm details
- [ ] Write comprehensive "Justification" explaining algorithm choice and collision resistance
- [ ] Document implementation approach in "Generate Checksum" section
- [ ] Insert screenshot in "Verification" section
- [ ] Review all sections for completeness and clarity

**Expected Outcome**: Complete assignment template ready for submission

**Verification Criteria**:
- [ ] All template sections are filled out
- [ ] Technical explanations are clear and accurate
- [ ] Screenshot is properly inserted
- [ ] Document follows assignment requirements

---

### Task 5.2: Code Review and Cleanup
- **Description**: Review and clean up code for submission
- **Priority**: Medium
- **Estimated Time**: 30 minutes
- **Dependencies**: Task 3.3

#### Subtasks:
- [ ] Add comprehensive code comments explaining implementation
- [ ] Ensure proper variable naming and code formatting
- [ ] Remove any debugging code or temporary modifications
- [ ] Verify code follows secure coding practices
- [ ] Test final code compilation and execution

**Expected Outcome**: Clean, well-documented code ready for instructor review

**Verification Criteria**:
- [ ] Code is properly commented
- [ ] Implementation is clean and professional
- [ ] No debugging artifacts remain
- [ ] Code compiles and runs correctly

---

### Task 5.3: Source Citations and References
- **Description**: Prepare APA format citations for all sources used
- **Priority**: Medium
- **Estimated Time**: 20 minutes
- **Dependencies**: Task 2.1

#### Subtasks:
- [ ] Identify all sources used during research and implementation
- [ ] Format Oracle Java Security documentation citation in APA style
- [ ] Include any additional cryptographic resources consulted
- [ ] Add NIST standards or other security references if used
- [ ] Review citation format for accuracy

**Expected Outcome**: Proper APA format citations for all sources

**Verification Criteria**:
- [ ] All sources are properly cited
- [ ] APA format is correct
- [ ] Citations support technical claims made

---

### Task 5.4: Final Submission Preparation
- **Description**: Prepare all deliverables for assignment submission
- **Priority**: High
- **Estimated Time**: 15 minutes
- **Dependencies**: Task 5.1, Task 5.2, Task 5.3

#### Subtasks:
- [ ] Export/package refactored source code
- [ ] Finalize assignment template document
- [ ] Verify screenshot is included and visible
- [ ] Double-check all requirements are met
- [ ] Prepare submission according to course guidelines

**Expected Outcome**: Complete assignment package ready for submission

**Verification Criteria**:
- [ ] All required deliverables are included
- [ ] Source code is properly packaged
- [ ] Documentation is complete and formatted correctly
- [ ] Submission meets all assignment requirements

---

## Quality Assurance Checklist

### Pre-Submission Validation
- [ ] **Functional Requirements**: Application generates and displays checksums correctly
- [ ] **Security Requirements**: Uses secure, collision-resistant algorithm
- [ ] **Technical Requirements**: Implements proper REST endpoint with HTTPS
- [ ] **Academic Requirements**: Includes personal name and proper documentation
- [ ] **Submission Requirements**: All deliverables complete and properly formatted

### Common Issues Prevention
- [ ] **Algorithm Selection**: Avoid deprecated algorithms (MD5, SHA-1)
- [ ] **Name Personalization**: Ensure actual student name is used, not placeholder
- [ ] **SSL Configuration**: Verify HTTPS is working and port 8443 is accessible
- [ ] **Error Handling**: Include proper exception handling for cryptographic operations
- [ ] **Documentation**: Complete all sections of assignment template

### Performance Validation
- [ ] **Startup Time**: Application starts within reasonable time
- [ ] **Response Time**: Hash generation completes quickly
- [ ] **Resource Usage**: No excessive memory or CPU consumption
- [ ] **Reliability**: Consistent behavior across multiple test runs

---

## Risk Mitigation

### Technical Risks
- **Algorithm Unavailability**: Test selected algorithm early to ensure Java support
- **SSL Configuration Issues**: Verify keystore loading and certificate configuration
- **Port Conflicts**: Check port 8443 availability before testing
- **Maven Build Problems**: Ensure clean environment and dependency resolution

### Academic Risks
- **Incomplete Documentation**: Use checklist to verify all template sections
- **Missing Screenshots**: Test screenshot capture process early
- **Citation Errors**: Review APA format requirements and examples
- **Late Submission**: Build in buffer time for unexpected issues

### Implementation Risks
- **Security Vulnerabilities**: Follow secure coding practices checklist
- **Functionality Gaps**: Test all requirements systematically
- **Code Quality Issues**: Review code for clarity and professionalism
- **Integration Problems**: Test end-to-end workflow regularly

---

## Success Metrics

### Technical Success
- ✅ Application compiles and runs without errors
- ✅ HTTPS endpoint accessible and functional
- ✅ Hash generation produces consistent, correct results
- ✅ Selected algorithm meets security requirements

### Academic Success
- ✅ All assignment requirements fulfilled
- ✅ Documentation demonstrates understanding of concepts
- ✅ Implementation shows practical application of security principles
- ✅ Submission is complete and professionally presented

### Learning Success
- ✅ Understanding of cryptographic hash functions demonstrated
- ✅ Knowledge of collision resistance importance shown
- ✅ Practical security implementation skills developed
- ✅ Academic and professional documentation skills practiced