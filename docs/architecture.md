# 🏗️ System Architecture — DevOps Microservices Project

This document describes the high-level architecture of the microservices-based system.

```mermaid
graph TD
    A[🧑‍💻 User<br>/ Browser] -->|HTTP| B[🌐 Nginx<br>Gateway]

    subgraph Cluster[Kubernetes / Minikube ☸️]
        B -->|/api/users| C[👤 Users Service<br>Spring Boot ☕]
        B -->|/api/orders| D[📦 Orders Service<br>Spring Boot ☕]
        B -->|Static Files| E[🖥️ Frontend<br>HTML + JS]
        C --> F[(🗄️ Postgres<br>DB 🐘)]
        D --> F
    end

    subgraph CI_CD[⚙️ CI/CD — Jenkins Pipeline]
        G[💾 Source Code<br>GitHub 🐙] --> H[🤖 Jenkins]
        H --> I[🧪 Build & Test<br>Gradle 🧱]
        I --> J[🐳 Docker Build<br>🔍 Trivy Scan]
        J --> K[📦 Push to<br>Local Registry]
        K --> L[🚀 Deploy<br>Terraform + kubectl]
        L --> Cluster
    end

    subgraph Infra[🏗️ Infrastructure — Terraform (IaC)]
        M[🐋 Docker Provider<br>🧰 Local Registry<br>🐘 Postgres]
        N[☸️ Kubernetes Provider<br>🗂️ Namespaces<br>🧩 Deployments<br>🌐 Services<br>🚪 Ingress]
    end

    Infra --> Cluster
```
