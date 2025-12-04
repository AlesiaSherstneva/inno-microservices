#!/bin/bash
set -e

echo "=== INNO-MICROSERVICES DEPLOYMENT STARTED ==="

echo "Initializing Minikube Docker environment..."
eval "$(minikube docker-env --shell bash)"

echo "Deploying configurations..."
kubectl apply -f k8s-manifests/config/storage
kubectl apply -f k8s-manifests/config/rbac

echo "Applying configuration secrets..."
kubectl apply -f k8s-manifests/secrets/app-config-secret.yaml

echo "Deploying infrastructure..."
kubectl apply -f k8s-manifests/infrastructure/postgres/user-service/
kubectl apply -f k8s-manifests/infrastructure/postgres/auth-service/
kubectl apply -f k8s-manifests/infrastructure/postgres/order-service/
kubectl apply -f k8s-manifests/infrastructure/redis/
kubectl apply -f k8s-manifests/infrastructure/kafka/

kubectl apply -f k8s-manifests/infrastructure/mongodb/replica-2/
kubectl apply -f k8s-manifests/infrastructure/mongodb/replica-3/
kubectl apply -f k8s-manifests/infrastructure/mongodb/replica-1/

echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod/user-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/auth-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/order-service-postgres-0 --timeout=180s
kubectl wait --for=condition=ready pod/redis-0 --timeout=60s
kubectl wait --for=condition=ready pod/kafka-0 --timeout=300s
kubectl wait --for=condition=ready pod/mongo-1-0 --timeout=180s

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

echo "Deploying OrderService..."
docker build -t order-service:latest ./order-service
kubectl apply -f k8s-manifests/services/order-service/
kubectl wait --for=condition=ready pod -l app=order-service --timeout=180s

echo "Deploying ApiGateway..."
docker build -t api-gateway:latest ./api-gateway
kubectl apply -f k8s-manifests/services/api-gateway/
kubectl wait --for=condition=ready pod -l app=api-gateway --timeout=120s

echo "Deploying PaymentService..."
docker build -t payment-service:latest ./payment-service
kubectl apply -f k8s-manifests/services/payment-service/
kubectl wait --for=condition=ready pod -l app=payment-service --timeout=180s

echo "Deploying Ingress..."
kubectl apply -f k8s-manifests/network/

echo "=== INNO-MICROSERVICES DEPLOYMENT COMPLETED ==="
kubectl get pods