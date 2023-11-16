#!/bin/sh
# Annotate the service account with the ARN
export IRSA_ROLE_ARN=eks.amazonaws.com/role-arn=$ACK_CONTROLLER_IAM_ROLE_ARN


echo "IRSA_ROLE_ARN=${IRSA_ROLE_ARN}"
echo "ACK_K8S_SERVICE_ACCOUNT_NAME=${ACK_K8S_SERVICE_ACCOUNT_NAME}"
echo "ACK_K8S_NAMESPACE=${ACK_K8S_NAMESPACE}"

kubectl annotate serviceaccount -n $ACK_K8S_NAMESPACE $ACK_K8S_SERVICE_ACCOUNT_NAME $IRSA_ROLE_ARN