FROM openjdk:8-jre-buster
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && libs="libltdl7" \
    && apt-get update \
    && apt-get install -y $libs \
    && apt-get clean && apt-get autoclean \
    && mkdir -p /app/log
COPY ./judge-service-*.jar /app/judge-service.jar
COPY ./*.yml /app/config/
VOLUME [ "/app/log" ]
WORKDIR /app
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "judge-service.jar"]