FROM mysql:8.0.18
COPY my.cnf /etc/mysql/conf.d/
COPY *.sql /docker-entrypoint-initdb.d/
RUN chmod 644 /etc/mysql/conf.d/my.cnf