#!/bin/sh
source ./init-env.sh
# Note the deployment name for ACK service controller from following command
kubectl get deployments -n $ACK_K8S_NAMESPACE
kubectl -n $ACK_K8S_NAMESPACE rollout restart deployment <ACK deployment name>