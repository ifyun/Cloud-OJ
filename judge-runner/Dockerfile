FROM openjdk:8-jdk-stretch
ENV TIME_ZONE Asia/Shanghai
RUN curl -LJO \
    https://github.com/JetBrains/kotlin/releases/download/v1.4.10/kotlin-compiler-1.4.10.zip \
    && unzip -d /opt kotlin-compiler-1.4.10.zip \
    && ln -s /opt/kotlinc/bin/kotlinc /usr/bin/kotlinc \
    && ln -s /opt/kotlinc/bin/kotlin /usr/bin/kotlin \
    && rm kotlin-compiler-1.4.10.zip
RUN libs="gcc g++ python3 mono-devel nodejs" \
    && sed -i 's/deb.debian.org/mirrors.163.com/g' /etc/apt/sources.list \
    && apt-get purge -y python \
    && apt-get autoremove -y \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y --no-install-recommends $libs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY ./cmake-build-release/runner /usr/bin/runner