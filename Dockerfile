# OPENJDK ENV
FROM debian:bookworm AS openjdk
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends openjdk-17-jdk-headless \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
# BUILD ENV
FROM openjdk AS build-env
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    nodejs \
    npm \
    maven \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
COPY . /build
WORKDIR /build
ARG TARGET=""
ENV TARGET=${TARGET}
RUN --mount=type=cache,target=/root/.m2/ \
    cp dev/mvn.xml /root/.m2/settings.xml \
    && bash build --target=${TARGET}
# WEB
FROM nginx:alpine AS web
COPY --from=build-env \
    /build/web/docker/nginx.conf \
    /build/web/docker/nginx.https.conf \
    /templates/
COPY --from=build-env /build/web/docker/config.sh /docker-entrypoint.d/00-config.sh
COPY --from=build-env /build/web/dist /usr/share/nginx/html
RUN chmod +x /docker-entrypoint.d/00-config.sh
# CORE
FROM openjdk AS core
COPY --from=build-env /build/services/core/target/*.jar /app/core.jar
WORKDIR /app
EXPOSE 8180
ENV JVM_OPTS="-Xmx200m"
CMD  java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core.jar
# GATEWAY
FROM openjdk AS gateway
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar gateway.jar
# JUDGE
FROM openjdk AS judge
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl gcc g++ mono-devel nodejs \
    && apt-get clean \
    && rm -r /var/lib/apt/lists/*
ARG PROXY=""
ENV HTTP_PROXY=${PROXY}
ENV HTTPS_PROXY=${PROXY}
RUN curl -LJO \
    https://go.dev/dl/go1.21.4.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.21.4.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.21.4.linux-amd64.tar.gz
RUN curl -LJO \
    https://github.com/JetBrains/kotlin/releases/download/v1.8.22/kotlin-native-linux-x86_64-1.8.22.tar.gz \
    && tar -C /usr/share -xzf kotlin-native-linux-x86_64-1.8.22.tar.gz \
    && mv /usr/share/kotlin-native-linux-x86_64-1.8.22 /usr/share/kotlin \
    && ln -s /usr/share/kotlin/bin/kotlinc-native /usr/bin/kotlinc-native \
    && ln -s /usr/share/kotlin/bin/run_konan /usr/bin/run_konan \
    && rm kotlin-native-linux-x86_64-1.8.22.tar.gz
# install dependencies
RUN kotlinc-native foo.kt || true
COPY --from=build-env /build/services/judge/target/*.jar /app/judge.jar
COPY --from=build-env \
    /build/judge/cmake-build-release/judge \
    /usr/bin/
WORKDIR /app
EXPOSE 8380
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar judge.jar
