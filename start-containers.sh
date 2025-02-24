#!/bin/bash

dicts=("keycloak" "postgres" "kafka")

for d in ${dicts[*]};
do
  (cd $d && bash start.sh)
done