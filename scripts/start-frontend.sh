#!/bin/bash
# UTMS Frontend Starter Script
# Starts React development server

echo "⚛️  Starting UTMS Frontend..."
echo "======================================"

# Ensure dependencies are installed
echo "📦 Ensuring dependencies are up-to-date..."
cd utms-frontend
npm install

# Start frontend
echo "🚀 Starting React development server..."
npm run dev
