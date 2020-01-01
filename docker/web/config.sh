#!/bin/sh
ME=$(basename "$0")
conf_file_path='/etc/nginx/templates/default.conf.template'
mkdir /etc/nginx/templates;
if [ "$ENABLE_HTTPS" = 'true' ]; then
  echo "$ME: HTTPS ON";
  echo "$ME: EXTERNAL_URL $EXTERNAL_URL"
  cp /templates/nginx.https.conf $conf_file_path;
else
  echo "$ME: HTTPS OFF";
  cp /templates/nginx.conf $conf_file_path;
fi