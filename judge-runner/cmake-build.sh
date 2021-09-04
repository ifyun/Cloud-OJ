#!/bin/bash
set -e
build_x64="cmake-build-release-x64"

if [ -d ${build_x64} ]; then
  rm -rf ${build_x64}
fi

mkdir ${build_x64}

cd ${build_x64}
cmake -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_SYSTEM_NAME=Linux \
  -DCMAKE_SYSTEM_PROCESSOR=x86_64 \
  -DCMAKE_C_FLAGS=-m64 \
  -DCMAKE_CXX_FLAGS=-m64 \
  -G "CodeBlocks - Unix Makefiles" ..

cd ../

cmake --build ./cmake-build-release-x64 --target all
