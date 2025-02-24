#!/bin/bash

modules=("channel-controller" "channel-repository" "chat-controller" "chat-repository" "chat-request-filter" "discovery" "gateway" "keycloak-repository" "shared-config" "sse" "user-repository" "user-sse")

for d in ${modules[*]}
do
  (cd "$d" && gradle clean bootJar)
done