#!/bin/sh
source ./init-env.sh

read -r -d '' EXTERNAL_DNS_DEPLOYMENT <<EOF
apiVersion: v1
kind: ServiceAccount
metadata:
  name: external-dns
  labels:
    app.kubernetes.io/name: external-dns
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: external-dns
  labels:
    app.kubernetes.io/name: external-dns
rules:
- apiGroups: [""]
  resources: ["services", "endpoints", "pods", "nodes"]
  verbs: ["get","watch","list"]
- apiGroups: ["extensions", "networking.k8s.io"]
  resources: ["ingresses"]
  verbs: ["get","watch","list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: external-dns-viewer
  labels:
    app.kubernetes.io/name: external-dns
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: external-dns
subjects:
- kind: ServiceAccount
  name: external-dns
  namespace: default
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: external-dns
  labels:
    app.kubernetes.io/name: external-dns
spec:
  selector:
    matchLabels:
      app.kubernetes.io/name: external-dns
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app.kubernetes.io/name: external-dns
    spec:
      serviceAccountName: external-dns
      securityContext:
        fsGroup: 65534
      containers:
      - name: external-dns
        image: bitnami/external-dns:0.13.1
        # must specify env AWS_REGION in AWS china regions
        # env:
        # - name: AWS_REGION
        #   value: cn-north-1
        args:
        - --source=service
        - --source=ingress
        - --domain-filter=${EXTERNAL_DNS_NAME_DOMAIN} # will make ExternalDNS see only the hosted zones matching provided domain, omit to process all available hosted zones
        - --provider=aws
        - --policy=upsert-only # would prevent ExternalDNS from deleting any records, omit to enable full synchronization
        - --aws-zone-type=public # only look at public hosted zones (valid values are public, private or no value for both)
        - --registry=txt
        - --txt-owner-id=my-identifier

EOF
echo "${EXTERNAL_DNS_DEPLOYMENT}" > external-dns-deployment.yaml
