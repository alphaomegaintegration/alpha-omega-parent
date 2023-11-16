#!/bin/sh
source ./init-env.sh

eksctl delete iamserviceaccount \
--cluster=$EKS_CLUSTER_NAME \
--namespace=kube-system \
--name=external-dns

eksctl create iamserviceaccount \
--cluster=$EKS_CLUSTER_NAME \
--namespace=kube-system \
--name=external-dns \
--attach-policy-arn=arn:aws:iam::$AWS_ACCOUNT_ID:policy/AllowExternalDNSUpdates \
--override-existing-serviceaccounts \
--region $AWS_REGION \
--approve