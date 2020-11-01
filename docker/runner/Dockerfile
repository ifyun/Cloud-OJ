FROM openjdk:8-jdk-stretch
ENV TIME_ZONE Asia/Shanghai
COPY ./sources.list /etc/apt/sources.list
COPY ./*.cpp ./*.h ./
RUN ln -fs /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} > /etc/timezone \
    && libs="gcc g++ python3 mono-devel" \
    && apt-get purge -y python \
    && apt-get autoremove -y \
    && apt-get update \
    && apt-get install -y $libs \
    && apt-get clean && apt-get autoclean \
    && ln -s /usr/bin/python3 /usr/bin/python \
    && g++ ./runner.cpp ./utils.cpp -o /usr/bin/runner && rm ./runner.cpp