#!/bin/sh
source ./init-env.sh

echo "1. Creating cluster with create-cluster.sh"

eksctl create cluster --external-dns-access \
  --name $AGW_EKS_CLUSTER_NAME \
  --region $AGW_AWS_REGION \
  --managed