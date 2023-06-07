# build environment
FROM openjdk:17-jdk-bullseye as build-env
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
  && apt-get update \
  && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    libboost-iostreams-dev \
    libboost-program-options-dev \
    maven \
    nodejs \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/* \
RUN npm config set registry registry.npm.taobao.org
COPY . /build
WORKDIR /build
ARG TARGET=""
ENV TARGET=${TARGET}
RUN --mount=type=cache,target=/root/.m2/ \
    cp services/mvn.xml /root/.m2/settings.xml \
    && bash build target ${TARGET}
# web
FROM nginx:alpine as web
COPY --from=build-env \
    /build/web/docker/nginx.conf \
    /build/web/docker/nginx.https.conf \
    /templates/
COPY --from=build-env /build/web/docker/config.sh /docker-entrypoint.d/00-config.sh
COPY --from=build-env /build/web/dist /usr/share/nginx/html
# core service
FROM openjdk:17-slim-bullseye as core
COPY --from=build-env /build/services/core/target/lib/* /app/lib/
COPY --from=build-env /build/services/core/target/*.jar /app/core.jar
WORKDIR /app
EXPOSE 8180
ENV JVM_OPTS="-Xmx200m"
CMD  java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core.jar
# gateway service
FROM openjdk:17-slim-bullseye as gateway
COPY --from=build-env /build/services/gateway/target/lib/* /app/lib/
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar gateway.jar
# judge service
FROM openjdk:17-jdk-bullseye as judge
RUN sed -i "s/deb.debian.org/mirrors.ustc.edu.cn/g" /etc/apt/sources.list \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ mono-devel nodejs \
    && apt-get clean && rm -r /var/lib/apt/lists/*
RUN curl -LJO \
    https://go.dev/dl/go1.19.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.19.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.19.linux-amd64.tar.gz
RUN curl -LJO \
    https://github.com/JetBrains/kotlin/releases/download/v1.7.10/kotlin-compiler-1.7.10.zip \
    && unzip -d /usr/share kotlin-compiler-1.7.10.zip \
    && ln -s /usr/share/kotlinc/bin/kotlinc /usr/bin/kotlinc \
    && ln -s /usr/share/kotlinc/bin/kotlin /usr/bin/kotlin \
    && rm kotlin-compiler-1.7.10.zip
COPY --from=build-env /build/services/judge/entrypoint.sh /app/entrypoint.sh
COPY --from=build-env /build/services/judge/target/lib/* /app/lib/
COPY --from=build-env /build/services/judge/target/*.jar /app/judge.jar
COPY --from=build-env \
    /build/judge/cmake-build-release/judge \
    /build/judge/cmake-build-release/judged \
    /usr/bin/
WORKDIR /app
EXPOSE 8380
ENV JVM_OPTS="-Xmx200m"
CMD ["bash", "entrypoint.sh"]
