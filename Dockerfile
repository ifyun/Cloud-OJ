# build environment
FROM openjdk:17-slim-bullseye as build-env
RUN curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
  && sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && apt-get update \
  && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    libboost-iostreams-dev \
    libboost-program-options-dev \
    maven \
    nodejs \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*
COPY . /build
WORKDIR /build
VOLUME ["/root/.m2", "/build/web/node_modules"]
ARG TARGET=""
ENV TARGET ${TARGET}
RUN --mount=type=cache,target=/build/web/node_modules/ \
    --mount=type=cache,target=/root/.m2/ \
    cp services/mvn.xml /root/.m2/settings.xml \
    && bash build target ${TARGET}

FROM nginx:alpine as web
COPY --from=build-env /build/docker/nginx.conf docker/nginx.https.conf /templates/
COPY --from=build-env /build/docker/config.sh /docker-entrypoint.d/00-config.sh
COPY --from=build-env /build/dist /usr/share/nginx/html

FROM openjdk:17-slim-bullseye as core
COPY --from=build-env /build/services/core/target/lib/* /app/lib/
COPY --from=build-env /build/services/core/target/*.jar /app/core.jar
WORKDIR /app
EXPOSE 8180
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "core.jar"]

FROM openjdk:17-slim-bullseye as gateway
COPY --from=build-env /build/services/gateway/target/lib/* /app/lib/
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "gateway.jar"]

FROM openjdk:17-slim-bullseye as judge
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
    && rm kotlin-compiler-1.7.10.zip \
RUN sed -i "s/deb.debian.org/mirrors.ustc.edu.cn/g" /etc/apt/sources.list \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ mono-devel nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY --from=build-env /build/services/judge/entrypoint.sh /app/entrypoint.sh
COPY --from=build-env /build/services/judge/target/lib/* /app/lib/
COPY --from=build-env /build/services/judge/target/*.jar /app/judge.jar
COPY --from=build-env /build/judge/cmake-build-release/judge /usr/bin/
COPY --from=build-env /build/judge/cmake-build-release/judged /usr/bin/
WORKDIR /app
EXPOSE 8380
CMD ["bash", "entrypoint.sh"]
