#!/bin/sh
source ./init-env.sh


echo "4. Creating IAM role policy with create-iam-role-policy-for-sa-external-dns.sh"



aws iam delete-policy --policy-arn arn:aws:iam::$AWS_ACCOUNT_ID:policy/AllowExternalDNSUpdates


read -r -d '' TRUST_RELATIONSHIP_EXTERNAL_DNS <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "route53:ChangeResourceRecordSets"
      ],
      "Resource": [
        "arn:aws:route53:::hostedzone/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "route53:ListHostedZones",
        "route53:ListResourceRecordSets",
        "route53:ListTagsForResource"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
EOF
echo "${TRUST_RELATIONSHIP_EXTERNAL_DNS}" > trust-external-dns.json

aws iam create-policy --policy-name "AllowExternalDNSUpdates" --policy-document file://trust-external-dns.json

# example: arn:aws:iam::XXXXXXXXXXXX:policy/AllowExternalDNSUpdates
export POLICY_ARN=$(aws iam list-policies \
 --query 'Policies[?PolicyName==`AllowExternalDNSUpdates`].Arn' --output text)

