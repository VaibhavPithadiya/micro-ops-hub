#!/bin/bash
set -e

echo "🚀 Starting Jenkins + Local Registry using Docker Compose..."
docker compose up -d

# Wait for Jenkins to fully start
echo "⏳ Waiting for Jenkins to be ready..."
until curl -s http://localhost:8080/login > /dev/null; do
  sleep 5
  echo "   ... still waiting for Jenkins to start ..."
done
echo "✅ Jenkins is up!"

# Copy plugins.txt into container
echo "📦 Copying plugin list into Jenkins container..."
docker cp plugins.txt jenkins:/usr/share/jenkins/ref/plugins.txt

# Install Docker CLI inside container (if not already)
echo "🐳 Installing Docker CLI inside Jenkins..."
docker exec -u root jenkins bash -c "apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*"

# Install Jenkins plugins
echo "🔧 Installing plugins inside Jenkins..."
docker exec -u root jenkins bash -c "jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt"

echo "✅ Plugins installed successfully!"

# Display initial admin password for convenience
echo "🔑 Initial Admin Password:"
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword || echo "(may already be configured)"

echo "🎉 Jenkins setup complete. Visit http://localhost:8080"
