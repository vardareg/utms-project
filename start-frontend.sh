#!/bin/bash
# UTMS Frontend Starter Script
# Starts React development server

echo "⚛️  Starting UTMS Frontend..."
echo "======================================"

# Check if node_modules exists
if [ ! -d "utms-frontend/node_modules" ]; then
    echo "📦 Installing dependencies..."
    cd utms-frontend
    npm install
    cd ..
fi

# Start frontend
echo "Starting React development server..."
cd utms-frontend
npm run dev
