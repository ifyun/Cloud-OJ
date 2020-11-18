FROM openjdk:8-jre-stretch
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
COPY ./judge-service-*.jar /app/judge-service.jar
COPY ./*.yml /app/config/
COPY ./docker-entrypoint.sh /
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && libs="libltdl7" \
    && apt-get update \
    && apt-get install -y --no-install-recommends $libs \
    && apt-get clean && apt-get autoclean
WORKDIR /app
EXPOSE 8002
ENTRYPOINT ["sh", "/docker-entrypoint.sh"]
CMD java ${JAVA_OPTS} -Dspring.profiles.active=prod -jar judge-service.jar