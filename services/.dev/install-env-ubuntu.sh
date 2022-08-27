#!/bin/bash
set -e

if [ "$(id -u)" -ne 0 ]; then
  echo "Permission required."
  exit 1
fi

curl -sL https://deb.nodesource.com/setup_lts.x | bash - &&
  libs="sqlite3 gcc g++ python3 openjdk-11-jdk mono-devel nodejs" &&
  apt-get update &&
  apt-get install -y --no-install-recommends "$libs" &&
  apt-get clean

if [ -d "/usr/share/kotlinc" ]; then
  rm -rf /usr/share/kotlinc/*
fi

curl -LJO \
  https://github.com/JetBrains/kotlin/releases/download/v1.7.10/kotlin-compiler-1.7.10.zip &&
  unzip -d /usr/share kotlin-compiler-1.7.10.zip &&
  ln -s /usr/share/kotlinc/bin/kotlinc /usr/bin/kotlinc &&
  ln -s /usr/share/kotlinc/bin/kotlin /usr/bin/kotlin &&
  rm kotlin-compiler-1.7.10.zip

if [ -d "/usr/local/go" ]; then
  rm -rf /usr/local/go/*
fi

curl -LJO \
  https://golang.google.cn/dl/go1.19.linux-amd64.tar.gz &&
  tar -C /usr/local -xzf go1.19.linux-amd64.tar.gz &&
  ln -s /usr/local/go/bin/go /usr/bin/go &&
  rm go1.19.linux-amd64.tar.gz
