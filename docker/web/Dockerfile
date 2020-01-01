FROM nginx:alpine
COPY nginx.conf nginx.https.conf /templates/
COPY dist /usr/share/nginx/html
COPY config.sh /docker-entrypoint.d/00-config.sh
RUN chmod +x /docker-entrypoint.d/00-config.sh