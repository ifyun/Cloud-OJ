#!/bin/bash
set -e
docker pull registry.cn-hangzhou.aliyuncs.com/cloud_oj/runner:latest
exec "$@"
