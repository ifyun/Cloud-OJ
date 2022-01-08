#!/bin/bash
docker run --name rabbitmq_cloudoj \
  -p 5672:5672 \
  -p 15672:15672 \
  -e "RABBITMQ_DEFAULT_USER=admin" \
  -e "RABBITMQ_DEFAULT_PASS=admin" \
  -v rabbitmq-cloudoj:/var/lib/rabbitmq/mnesia \
  -d rabbitmq:3.8.2-management
