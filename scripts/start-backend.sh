#!/bin/bash
# UTMS Backend Starter Script
# Starts PostgreSQL and Spring Boot backend in Docker

echo "🐳 Starting UTMS Backend (Docker)..."
echo "======================================"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi

# Start Docker containers
echo "Starting PostgreSQL and Spring Boot backend..."
docker compose up -d

# Wait for containers to be healthy
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 8

# Check container status using grep (simpler than jq)
if docker compose ps | grep -q "utms-backend.*Up"; then
    echo "✅ Backend is running!"
    echo ""
    echo "📊 Container Status:"
    docker compose ps
    echo ""
    echo "🌐 Backend API: http://localhost:8080"
    echo "📝 View logs: docker compose logs backend -f"
    echo "🛑 Stop: docker compose down"
else
    echo "❌ Backend failed to start. Checking logs..."
    echo ""
    docker compose logs backend --tail=30
    exit 1
fi
