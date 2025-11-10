#!/bin/bash
set -e

echo "📤 Pushing images to local registry..."
docker push localhost:5000/user-service:latest
docker push localhost:5000/order-service:latest
docker push localhost:5000/frontend-service:latest

echo "✅ Push complete."
