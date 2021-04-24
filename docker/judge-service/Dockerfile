FROM openjdk:8-jdk-stretch
ENV TIME_ZONE Asia/Shanghai
# Install kotlin
RUN curl -LJO \
    https://github.com/JetBrains/kotlin/releases/download/v1.4.10/kotlin-compiler-1.4.10.zip \
    && unzip -d /usr/share kotlin-compiler-1.4.10.zip \
    && ln -s /usr/share/kotlinc/bin/kotlinc /usr/bin/kotlinc \
    && ln -s /usr/share/kotlinc/bin/kotlin /usr/bin/kotlin \
    && rm kotlin-compiler-1.4.10.zip
# Install Golang
RUN curl -LJO \
    https://golang.google.cn/dl/go1.15.7.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.15.7.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.15.7.linux-amd64.tar.gz
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && sed -i 's/deb.debian.org/mirrors.163.com/g' /etc/apt/sources.list \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && libs="sqlite3 gcc g++ python3 mono-devel nodejs" \
    && apt-get update \
    && apt-get install -y --no-install-recommends $libs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY ./judge-service-*.jar /app/judge-service.jar
COPY ./*.yml /app/config/
WORKDIR /app
EXPOSE 8002
CMD java ${JAVA_OPTS} -Dspring.profiles.active=prod -jar judge-service.jar