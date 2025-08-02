#!/bin/bash

# Simple task status updater for PROJECT_STATUS.md
# Usage: ./update-task.sh 7 "completed" "HTML response formatting implemented"

TASK_NUMBER=$1
STATUS=$2
NOTE=$3
PROJECT_STATUS_FILE=".claude/PROJECT_STATUS.md"

if [[ ! -f "$PROJECT_STATUS_FILE" ]]; then
    echo "❌ Error: PROJECT_STATUS.md not found at $PROJECT_STATUS_FILE"
    exit 1
fi

if [[ -z "$TASK_NUMBER" ]] || [[ -z "$STATUS" ]]; then
    echo "Usage: $0 <task_number> <status> [note]"
    echo "  task_number: Number of task to update (e.g., 7)"
    echo "  status: 'completed', 'in-progress', or 'blocked'"
    echo "  note: Optional note about the status change"
    echo ""
    echo "Examples:"
    echo "  $0 7 completed 'HTML formatting implemented'"
    echo "  $0 8 in-progress 'Working on error handling'"
    echo "  $0 9 blocked 'Waiting for SSL certificate'"
    exit 1
fi

# Create backup
cp "$PROJECT_STATUS_FILE" "$PROJECT_STATUS_FILE.backup"

case $STATUS in
    "completed")
        echo "✅ Marking task $TASK_NUMBER as completed"
        # Mark task as completed with [x]
        sed -i.tmp "s/- \[ \] \*\*$TASK_NUMBER\./- [x] **$TASK_NUMBER./" "$PROJECT_STATUS_FILE"
        ;;
    "in-progress")
        echo "🔄 Marking task $TASK_NUMBER as in-progress"
        # Add in-progress indicator
        sed -i.tmp "s/- \[ \] \*\*$TASK_NUMBER\./- [ ] **$TASK_NUMBER. 🔄/" "$PROJECT_STATUS_FILE"
        ;;
    "blocked")
        echo "⚠️ Marking task $TASK_NUMBER as blocked"
        # Add blocked indicator
        sed -i.tmp "s/- \[ \] \*\*$TASK_NUMBER\./- [ ] **$TASK_NUMBER. ⚠️ BLOCKED/" "$PROJECT_STATUS_FILE"
        ;;
    *)
        echo "❌ Invalid status: $STATUS"
        echo "Valid statuses: completed, in-progress, blocked"
        exit 1
        ;;
esac

# Clean up temp file
rm -f "$PROJECT_STATUS_FILE.tmp"

# Add note if provided
if [[ -n "$NOTE" ]]; then
    echo "📝 Adding note: $NOTE"
    # You could add logic here to append notes to tasks
fi

# Update last modified date
sed -i.tmp "s/\*\*Last Updated\*\*: \[.*\]/Last Updated**: $(date '+%Y-%m-%d %H:%M')/" "$PROJECT_STATUS_FILE"
rm -f "$PROJECT_STATUS_FILE.tmp"

echo "✅ Updated PROJECT_STATUS.md"
echo "📝 Backup saved as PROJECT_STATUS.md.backup"

# Optionally commit the change
read -p "🤔 Commit this change to git? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    git add "$PROJECT_STATUS_FILE"
    git commit -m "docs: update task $TASK_NUMBER status to $STATUS"
    echo "✅ Changes committed to git"
fi