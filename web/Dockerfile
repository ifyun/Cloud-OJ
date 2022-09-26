FROM nginx:alpine
COPY .docker/nginx.conf .docker/nginx.https.conf /templates/
COPY .docker/config.sh /docker-entrypoint.d/00-config.sh
COPY dist /usr/share/nginx/html
RUN chmod +x /docker-entrypoint.d/00-config.sh