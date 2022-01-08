#!/bin/bash
docker run --name mysql_cloudoj \
  -p 3306:3306 \
  -e "TZ=Asia/Shanghai" \
  -e "MYSQL_ROOT_PASSWORD=root" \
  -e "MYSQL_ROOT_HOST=%" \
  -v "$PWD"/sql:/docker-entrypoint-initdb.d \
  -v mysql-cloudoj:/var/lib/mysql \
  -d mysql:8.0
