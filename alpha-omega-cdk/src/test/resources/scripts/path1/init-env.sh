#!/bin/sh
export AGW_AWS_REGION='us-east-1'
export AGW_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
export AGW_EKS_CLUSTER_NAME='eks-ack-apigw-rapid'
export EKS_CLUSTER_NAME=$AGW_EKS_CLUSTER_NAME
export AWS_REGION=$AGW_AWS_REGION
export ACK_K8S_NAMESPACE='ack-system'
export SERVICE='apigatewayv2'
export AWS_ACCOUNT_ID=$AGW_ACCOUNT_ID

export ACK_K8S_SERVICE_ACCOUNT_NAME=ack-$SERVICE-controller


echo "ACK_K8S_NAMESPACE=${ACK_K8S_NAMESPACE}"
echo "ACK_K8S_SERVICE_ACCOUNT_NAME=${ACK_K8S_SERVICE_ACCOUNT_NAME}"
echo "AWS_ACCOUNT_ID=${AWS_ACCOUNT_ID}"

echo "SERVICE=${SERVICE}"
echo "EKS_CLUSTER_NAME=${EKS_CLUSTER_NAME}"
echo "AGW_AWS_REGION=${AGW_AWS_REGION}"
echo "AGW_ACCOUNT_ID=${AGW_ACCOUNT_ID}"