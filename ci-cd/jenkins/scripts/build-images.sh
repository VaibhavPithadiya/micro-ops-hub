#!/bin/bash
set -e

echo "🔨 Building service images..."
pwd
docker build -t localhost:5000/user-service:latest -f ./services/user-service/Dockerfile ./services
docker build -t localhost:5000/order-service:latest -f ./services/order-service/Dockerfile ./services
docker build -t localhost:5000/frontend-service:latest -f ./services/frontend-service/Dockerfile ./services

echo "✅ Build complete."
