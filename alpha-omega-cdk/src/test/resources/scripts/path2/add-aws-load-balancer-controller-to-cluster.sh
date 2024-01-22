#!/bin/sh
source ./init-env.sh

echo "4. Adding AWS Loadbalancer Controller to cluster ${EKS_CLUSTER_NAME} with add-aws-load-balancer-controller-to-cluster.sh "

helm repo add eks https://aws.github.io/eks-charts

wget https://raw.githubusercontent.com/aws/eks-charts/master/stable/aws-load-balancer-controller/crds/crds.yaml
kubectl apply -f crds.yaml

helm install aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system --set clusterName=$EKS_CLUSTER_NAME --set serviceAccount.create=false --set serviceAccount.name=aws-load-balancer-controller