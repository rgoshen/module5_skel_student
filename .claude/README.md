# Claude Code Documentation Index

> **CS305 Checksum Verification Project** - Comprehensive development documentation for Claude Code

## 📋 Documentation Hierarchy

### 🚀 Project Status & Planning
- **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - **⭐ START HERE** - Current implementation progress and next steps

### 🎯 Quick Reference (Root Level)
- **[CLAUDE.md](../CLAUDE.md)** - Essential commands, URLs, and quick troubleshooting

### 🏗️ Strategic Guidance (Kiro Steering)
- **[.kiro/steering/product.md](../.kiro/steering/product.md)** - Business context, goals, and success metrics
- **[.kiro/steering/structure.md](../.kiro/steering/structure.md)** - High-level architecture philosophy and project organization
- **[.kiro/steering/tech.md](../.kiro/steering/tech.md)** - Technology stack selection and rationale

### 🔧 Implementation Details (Claude Development)
- **[CLAUDE.md](CLAUDE.md)** - Comprehensive development guide with architecture and workflows
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed design patterns, interfaces, and code examples
- **[REQUIREMENTS.md](REQUIREMENTS.md)** - Complete technical specifications and acceptance criteria
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Setup procedures, debugging, and CI/CD workflows
- **[SECURITY.md](SECURITY.md)** - Cryptographic guidelines, validation, and compliance

## 🎯 When to Use Which Documentation

### For Project Status Questions ⭐
**Use PROJECT_STATUS.md** when you need:
- Current implementation progress
- What tasks are completed vs. pending
- Next priority work items
- Requirement mapping and dependencies

```bash
# Examples:
"What still needs to be implemented?"                    # → PROJECT_STATUS.md
"What should I work on next?"                           # → PROJECT_STATUS.md  
"Has the HTML formatting been completed?"               # → PROJECT_STATUS.md
"What are the current priorities?"                      # → PROJECT_STATUS.md
```

### For Strategic Questions
**Use Kiro Steering docs** when you need:
- Business context and project goals
- High-level architectural decisions
- Technology selection rationale
- Project evolution strategy

### For Implementation Questions
**Use Claude development docs** when you need:
- Specific code examples and patterns
- Detailed debugging procedures
- Security implementation guidance
- Step-by-step development workflows

### For Daily Development
**Use root CLAUDE.md** when you need:
- Quick command reference
- Immediate troubleshooting
- Essential URLs and endpoints

## 🔄 Documentation Workflow for Claude Code

### 1. Always Start with Project Status
```bash
1. Check PROJECT_STATUS.md for current progress
2. Identify which tasks are incomplete
3. Reference detailed implementation docs as needed
4. Update PROJECT_STATUS.md when tasks completed
```

### 2. Development Decision Flow
```
Need to work on project → Check PROJECT_STATUS.md → Find priority tasks
    ↓
Need implementation details → Check ARCHITECTURE.md or SECURITY.md
    ↓
Need setup/debugging help → Check DEVELOPMENT.md
    ↓
Need quick command → Check ../CLAUDE.md
```

### 3. Task Completion Workflow
```
Complete task → Update PROJECT_STATUS.md → Mark task as [x]
    ↓
Move to "Completed Tasks" section → Update progress metrics
    ↓
Commit documentation changes → Continue with next priority
```

---

**💡 Tip**: PROJECT_STATUS.md is the "living document" that changes as work progresses. All other docs are more stable references for how to implement things correctly.