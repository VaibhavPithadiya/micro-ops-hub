#!/bin/bash
set -e

echo "🚀 Deploying with docker-compose..."
docker-compose -f docker-compose.yml up -d

echo "✅ Deployment successful."
