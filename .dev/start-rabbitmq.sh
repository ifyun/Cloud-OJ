#!/bin/bash
NAME=rabbitmq_cloudoj

if docker ps -a | grep $NAME >/dev/null; then
  docker start $NAME
else
  docker run --name $NAME \
    -p 5672:5672 \
    -p 15672:15672 \
    -e "RABBITMQ_DEFAULT_USER=admin" \
    -e "RABBITMQ_DEFAULT_PASS=admin" \
    -v rabbitmq-cloudoj:/var/lib/rabbitmq/mnesia \
    -d rabbitmq:3.8.2-management
fi
