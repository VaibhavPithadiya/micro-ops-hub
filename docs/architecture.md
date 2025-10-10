# 🏗️ System Architecture — DevOps Microservices Project

This document describes the high-level architecture of the microservices-based system.

```mermaid
graph TD
    %% Users access the system
    A[🧑‍💻 User<br>/ Browser] -->|HTTP| B[🌐 Nginx<br>Gateway]

    %% Kubernetes Cluster
    subgraph Cluster[Kubernetes / Minikube ☸️]
        B -->|/api/users| C[👤 Users Service<br>Spring Boot ☕]
        B -->|/api/orders| D[📦 Orders Service<br>Spring Boot ☕]
        B -->|Static Files| E[🖥️ Frontend<br>HTML + JS]
        C --> F[(🗄️ Postgres<br>DB 🐘)]
        D --> F
    end

    %% CI/CD Pipeline
    subgraph CI_CD[⚙️ CI/CD — Jenkins Pipeline]
        G[💾 Source Code<br>GitHub 🐙] --> H[🤖 Jenkins]
        H --> I[🧪 Build & Test<br>Gradle 🧱]
        I --> J[🐳 Docker Build<br>🔍 Trivy Scan]
        J --> K[📦 Push to<br>Local Registry]
        K --> L[🚀 Deploy<br>Terraform + kubectl]
        L --> Cluster
    end

    %% Infrastructure
    subgraph Infra[🏗️ Infra — Terraform]
        M[🐋 Docker Provider<br>🧰 Local Registry<br>🐘 Postgres]
        N[☸️ Kubernetes Provider<br>🗂️ Namespaces<br>🧩 Deployments<br>🌐 Services<br>🚪 Ingress]
    end

    Infra --> Cluster

    %% Styling for Mermaid v10+ (width, height, colors)
    style Cluster fill:#E3F2FD,stroke:#1565C0,stroke-width:2px,color:#0D47A1
    style CI_CD fill:#FFF3E0,stroke:#FB8C00,stroke-width:2px,color:#E65100
    style Infra fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,color:#1B5E20

```
