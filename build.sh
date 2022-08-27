#!/bin/bash
set -e
if [ "$1" == "clean" ]; then
  mvn clean
  if [ -d "target" ]; then
    rm -rf target
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
else
  echo "=============================== Cloud OJ Build ================================"
  if [ ! -d "target" ]; then
    mkdir target
  else
    rm -rf target/*
  fi
  # build web
  cd web
  npm install --loglevel info
  npm run build
  # build services
  cd ../services
  mvn clean -B package '-Dmaven.test.skip=true' --file pom.xml
  cd ../
  # copy artifacts
  cp -r web/dist/* build/web/
  cp -r services/gateway/target/*.jar target/gateway.jar
  cp -r services/registry/target/*.jar target/registry.jar
  cp -r services/file-service/target/*.jar target/file-service.jar
  cp -r services/core-service/target/*.jar target/core-service.jar
  cp -r services/judge-service/target/*.jar target/judge-service.jar
  echo "=================================== Success ==================================="
fi
