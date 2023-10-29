# build environment
FROM openjdk:17-jdk-bullseye as build-env
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list \
  && mkdir -p /etc/apt/keyrings \
  && apt-get update \
  && apt-get install -y ca-certificates gnupg --no-install-recommends \
  && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key \
    | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
  && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_18.x nodistro main" \
    | tee /etc/apt/sources.list.d/nodesource.list \
  && apt-get update \
  && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    maven \
    nodejs \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*
COPY . /build
WORKDIR /build
ARG TARGET=""
ENV TARGET=${TARGET}
RUN --mount=type=cache,target=/root/.m2/ \
    cp dev/mvn.xml /root/.m2/settings.xml \
    && bash build target ${TARGET}
# web
FROM nginx:alpine as web
COPY --from=build-env \
    /build/web/docker/nginx.conf \
    /build/web/docker/nginx.https.conf \
    /templates/
COPY --from=build-env /build/web/docker/config.sh /docker-entrypoint.d/00-config.sh
COPY --from=build-env /build/web/dist /usr/share/nginx/html
RUN chmod +x /docker-entrypoint.d/00-config.sh
# core service
FROM openjdk:17-slim-bullseye as core
COPY --from=build-env /build/services/core/target/*.jar /app/core.jar
WORKDIR /app
EXPOSE 8180
ENV JVM_OPTS="-Xmx200m"
CMD  java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core.jar
# gateway service
FROM openjdk:17-slim-bullseye as gateway
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar gateway.jar
# judge service
FROM openjdk:17-jdk-bullseye as judge
RUN sed -i "s/deb.debian.org/mirrors.ustc.edu.cn/g" /etc/apt/sources.list \
    && curl -sL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ mono-devel nodejs \
    && apt-get clean && rm -r /var/lib/apt/lists/*
RUN curl -LJO \
    https://go.dev/dl/go1.20.5.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.20.5.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.20.5.linux-amd64.tar.gz
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
