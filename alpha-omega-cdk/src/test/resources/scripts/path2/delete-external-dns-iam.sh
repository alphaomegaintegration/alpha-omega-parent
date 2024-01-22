#!/bin/sh
source ./init-env.sh


eksctl delete iamserviceaccount \
--cluster=$EKS_CLUSTER_NAME \
--namespace=kube-system \
--name=external-dns

aws iam delete-policy --policy-arn arn:aws:iam::$AWS_ACCOUNT_ID:policy/AWSExternalDNSIAMPolicy