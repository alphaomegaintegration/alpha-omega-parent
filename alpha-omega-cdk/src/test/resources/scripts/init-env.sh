
export AGW_AWS_REGION=us-east-1
export AGW_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
export AGW_EKS_CLUSTER_NAME=eks-ack-apigw-rapid