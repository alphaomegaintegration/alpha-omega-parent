export EKS_CLUSTER_NAME=$AGW_EKS_CLUSTER_NAME
export AWS_REGION=$AGW_AWS_REGION
eksctl utils associate-iam-oidc-provider --cluster $EKS_CLUSTER_NAME --region $AWS_REGION --approve