#!/bin/sh
source ./init-env.sh

echo "2. Installing ACK Controller with install-ack-controller-APIGatewayv2.sh"
#echo "3. Installing ACK Controller with install-ack-controller-APIGatewayv2.sh"



aws ecr-public get-login-password --region us-east-1 | helm registry login --username AWS --password-stdin public.ecr.aws
helm install --create-namespace -n "${ACK_K8S_NAMESPACE}" oci://public.ecr.aws/aws-controllers-k8s/apigatewayv2-chart --version=0.0.17 --generate-name --set=aws.region="${AWS_REGION}"