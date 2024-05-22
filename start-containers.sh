#!/bin/bash

dicts=("keycloak" "postgres")

for d in ${dicts[*]};
do
  (cd $d && bash start.sh)
done