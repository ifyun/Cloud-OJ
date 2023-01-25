FROM openjdk:17-bullseye
# Install Golang
RUN curl -LJO \
    https://go.dev/dl/go1.19.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.19.linux-amd64.tar.gz \
    && ln -s /usr/local/go/bin/go /usr/bin/go \
    && rm go1.19.linux-amd64.tar.gz
# Install Kotlin
RUN curl -LJO \
    https://github.com/JetBrains/kotlin/releases/download/v1.7.10/kotlin-compiler-1.7.10.zip \
    && unzip -d /usr/share kotlin-compiler-1.7.10.zip \
    && ln -s /usr/share/kotlinc/bin/kotlinc /usr/bin/kotlinc \
    && ln -s /usr/share/kotlinc/bin/kotlin /usr/bin/kotlin \
    && rm kotlin-compiler-1.7.10.zip \
COPY ./entrypoint.sh /app/entrypoint.sh
COPY ./target/lib/* /app/lib/
COPY ./target/bin/* /usr/bin/
COPY ./target/*.jar /app/judge-service.jar
RUN sed -i "s/deb.debian.org/mirrors.163.com/g" /etc/apt/sources.list \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends sqlite3 gcc g++ python3 mono-devel nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && chmod +x /app/entrypoint.sh
WORKDIR /app
EXPOSE 8083
CMD ./entrypoint.sh
