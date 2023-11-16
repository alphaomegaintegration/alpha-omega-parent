export HELM_EXPERIMENTAL_OCI=1
export SERVICE=apigatewayv2
export RELEASE_VERSION=v0.0.2
export CHART_REPO=oci://public.ecr.aws/aws-controllers-k8s
export CHART_REF="$CHART_REPO/$SERVICE-chart --version $RELEASE_VERSION"

#public.ecr.aws/aws-controllers-k8s/{SERVICE}-chart

helm pull $CHART_REF

# Install ACK

# Please note that the ECR Public Endpoints are available in "us-east-1"
# and "us-west-2". The region value in this command does not need to be
# updated to the region where you are planning to deploy the resources.
# https://docs.aws.amazon.com/general/latest/gr/ecr-public.html#ecr-public-region
aws ecr-public get-login-password --region us-east-1 | helm registry login --username AWS --password-stdin public.ecr.aws

# The region value in this command should reflect the region where you
# are planning to deploy the resources.
helm install \
  --namespace kube-system \
  ack-$SERVICE-controller \
  $CHART_REPO/$SERVICE-chart \
  --version=$RELEASE_VERSION \
  --set=aws.region=$AGW_AWS_REGION