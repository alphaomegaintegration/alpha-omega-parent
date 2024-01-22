kubectl delete stages.apigatewayv2.services.k8s.aws apiv1
kubectl delete apis.apigatewayv2.services.k8s.aws apitest-private-nlb
kubectl delete vpclinks.apigatewayv2.services.k8s.aws nlb-internal
kubectl delete service echoserver
kubectl delete services authorservice
sleep 10
aws ec2 delete-security-group --group-id $AGW_VPCLINK_SG --region $AGW_AWS_REGION
helm delete aws-load-balancer-controller --namespace kube-system
helm delete ack-apigatewayv2-controller --namespace kube-system
for role in $(aws iam list-roles --query "Roles[?contains(RoleName, \
  'eksctl-eks-ack-apigw-addon-iamserviceaccount')].RoleName" \
  --output text)
do (aws iam detach-role-policy --role-name $role --policy-arn $(aws iam list-attached-role-policies --role-name $role --query 'AttachedPolicies[0].PolicyArn' --output text))
done
for role in $(aws iam list-roles --query "Roles[?contains(RoleName, \
  'eksctl-eks-ack-apigw-addon-iamserviceaccount')].RoleName" \
  --output text)
do (aws iam delete-role --role-name $role)
done