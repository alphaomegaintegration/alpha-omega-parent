
https://aws.amazon.com/blogs/containers/integrate-amazon-api-gateway-with-amazon-eks/

1.setup env
init-env.sh
~
export AGW_AWS_REGION=ap-south-1 <-- Change this to match your region
export AGW_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
export AGW_EKS_CLUSTER_NAME=eks-ack-apigw
~

2. Create a new EKS cluster using eksctl:
create-cluster.sh

3. Deploy AWS load balancer Controller
install-aws-load-balancer-controller.sh
4. Deploy ack controller for api gateway
ack-controller-deploy.sh
5. helm chart install

https://aws-controllers-k8s.github.io/community/docs/tutorials/apigatewayv2-reference-example/

helm-chart-install.sh
   tap@Gregorys-MBP scripts % helm install --create-namespace -n ack-system oci://public.ecr.aws/aws-controllers-k8s/apigatewayv2-chart --version=0.0.17 --generate-name --set=aws.region=us-east-1
   Pulled: public.ecr.aws/aws-controllers-k8s/apigatewayv2-chart:0.0.17
   Digest: sha256:b7d93140e451459053dcb3c063ad71b2f21e49cafd10a79a6b1ab433af9a5bd9
   NAME: apigatewayv2-chart-1701812860
   LAST DEPLOYED: Tue Dec  5 16:47:45 2023
   NAMESPACE: ack-system
   STATUS: deployed
   REVISION: 1
   TEST SUITE: None
   NOTES:
   apigatewayv2-chart has been installed.
   This chart deploys "public.ecr.aws/aws-controllers-k8s/apigatewayv2-controller:0.0.17".

Check its status by running:
kubectl --namespace ack-system get pods -l "app.kubernetes.io/instance=apigatewayv2-chart-1701812860"

You are now able to create Amazon API Gateway (APIGWv2) resources!

The controller is running in "cluster" mode.
The controller is configured to manage AWS resources in region: "us-east-1"

Visit https://aws-controllers-k8s.github.io/community/reference/ for an API
reference of all the resources that can be created using this controller.

For more information on the AWS Controllers for Kubernetes (ACK) project, visit:
https://aws-controllers-k8s.github.io/community/

https://aws-controllers-k8s.github.io/community/docs/user-docs/irsa/
https://aws-controllers-k8s.github.io/community/docs/user-docs/irsa/#step-1-create-an-oidc-identity-provider-for-your-cluster