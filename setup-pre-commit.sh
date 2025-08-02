#!/bin/bash
#
# Setup script to install pre-commit hooks for code formatting
# Run this script once after cloning the repository
#

echo "Setting up pre-commit hooks for code formatting..."

# Make sure we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Error: This script must be run from the root of a git repository"
    exit 1
fi

# Copy the pre-commit hook
cp .git/hooks/pre-commit.sample .git/hooks/pre-commit 2>/dev/null || true

# Create the pre-commit hook content
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/sh
#
# Pre-commit hook to check code formatting with Spotless
# This hook will prevent commits if code is not properly formatted
#

echo "Running Spotless format check..."

# Run Spotless check
./mvnw spotless:check

# Check if Spotless check passed
if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Code formatting check failed!"
    echo ""
    echo "Please run the following command to fix formatting issues:"
    echo "  ./mvnw spotless:apply"
    echo ""
    echo "Then stage your changes and commit again:"
    echo "  git add ."
    echo "  git commit"
    echo ""
    exit 1
fi

echo "✅ Code formatting check passed!"
exit 0
EOF

# Make the hook executable
chmod +x .git/hooks/pre-commit

echo "✅ Pre-commit hook installed successfully!"
echo ""
echo "Usage:"
echo "  ./mvnw spotless:check   - Check if code is formatted correctly"
echo "  ./mvnw spotless:apply   - Auto-format all code"
echo ""
echo "The pre-commit hook will now automatically check formatting before each commit."