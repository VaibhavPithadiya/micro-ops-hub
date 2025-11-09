#!/bin/bash
set -e

echo "🧪 Running Trivy scans..."
for image in users-service orders-service frontend; do
  trivy image --severity HIGH,CRITICAL localhost:5000/$image:latest || true
done

echo "✅ Security scan done."
