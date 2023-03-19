# build environment
FROM openjdk:17-slim-bullseye
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