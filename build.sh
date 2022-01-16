#!/bin/bash
set -e
echo "=============================== Cloud OJ Build ================================"
if [ ! -d "build" ]; then
  mkdir build
else
  rm -rf build/*
fi
# build web
cd web
npm install --loglevel info
npm run build
cd ../
# build services
mvn clean -B -P prod package '-Dmaven.test.skip=true' --file pom.xml
cd build
mkdir web gateway registry file-service core-service judge-service
cd ../
# copy artifacts
cp -r web/dist/* build/web/
cp -r gateway/target/*.jar gateway/target/*.yml build/gateway/
cp -r registry/target/*.jar registry/target/*.yml build/registry/
cp -r file-service/target/*.jar file-service/target/*.yml build/file-service/
cp -r core-service/target/*.jar core-service/target/*.yml build/core-service/
cp -r judge-service/target/*.jar judge-service/target/*.yml build/judge-service/

rm -rf web/dist
mvn clean

echo "=================================== Success ==================================="
