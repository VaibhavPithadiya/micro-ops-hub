#!/bin/bash
set -e

echo "🔨 Building service images..."
docker build -t localhost:5000/users-service:latest ./services/users-service
docker build -t localhost:5000/orders-service:latest ./services/orders-service
docker build -t localhost:5000/frontend:latest ./services/frontend

echo "✅ Build complete."
