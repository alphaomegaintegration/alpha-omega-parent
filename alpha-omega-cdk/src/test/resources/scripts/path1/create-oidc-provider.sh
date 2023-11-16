#!/bin/sh
source ./init-env.sh


echo "3. Creating oidc provider with create-oidc-provider.sh"

export OIDC_PROVIDER=$(aws eks describe-cluster --name $EKS_CLUSTER_NAME --region $AWS_REGION --query 'cluster.identity.oidc.issuer' --output text | sed -e "s/^https:\/\///")
echo "OIDC_PROVIDER=${OIDC_PROVIDER}"
eksctl utils associate-iam-oidc-provider --cluster $EKS_CLUSTER_NAME --region $AWS_REGION --approve