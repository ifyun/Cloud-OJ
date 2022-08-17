#!/bin/bash
NAME=mysql_cloudoj

if docker ps -a | grep $NAME >/dev/null; then
  docker start $NAME
else
  docker run --name $NAME \
    -p 3306:3306 \
    -e "TZ=Asia/Shanghai" \
    -e "MYSQL_ROOT_PASSWORD=root" \
    -e "MYSQL_ROOT_HOST=%" \
    -v "$PWD"/sql:/docker-entrypoint-initdb.d \
    -v mysql-cloudoj:/var/lib/mysql \
    -d mysql:8.0.30
fi
