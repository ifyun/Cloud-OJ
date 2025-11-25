# OPENJDK ENV
FROM debian:trixie AS openjdk
RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl openjdk-21-jdk-headless \
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
CMD ["bash", "-c", "exec java ${JVM_OPTS} -Dspring.profiles.active=prod -jar core.jar"]
# GATEWAY
FROM openjdk AS gateway
COPY --from=build-env /build/services/gateway/target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
ENV JVM_OPTS="-Xmx200m"
CMD ["bash", "-c", "exec java ${JVM_OPTS} -Dspring.profiles.active=prod -jar gateway.jar"]
# JUDGE
FROM openjdk AS judge
RUN curl -LJO https://packages.microsoft.com/config/debian/12/packages-microsoft-prod.deb \
    && dpkg -i packages-microsoft-prod.deb \
    && apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ python3 dotnet-sdk-8.0 \
    && apt-get clean \
    && rm -r /var/lib/apt/lists/* \
    && rm packages-microsoft-prod.deb \
    && echo -n "dotnet /usr/share/dotnet/sdk/$(dotnet --version)/Roslyn/bincore/csc.dll " > /bin/csc \
    && echo -n "/r:/usr/share/dotnet/sdk/$(dotnet --version)/ref/netstandard.dll " >> /bin/csc \
    && echo '"$@"' >> /bin/csc \
    && chmod +x /bin/csc
COPY dotnet.runtimeconfig.json /etc/
# Install Golang
ADD .build/go1.25.4.linux-amd64.tar.gz /usr/local/
RUN ln -s /usr/local/go/bin/go /usr/bin/go
# Install Kotlin
COPY .build/kotlin-compiler-2.2.21/kotlinc /usr/local/
RUN ln -s /usr/local/kotlinc/bin/kotlinc /usr/bin/kotlinc
# QuickJS
COPY .build/quickjs-linux-x86_64-2025-09-13/qjs /usr/bin/
COPY --from=build-env /build/services/judge/target/*.jar /app/judge.jar
COPY --from=build-env /build/judge/cmake-build-release/judge /usr/bin/
WORKDIR /app
EXPOSE 8380
ENV JVM_OPTS="-Xmx200m"
CMD ["bash", "-c", "exec java ${JVM_OPTS} -Dspring.profiles.active=prod -jar judge.jar"]
