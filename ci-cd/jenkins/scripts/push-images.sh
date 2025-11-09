#!/bin/bash
set -e

echo "📤 Pushing images to local registry..."
docker push localhost:5000/users-service:latest
docker push localhost:5000/orders-service:latest
docker push localhost:5000/frontend:latest

echo "✅ Push complete."
