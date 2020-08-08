FROM openjdk:8-jdk-buster
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && libs="netcat gcc g++ python3 mono-devel" \
    && apt-get clean \
    # 删除镜像自带的 Python2.7
    && apt-get purge -y python \
    && apt-get autoremove -y \
    && apt-get update \
    # 安装 C/C++ Python C#环境
    && apt-get install -y $libs \
    # 清理缓存
    && apt-get clean && apt-get autoclean \
    && ln -s /usr/bin/python3 /usr/bin/python \
    && mkdir -p /app/log /test_data /target
COPY ./judge-service-*.jar /app/judge-service.jar
COPY ./*.yml /app/config/
VOLUME [ "/app/log" ]
WORKDIR /app
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "judge-service.jar"]