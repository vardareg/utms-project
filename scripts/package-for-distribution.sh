#!/bin/bash
# UTMS Distribution Packaging Script
# Creates a clean, distributable archive of the UTMS project

set -e

echo "📦 UTMS Distribution Packager"
echo "=============================="
echo ""

# Get project root directory
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT_NAME="$(basename "$PROJECT_ROOT")"
PARENT_DIR="$(dirname "$PROJECT_ROOT")"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
OUTPUT_NAME="utms-project-v1.0.tar.gz"
OUTPUT_PATH="$(pwd)/$OUTPUT_NAME"

echo "📁 Project: $PROJECT_NAME"
echo "📍 Location: $PROJECT_ROOT"
echo ""

# Check if we're in the right directory
if [ ! -f "$PROJECT_ROOT/docker-compose.yml" ]; then
    echo "❌ Error: Not in UTMS project root directory"
    echo "   Could not find docker-compose.yml"
    exit 1
fi

echo "🧹 Creating clean package (excluding build artifacts)..."
echo ""

# Create the archive with exclusions (using -C to change directory)
tar -czf "$OUTPUT_PATH" \
    -C "$PARENT_DIR" \
    --exclude="$PROJECT_NAME/.git" \
    --exclude="$PROJECT_NAME/.gitignore" \
    --exclude="$PROJECT_NAME/.vscode" \
    --exclude="$PROJECT_NAME/.idea" \
    --exclude="$PROJECT_NAME/.DS_Store" \
    --exclude="$PROJECT_NAME/utms-frontend/node_modules" \
    --exclude="$PROJECT_NAME/utms-frontend/dist" \
    --exclude="$PROJECT_NAME/utms-frontend/.vite" \
    --exclude="$PROJECT_NAME/utms-backend/target" \
    --exclude="$PROJECT_NAME/utms-backend/.mvn" \
    --exclude="$PROJECT_NAME/utms-backend/mvnw" \
    --exclude="$PROJECT_NAME/utms-backend/mvnw.cmd" \
    --exclude="$OUTPUT_NAME" \
    --exclude="$PROJECT_NAME/**/*.tar.gz" \
    --exclude="$PROJECT_NAME/**/*.log" \
    --exclude="$PROJECT_NAME/**/.gradle" \
    "$PROJECT_NAME"

# Get archive size
ARCHIVE_SIZE=$(du -h "$OUTPUT_PATH" | cut -f1)

echo "✅ Package created successfully!"
echo ""
echo "📦 Output:  $OUTPUT_NAME"
echo "📊 Size:    $ARCHIVE_SIZE"
echo "📍 Location: $OUTPUT_PATH"
echo ""

# List what's included
echo "📋 Package Contents:"
tar -tzf "$OUTPUT_NAME" | head -20
echo "   ... (and more)"
echo ""

echo "✨ Distribution package ready!"
echo ""
echo "📤 Next Steps:"
echo "   1. Share this file: $OUTPUT_NAME"
echo "   2. Recipient should read: DISTRIBUTION_GUIDE.md"
echo "   3. They run: bash scripts/start-utms.sh"
echo ""
echo "💡 Quick test extraction:"
echo "   tar -xzf $OUTPUT_NAME"
echo ""
