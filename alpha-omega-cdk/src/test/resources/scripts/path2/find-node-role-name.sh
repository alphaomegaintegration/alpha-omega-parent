#!/bin/sh
source ./init-env.sh

export EKS_CLUSTER_NAME=$EKS_CLUSTER_NAME
export EKS_CLUSTER_REGION=$AWS_REGION

# get managed node group name (assuming there's only one node group)
GROUP_NAME=$(aws eks list-nodegroups --cluster-name $EKS_CLUSTER_NAME \
  --query nodegroups --out text)
# fetch role arn given node group name
ROLE_ARN=$(aws eks describe-nodegroup --cluster-name $EKS_CLUSTER_NAME \
  --nodegroup-name $GROUP_NAME --query nodegroup.nodeRole --out text)
# extract just the name part of role arn
ROLE_NAME=${NODE_ROLE_ARN##*/}


# node instance name of one of the external dns pods currently running
INSTANCE_NAME=$(kubectl get pods --all-namespaces \
  --selector app.kubernetes.io/instance=external-dns \
  --output jsonpath='{.items[0].spec.nodeName}')

# instance name of one of the nodes (change if node group is different)
INSTANCE_NAME=$(kubectl get nodes --output name | cut -d'/' -f2 | tail -1)

get_instance_id() {
  INSTANCE_NAME=$1 # example: ip-192-168-74-34.us-east-2.compute.internal

  # get list of nodes
  # ip-192-168-74-34.us-east-2.compute.internal	aws:///us-east-2a/i-xxxxxxxxxxxxxxxxx
  # ip-192-168-86-105.us-east-2.compute.internal	aws:///us-east-2a/i-xxxxxxxxxxxxxxxxx
  NODES=$(kubectl get nodes \
   --output jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.providerID}{"\n"}{end}')

  # print instance id from matching node
  grep $INSTANCE_NAME <<< "$NODES" | cut -d'/' -f5
}

INSTANCE_ID=$(get_instance_id $INSTANCE_NAME)

findRoleName() {
  INSTANCE_ID=$1

  # get all of the roles
  ROLES=($(aws iam list-roles --query Roles[*].RoleName --out text))
  for ROLE in ${ROLES[*]}; do
    # get instance profile arn
    PROFILE_ARN=$(aws iam list-instance-profiles-for-role \
      --role-name $ROLE --query InstanceProfiles[0].Arn --output text)
    # if there is an instance profile
    if [[ "$PROFILE_ARN" != "None" ]]; then
      # get all the instances with this associated instance profile
      INSTANCES=$(aws ec2 describe-instances \
        --filters Name=iam-instance-profile.arn,Values=$PROFILE_ARN \
        --query Reservations[*].Instances[0].InstanceId --out text)
      # find instances that match the instant profile
      for INSTANCE in ${INSTANCES[*]}; do
        # set role name value if there is a match
        if [[ "$INSTANCE_ID" == "$INSTANCE" ]]; then ROLE_NAME=$ROLE; fi
      done
    fi
  done

  echo $ROLE_NAME
}

NODE_ROLE_NAME=$(findRoleName $INSTANCE_ID)

# attach policy arn created earlier to node IAM role
aws iam attach-role-policy --role-name $NODE_ROLE_NAME --policy-arn $POLICY_ARN