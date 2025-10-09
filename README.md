# 🚀 DevOps Microservices Project

A fully open-source, end-to-end DevOps showcase built around **Java (Spring Boot, Gradle)** microservices, **Postgres**, and **Nginx** — automated via **Jenkins**, **Terraform**, and **Kubernetes (Minikube)**.

---

## 🧩 Project Overview

| Layer | Tools & Tech | Purpose |
|-------|---------------|---------|
| Application | Java (Spring Boot + Gradle), HTML/JS | Users + Orders microservices + Frontend |
| Gateway | Nginx | Reverse proxy / static content |
| Database | Postgres | Shared datastore |
| CI/CD | Jenkins | Build → Test → Scan → Deploy |
| IaC | Terraform | Infra & Kubernetes resource management |
| Runtime | Docker + Minikube | Local cluster |
| Security | Trivy, Checkov, Gitleaks | DevSecOps checks |
| Monitoring | Prometheus, Grafana | Metrics & dashboards |

---

## 📂 Repository Structure

services/ # All microservices
infrastructure/ # Terraform + scripts
k8s/ # Kubernetes manifests & Helm charts
ci-cd/jenkins/ # Jenkins pipelines & config
monitoring/ # Prometheus & Grafana setup
security/ # DevSecOps tools
docs/ # Documentation & diagrams
