#!/bin/bash
set -e
docker pull ${RUNNER_IMAGE}
exec "$@"
