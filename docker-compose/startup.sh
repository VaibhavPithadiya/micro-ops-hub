#!/bin/bash
set -e  # stop on first error

# 1️⃣ Start local Docker registry (so you can push to localhost:5000)
echo "Starting local registry..."
docker compose up -d registry

# Wait a few seconds to ensure registry is ready
sleep 3

# 2️⃣ Build service images
echo "Building service images..."
docker compose build

# 3️⃣ Tag images (if not already using localhost:5000)
echo "Tagging images for local registry..."
docker tag user-service:latest localhost:5000/user-service:1.0 || true
docker tag order-service:latest localhost:5000/order-service:1.0 || true
docker tag frontend-service:latest localhost:5000/frontend-service:1.0 || true

# 4️⃣ Push images to local registry
echo "Pushing images to local registry..."
docker push localhost:5000/user-service:1.0
docker push localhost:5000/order-service:1.0
docker push localhost:5000/frontend-service:1.0

# 5️⃣ Start all containers (including dependencies)
echo "Starting full stack..."
docker compose up -d

echo "✅ All services are up and running!"
