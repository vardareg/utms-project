#!/bin/bash
# UTMS Stop Script
# Stops all UTMS services

echo "🛑 Stopping UTMS Application"
echo "======================================"

# Stop Docker containers
echo "Stopping backend containers..."
docker compose down

echo ""
echo "✅ UTMS stopped successfully!"
echo ""
echo "Note: Data is preserved in Docker volume."
echo "To delete all data, run: docker compose down -v"
