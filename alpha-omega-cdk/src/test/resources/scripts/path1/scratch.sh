

 aws ec2 describe-security-groups --query "SecurityGroups[*].VpcId==${AGW_VPC_ID}"

  export OPEN_API_YAML=$(cat open-api.yaml)

  aws apigatewayv2 import-api --body file://open-api.yaml

  aws apigatewayv2 create-vpc-link --name gtway-vpcLink \
      --subnet-ids subnet-0c3e0669a29b87c42 subnet-0197d10ca32ab58de subnet-0ba3db5e36525b84d subnet-02650cbfe620d8d35 \
      --security-group-ids sg-05a4e0aed22a1f772


  aws apigatewayv2 delete-vpc-link --vpc-link-id 5zs702

  aws apigatewayv2 delete-vpc-link --vpc-link-id 2tqg3r

  aws apigatewayv2 create-integration --api-id api-id --integration-type HTTP_PROXY \
      --integration-method GET --connection-type VPC_LINK \
      --connection-id VPC-link-ID \
      --integration-uri arn:aws:elasticloadbalancing:us-east-2:123456789012:listener/app/my-load-balancer/50dc6c495c0c9188/0467ef3c8400ae65
      --payload-format-version 1.0