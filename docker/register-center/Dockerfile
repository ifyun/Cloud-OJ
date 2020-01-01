FROM openjdk:8-jre-alpine
ENV TIME_ZONE Asia/Shanghai
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk add tzdata && cp /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && apk del tzdata \
    && mkdir -p /app/log
COPY ./register-center-*.jar /app/register-center.jar
COPY ./*.yml /app/config/
VOLUME [ "/app/log" ]
WORKDIR /app
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "register-center.jar"]