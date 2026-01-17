#!/bin/bash
# UTMS All-in-One Starter Script
# Starts backend, seeds database, and opens frontend

echo "🚀 Starting UTMS Application"
echo "======================================"
echo ""

# Step 1: Start Backend
echo "Step 1/3: Starting backend..."
./start-backend.sh
if [ $? -ne 0 ]; then
    echo "❌ Failed to start backend. Exiting."
    exit 1
fi

echo ""
echo "======================================"
echo ""

# Step 2: Seed Database
echo "Step 2/3: Seeding database..."
./seed-database.sh
if [ $? -ne 0 ]; then
    echo "⚠️  Warning: Database seeding may have failed."
fi

echo ""
echo "======================================"
echo ""

# Step 3: Start Frontend
echo "Step 3/3: Starting frontend..."
echo ""
echo "🌐 Opening browser at http://localhost:5173"
echo "📝 Press Ctrl+C to stop the frontend"
echo ""

# Open browser (if available)
sleep 3
if command -v xdg-open > /dev/null; then
    xdg-open http://localhost:5173 2>/dev/null &
elif command -v open > /dev/null; then
    open http://localhost:5173 2>/dev/null &
fi

# Start frontend (this will block)
./start-frontend.sh
