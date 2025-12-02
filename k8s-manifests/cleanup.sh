#!/bin/bash
set -e

echo "=== INNO-MICROSERVICES CLEANUP STARTED ==="

echo "Deleting services..."
kubectl delete -f k8s-manifests/services/config-server --ignore-not-found
kubectl delete -f k8s-manifests/services/user-service --ignore-not-found
kubectl delete -f k8s-manifests/services/auth-service --ignore-not-found

echo "Deleting infrastructure..."
kubectl delete statefulset -l app=user-service-postgres --ignore-not-found
kubectl delete pvc -l app=user-service-postgres --ignore-not-found

kubectl delete statefulset -l app=auth-service-postgres --ignore-not-found
kubectl delete pvc -l app=auth-service-postgres --ignore-not-found

kubectl delete statefulset -l app=order-service-postgres --ignore-not-found
kubectl delete pvc -l app=order-service-postgres --ignore-not-found

kubectl delete statefulset -l app=redis --ignore-not-found
kubectl delete pvc -l app=redis --ignore-not-found

kubectl delete statefulset -l app=kafka --ignore-not-found
kubectl delete pvc -l app=kafka --ignore-not-found

echo "Deleting secrets..."
kubectl delete -f k8s-manifests/secrets/ --ignore-not-found

echo "=== INNO-MICROSERVICES CLEANUP COMPLETED ==="