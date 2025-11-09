#!/bin/bash
set -e

echo "🧪 Running Trivy scans..."

# List of images you want to scan
IMAGES=(
  "localhost:5000/user-service:latest"
  "localhost:5000/order-service:latest"
  "localhost:5000/frontend-service:latest"
)

# Define severity levels that should fail the build
FAIL_ON="HIGH,CRITICAL"

# Run scans for each image
for image in "${IMAGES[@]}"; do
  echo "🔍 Scanning $image ..."

  docker run --rm \
    -v /var/run/docker.sock:/var/run/docker.sock \
    aquasec/trivy:latest image \
    --severity $FAIL_ON \
    --exit-code 1 \
    --no-progress \
    "$image" || {
      echo "❌ Vulnerabilities found in $image"
      exit 1
    }

  echo "✅ $image passed the scan"
done

echo "🎉 All images passed security scan!"
