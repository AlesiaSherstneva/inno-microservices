#!/bin/bash
set -e

echo "=== INNO-MICROSERVICES DEPLOYMENT STARTED ==="

echo "Initializing Minikube Docker environment..."
eval "$(minikube docker-env --shell bash)"

echo "Applying configuration secrets..."
kubectl apply -f k8s-manifests/secrets/app-config-secret.yaml

echo "Deploying infrastructure..."
kubectl apply -f k8s-manifests/infrastructure/postgres/user-service/
kubectl apply -f k8s-manifests/infrastructure/postgres/auth-service/
kubectl apply -f k8s-manifests/infrastructure/postgres/order-service/
kubectl apply -f k8s-manifests/infrastructure/redis/
kubectl apply -f k8s-manifests/infrastructure/kafka/

echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod/user-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/auth-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/order-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/redis-0 --timeout=60s
kubectl wait --for=condition=ready pod/kafka-0 --timeout=300s

echo "Deploying ConfigServer..."
docker build -t config-server:latest ./config-server
kubectl apply -f k8s-manifests/services/config-server/
kubectl wait --for=condition=ready pod -l app=config-server --timeout=120s

echo "Deploying UserService..."
docker build -t user-service:latest ./user-service
kubectl apply -f k8s-manifests/services/user-service/
kubectl wait --for=condition=ready pod -l app=user-service --timeout=180s

echo "Deploying AuthService..."
docker build -t auth-service:latest ./auth-service
kubectl apply -f k8s-manifests/services/auth-service/
kubectl wait --for=condition=ready pod -l app=auth-service --timeout=180s

echo "=== INNO-MICROSERVICES DEPLOYMENT COMPLETED ==="
kubectl get pods