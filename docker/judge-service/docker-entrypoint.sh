#!/bin/bash
set -e
if test -z "${RUNNER_IMAGE}"; then
  echo "Use customized runner image: ${RUNNER_IMAGE}."
else
  echo "Pull default runner image."
  docker pull registry.cn-hangzhou.aliyuncs.com/cloud_oj/judge-runner:latest
fi
exec "$@"
