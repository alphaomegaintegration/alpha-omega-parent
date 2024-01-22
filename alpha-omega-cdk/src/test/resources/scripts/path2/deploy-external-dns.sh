#!/bin/sh
source ./init-env.sh

kubectl apply -f external-dns-deployment.yaml

kubectl logs -f $(kubectl get po | egrep -o 'external-dns[A-Za-z0-9-]+')