# OPENJDK ENV
FROM debian:bookworm AS openjdk
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl openjdk-17-jdk-headless \
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
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core.jar
# GATEWAY
FROM openjdk AS gateway
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar gateway.jar
# JUDGE
FROM openjdk AS judge
RUN curl -LJO https://packages.microsoft.com/config/debian/12/packages-microsoft-prod.deb \
    && dpkg -i packages-microsoft-prod.deb \
    && apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ python3 dotnet-sdk-8.0 nodejs \
    && apt-get clean \
    && rm -r /var/lib/apt/lists/* \
    && rm packages-microsoft-prod.deb \
    && echo -n "dotnet /usr/share/dotnet/sdk/$(dotnet --version)/Roslyn/bincore/csc.dll " > /bin/csc \
    && echo -n "/r:/usr/share/dotnet/sdk/$(dotnet --version)/ref/netstandard.dll " >> /bin/csc \
    && echo '"$@"' >> /bin/csc \
    && chmod +x /bin/csc
ARG PROXY=""
ENV HTTP_PROXY=${PROXY}
ENV HTTPS_PROXY=${PROXY}
# Download Golang
RUN curl -LJO https://golang.google.cn/dl/go1.24.1.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.24.1.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.24.1.linux-amd64.tar.gz
# Download Kotlin Native
RUN curl -LJO https://github.com/JetBrains/kotlin/releases/download/v2.1.10/kotlin-native-prebuilt-linux-x86_64-2.1.10.tar.gz \
    && tar -C /usr/local -xzf kotlin-native-prebuilt-linux-x86_64-2.1.10.tar.gz \
    --transform 's/kotlin-native-prebuilt-linux-x86_64-2.1.10/kotlin/' \
    && ln -s /usr/local/kotlin/bin/kotlinc-native /usr/bin/kotlinc-native \
    && ln -s /usr/local/kotlin/bin/run_konan /usr/bin/run_konan \
    && rm kotlin-native-prebuilt-linux-x86_64-2.1.10.tar.gz
# Download Kotlin Native Dependencies
RUN kotlinc-native nothing.kt || true
COPY dotnet.runtimeconfig.json /etc/
COPY --from=build-env /build/services/judge/target/*.jar /app/judge.jar
COPY --from=build-env \
    /build/judge/cmake-build-release/judge \
    /usr/bin/
WORKDIR /app
EXPOSE 8380
ENV JVM_OPTS="-Xmx200m"
CMD java ${JVM_OPTS} -Dspring.profiles.active=prod -jar judge.jar
