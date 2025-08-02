# CI/CD Pipeline Documentation

## Overview

This project implements a comprehensive CI/CD pipeline using GitHub Actions to ensure code quality, security, and compliance with CS-305 assignment requirements before merging pull requests into the main branch.

## Workflow Structure

### 1. Main CI Pipeline (`.github/workflows/ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### Test Job
- **Matrix Strategy**: Tests on Java 8 and 11
- **Steps**:
  - Checkout code
  - Set up JDK with Temurin distribution
  - Cache Maven dependencies for faster builds
  - Run `mvn clean test`
  - Generate test reports using `dorny/test-reporter`
  - Upload test results as artifacts

#### Security Scan Job
- **Dependencies**: Requires test job to pass
- **Steps**:
  - OWASP Dependency Check for known vulnerabilities
  - Upload security scan results

#### Code Quality Job
- **Dependencies**: Requires test job to pass
- **Steps**:
  - Check for hardcoded secrets/passwords
  - Verify SSL configuration presence
  - Compile and package verification

#### Build Verification Job
- **Dependencies**: Requires all previous jobs to pass
- **Steps**:
  - Full application build
  - JAR file creation verification
  - Upload build artifacts

#### PR Status Check Job
- **Condition**: Only runs on pull requests
- **Dependencies**: Requires all jobs to pass
- **Purpose**: Final confirmation that PR is ready for merge

### 2. Branch Protection (`.github/workflows/branch-protection.yml`)

**Triggers:**
- Pull requests to `main` branch

**Validations:**
- **PR Title Format**: Enforces conventional commit format
  - Examples: `feat: add hash validation`, `fix(tests): resolve null pointer exception`
- **PR Description**: Requires meaningful description
- **Required Checks**: Lists all mandatory CI checks

### 3. Security Validation (`.github/workflows/security-validation.yml`)

**Triggers:**
- Pull requests to `main` branch
- Push to `main` branch

**Security Checks:**

#### Cryptographic Validation
- **Deprecated Algorithm Detection**: Fails if MD5 or SHA-1 found
- **Secure Algorithm Verification**: Confirms SHA-256, SHA-3-256, SHA-512, SHA-3-512 usage
- **SSL Configuration**: Validates HTTPS setup and keystore presence

#### Input Security
- **Validation Patterns**: Checks for input sanitization
- **Error Handling**: Ensures no stack traces or sensitive data exposure
- **Logging Security**: Prevents sensitive data logging

#### CS-305 Compliance
- **Required Files**: Validates presence of assignment components
- **Hash Functionality**: Confirms hash implementation
- **REST Endpoint**: Verifies `/hash` endpoint exists
- **Compliance Report**: Generates detailed compliance documentation

## Branch Protection Rules

To enable automatic enforcement, configure these branch protection rules in GitHub:

### Main Branch Protection
1. Go to **Settings** → **Branches**
2. Add rule for `main` branch
3. Enable:
   - ✅ **Require a pull request before merging**
   - ✅ **Require approvals** (minimum 1)
   - ✅ **Dismiss stale PR approvals when new commits are pushed**
   - ✅ **Require status checks to pass before merging**
   - ✅ **Require branches to be up to date before merging**
   - ✅ **Require conversation resolution before merging**

### Required Status Checks
Add these checks as required:
- `test (8)` - Tests on Java 8
- `test (11)` - Tests on Java 11
- `security-scan` - Security vulnerability scan
- `code-quality` - Code quality verification
- `build-verification` - Build success confirmation
- `cryptographic-validation` - Security algorithm validation
- `compliance-check` - CS-305 requirement validation

## Pull Request Process

### 1. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
# Make your changes
git add .
git commit -m "feat: your feature description"
git push origin feature/your-feature-name
```

### 2. Open Pull Request
- Use the provided PR template
- Follow conventional commit format for title
- Provide detailed description
- Mark relevant checkboxes

### 3. Automated Checks
The CI pipeline will automatically:
- Run all tests on multiple Java versions
- Perform security scans
- Validate code quality
- Check cryptographic algorithm usage
- Verify CS-305 compliance
- Generate reports

### 4. Review Process
- At least one approval required
- All CI checks must pass
- Address any feedback
- Resolve conversations

### 5. Merge
Once approved and all checks pass:
- Squash and merge recommended
- Delete feature branch after merge

## Local Development

### Running Tests Locally
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=HashResultTest

# Run tests with coverage
mvn clean test jacoco:report
```

### Security Validation Locally
```bash
# Check for deprecated algorithms
grep -r "MD5\|SHA-1" src/main/java --include="*.java"

# Verify secure algorithms
grep -r "SHA-256\|SHA-3-256" src/main/java --include="*.java"

# Check SSL configuration
grep -r "server.port=8443" src/main/resources
```

### Build Verification
```bash
# Full build
mvn clean package

# Verify JAR creation
ls -la target/*.jar
```

## Troubleshooting

### Common CI Failures

#### Test Failures
- Check test output in GitHub Actions logs
- Run tests locally: `mvn clean test`
- Fix failing tests before pushing

#### Security Scan Issues
- Review OWASP dependency check report
- Update vulnerable dependencies
- Add exceptions for false positives if necessary

#### Code Quality Issues
- Remove hardcoded secrets/passwords
- Ensure proper input validation
- Fix error handling security issues

#### Compliance Failures
- Verify all required files present
- Ensure hash functionality implemented
- Confirm REST endpoint exists

### Getting Help
- Check GitHub Actions logs for detailed error messages
- Review security validation output
- Consult CS-305 assignment requirements
- Ask for code review feedback

## Benefits

### Quality Assurance
- **Automated Testing**: Ensures all functionality works correctly
- **Multi-Version Support**: Tests on Java 8 and 11
- **Security Validation**: Prevents vulnerable code from merging

### Security Focus
- **Cryptographic Standards**: Enforces secure algorithm usage
- **Input Validation**: Ensures proper sanitization
- **Error Handling**: Prevents information leakage

### Educational Value
- **Best Practices**: Demonstrates professional development workflows
- **Security Awareness**: Teaches secure coding practices
- **Documentation**: Provides clear process guidelines

### Compliance
- **Assignment Requirements**: Validates CS-305 specifications
- **Industry Standards**: Follows professional CI/CD practices
- **Audit Trail**: Maintains detailed logs and reports

This CI/CD pipeline ensures that only high-quality, secure, and compliant code reaches the main branch, supporting both educational objectives and professional development practices.
