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
  cd ../
  # build services
  mvn clean -B package '-Dmaven.test.skip=true' --file pom.xml
  cd target
  mkdir web gateway registry file-service core-service judge-service
  cd ../
  # copy artifacts
  cp -r web/dist/* build/web/
  cp -r gateway/target/*.jar target/gateway.jar
  cp -r registry/target/*.jar target/registry.jar
  cp -r file-service/target/*.jar target/file-service.jar
  cp -r core-service/target/*.jar target/core-service.jar
  cp -r judge-service/target/*.jar target/judge-service.jar
  echo "=================================== Success ==================================="
fi
