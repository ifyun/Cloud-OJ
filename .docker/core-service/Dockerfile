FROM openjdk:8-jre-alpine
ENV TIME_ZONE Asia/Shanghai
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk add tzdata && cp /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && apk del tzdata
COPY ./core-service-*.jar /app/core-service.jar
COPY ./*.yml /app/config/
WORKDIR /app
EXPOSE 8001
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core-service.jar