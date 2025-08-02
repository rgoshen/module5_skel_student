#!/bin/bash

# Setup Claude Code documentation structure
# Run this script from your project root directory

echo "🚀 Setting up Claude Code documentation structure..."

# Create .claude directory
mkdir -p .claude

# Check if we're in the right directory
if [[ ! -f "pom.xml" ]]; then
    echo "❌ Error: Run this script from your project root (where pom.xml is located)"
    exit 1
fi

echo "📁 Created .claude/ directory"

# Create placeholder files (you'll need to add content from the artifacts)
cat > .claude/README.md << 'EOF'
# Claude Code Documentation

This directory contains comprehensive documentation for Claude Code development.

## Files

- **CLAUDE.md** - Main development guide with commands and architecture
- **ARCHITECTURE.md** - Detailed design patterns and component relationships  
- **REQUIREMENTS.md** - Complete business and technical requirements
- **DEVELOPMENT.md** - Setup workflows, debugging, and CI/CD
- **SECURITY.md** - Cryptographic guidelines and security patterns

## Usage

Claude Code will automatically reference these files when working on the project.
Start with CLAUDE.md for the main development guide.
EOF

# Create simplified root CLAUDE.md
cat > CLAUDE.md << 'EOF'
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

For detailed guidance, see `.claude/` directory:

- **[📖 CLAUDE.md](.claude/CLAUDE.md)** - Complete development guide
- **[🏗️ ARCHITECTURE.md](.claude/ARCHITECTURE.md)** - Design patterns and interfaces
- **[📋 REQUIREMENTS.md](.claude/REQUIREMENTS.md)** - Requirements and specifications
- **[⚙️ DEVELOPMENT.md](.claude/DEVELOPMENT.md)** - Workflows and debugging
- **[🔐 SECURITY.md](.claude/SECURITY.md)** - Security guidelines and compliance

---

**CS-305 Checksum Verification Project** - Secure hash generation with Spring Boot
EOF

echo "📝 Created root CLAUDE.md (entry point)"
echo "📝 Created .claude/README.md (directory guide)"

echo ""
echo "✅ Setup complete!"
echo ""
echo "📋 Next steps:"
echo "1. Copy the detailed documentation files to .claude/ directory:"
echo "   - .claude/CLAUDE.md (comprehensive version)"
echo "   - .claude/ARCHITECTURE.md"
echo "   - .claude/REQUIREMENTS.md" 
echo "   - .claude/DEVELOPMENT.md"
echo "   - .claude/SECURITY.md"
echo ""
echo "2. Commit the documentation:"
echo "   git add .claude/ CLAUDE.md"
echo "   git commit -m 'docs: add Claude Code documentation structure'"
echo ""
echo "🎯 Claude Code will now use .claude/ for comprehensive project guidance!"