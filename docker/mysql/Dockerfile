# 仅供 GitLab 流水线使用
FROM mysql:8.0
COPY my.cnf /etc/mysql/conf.d/
COPY *.sql /docker-entrypoint-initdb.d/
RUN chmod 644 /etc/mysql/conf.d/my.cnf