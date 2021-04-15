#!/bin/bash
set -e
mvn clean
if [ -d "build" ]; then
  rm -rf build
fi
if [ -d "judge-runner/cmake-build-debug" ]; then
  rm -rf judge-runner/cmake-build-debug
fi
if [ -d "judge-runner/cmake-build-release" ]; then
  rm -rf judge-runner/cmake-build-release
fi
if [ -d "web/dist" ]; then
  rm -rf web/dist
fi