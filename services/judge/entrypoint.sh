#!/bin/bash
set -e
echo "${JVM_OPTS}"
# start judged as background
judged >/dev/null 2>&1 &
# start judge service
java "${JVM_OPTS}" "-Dspring.profiles.active=prod" -jar judge.jar &
wait -n
exit $?
