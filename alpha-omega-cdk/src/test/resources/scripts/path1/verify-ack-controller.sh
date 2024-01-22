#!/bin/sh
source ./init-env.sh

# https://aws-controllers-k8s.github.io/community/docs/user-docs/irsa/#step-1-create-an-oidc-identity-provider-for-your-cluster
kubectl get pods -n $ACK_K8S_NAMESPACE -o=wide
#kubectl describe pod -n $ACK_K8S_NAMESPACE <NAME> | grep "^\s*AWS_"