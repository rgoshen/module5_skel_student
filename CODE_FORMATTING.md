# Code Formatting Guide

This project uses [Spotless](https://github.com/diffplug/spotless) to maintain consistent code formatting across all Java files.

## Setup

### First Time Setup
Run the setup script to install the pre-commit hook:
```bash
./setup-pre-commit.sh
```

## Usage

### Check Code Formatting
To check if your code is properly formatted:
```bash
./mvnw spotless:check
```

### Auto-Format Code
To automatically format all code:
```bash
./mvnw spotless:apply
```

### Build Integration
Formatting checks are automatically run during the Maven `validate` phase, so they'll be checked when you run:
```bash
./mvnw compile
./mvnw test
./mvnw package
```

## Pre-commit Hook

The pre-commit hook will automatically run `spotless:check` before each commit. If formatting issues are found:

1. The commit will be blocked
2. You'll see instructions to fix the formatting
3. Run `./mvnw spotless:apply` to fix issues
4. Stage your changes: `git add .`
5. Commit again: `git commit`

## Formatting Rules

The project uses Google Java Format with the following additional rules:

- **Import Organization**: Imports are organized in the order: `java`, `javax`, `org`, `com`
- **Unused Imports**: Automatically removed
- **Trailing Whitespace**: Automatically trimmed
- **File Endings**: All files end with a newline
- **XML Files**: pom.xml and other XML files are also formatted
- **Markdown Files**: README.md and other markdown files are formatted

## IDE Integration

### IntelliJ IDEA
1. Install the "google-java-format" plugin
2. Enable it in Settings → google-java-format Settings
3. Set the code style to "Google Style"

### Eclipse
1. Download the [google-java-format Eclipse plugin](https://github.com/google/google-java-format#eclipse)
2. Install and configure it to use Google Style

### VS Code
1. Install the "Language Support for Java" extension
2. Configure it to use Google Java Format

## Bypassing the Hook (Not Recommended)

If you absolutely need to bypass the pre-commit hook:
```bash
git commit --no-verify
```

**Note**: This is not recommended as it can lead to inconsistent formatting in the codebase.

## Troubleshooting

### Hook Not Running
If the pre-commit hook isn't running, make sure it's executable:
```bash
chmod +x .git/hooks/pre-commit
```

### Formatting Conflicts
If you have merge conflicts in formatted files:
1. Resolve the conflicts manually
2. Run `./mvnw spotless:apply`
3. Stage and commit the resolved files

### Plugin Version Updates
To update Spotless to the latest version, modify the version in `pom.xml`:
```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>2.43.0</version> <!-- Update this version -->
</plugin>
```
