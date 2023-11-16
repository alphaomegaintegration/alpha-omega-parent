#!/bin/sh
source ./init-env.sh

export AGW_VPC_ID=$(aws eks describe-cluster \
  --name $AGW_EKS_CLUSTER_NAME \
  --region $AGW_AWS_REGION  \
  --query "cluster.resourcesVpcConfig.vpcId" \
  --output text)

echo "AGW_VPC_ID=${AGW_VPC_ID}"

export AGW_VPCLINK_SG=$(aws ec2 create-security-group \
  --description "SG for VPC Link" \
  --group-name SG_VPC_LINK \
  --vpc-id $AGW_VPC_ID \
  --region $AGW_AWS_REGION \
  --output text \
  --query 'GroupId')

