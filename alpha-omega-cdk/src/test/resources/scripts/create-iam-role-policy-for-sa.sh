#!/bin/sh
# Update the service name variables as needed
export SERVICE="apigatewayv2"
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)
export OIDC_PROVIDER=$(aws eks describe-cluster --name $EKS_CLUSTER_NAME --region $AWS_REGION --query "cluster.identity.oidc.issuer" --output text | sed -e "s/^https:\/\///")
export ACK_K8S_NAMESPACE=ack-system

export ACK_K8S_SERVICE_ACCOUNT_NAME=ack-$SERVICE-controller

echo "ACK_K8S_NAMESPACE=${ACK_K8S_NAMESPACE}"
echo "ACK_K8S_SERVICE_ACCOUNT_NAME=${ACK_K8S_SERVICE_ACCOUNT_NAME}"

read -r -d '' TRUST_RELATIONSHIP <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::${AWS_ACCOUNT_ID}:oidc-provider/${OIDC_PROVIDER}"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "${OIDC_PROVIDER}:sub": "system:serviceaccount:${ACK_K8S_NAMESPACE}:${ACK_K8S_SERVICE_ACCOUNT_NAME}"
        }
      }
    }
  ]
}
EOF
echo "${TRUST_RELATIONSHIP}" > trust.json

ACK_CONTROLLER_IAM_ROLE="ack-${SERVICE}-controller"
ACK_CONTROLLER_IAM_ROLE_DESCRIPTION="IRSA role for ACK ${SERVICE} controller deployment on EKS cluster using Helm charts"
aws iam create-role --role-name "${ACK_CONTROLLER_IAM_ROLE}" --assume-role-policy-document file://trust.json --description "${ACK_CONTROLLER_IAM_ROLE_DESCRIPTION}"
ACK_CONTROLLER_IAM_ROLE_ARN=$(aws iam get-role --role-name=$ACK_CONTROLLER_IAM_ROLE --query Role.Arn --output text)


export IRSA_ROLE_ARN=eks.amazonaws.com/role-arn=$ACK_CONTROLLER_IAM_ROLE_ARN


echo "IRSA_ROLE_ARN=${IRSA_ROLE_ARN}"
echo "ACK_K8S_SERVICE_ACCOUNT_NAME=${ACK_K8S_SERVICE_ACCOUNT_NAME}"
echo "ACK_K8S_NAMESPACE=${ACK_K8S_NAMESPACE}"

kubectl annotate serviceaccount -n $ACK_K8S_NAMESPACE $ACK_K8S_SERVICE_ACCOUNT_NAME $IRSA_ROLE_ARN

kubectl get deployments -n $ACK_K8S_NAMESPACE