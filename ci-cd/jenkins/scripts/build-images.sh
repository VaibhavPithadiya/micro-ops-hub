#!/bin/bash
set -e

echo "🔨 Building service images..."
pwd
docker build -t localhost:5000/users-service:latest ./services
docker build -t localhost:5000/orders-service:latest ./services
docker build -t localhost:5000/frontend:latest ./services

echo "✅ Build complete."
