FROM openjdk:8-jdk-stretch
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
COPY ./*.cpp ./*.h /
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && libs="gcc g++ python3 mono-devel nodejs" \
    && apt-get purge -y python \
    && apt-get autoremove -y \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get update \
    && apt-get install -y $libs \
    && apt-get clean && apt-get autoclean
RUN g++ ./runner.cpp ./utils.cpp -o /usr/bin/runner && rm ./*.cpp ./*.h