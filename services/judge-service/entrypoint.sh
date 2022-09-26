#!/bin/bash
set -e
echo "${JVM_OPTS}"
# start judged as background
judged &
# start judge service
java "${JVM_OPTS} -Dspring.profiles.active=prod" -jar judge-service.jar
