FROM openjdk:8-jdk-stretch
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
COPY ./*.cpp ./*.h /
RUN wget --no-check-certificate --content-disposition \
    https://github.com/JetBrains/kotlin/releases/download/v1.4.10/kotlin-compiler-1.4.10.zip \
    && unzip -d /opt kotlin-compiler-1.4.10.zip \
    && ln -s /opt/kotlinc/bin/kotlinc /usr/bin/kotlinc \
    && ln -s /opt/kotlinc/bin/kotlin /usr/bin/kotlin \
    && rm kotlin-compiler-1.4.10.zip
RUN libs="gcc g++ python3 mono-devel nodejs" \
    && apt-get purge -y python \
    && apt-get autoremove -y \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y $libs \
    && apt-get clean
RUN g++ ./runner.cpp ./utils.cpp -o /usr/bin/runner && rm ./*.cpp ./*.h