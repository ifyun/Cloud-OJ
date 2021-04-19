#!/bin/bash
set -e
build_x64="cmake-build-release-x64"
build_x86="cmake-build-release-x86"

if [ -d ${build_x64} ]; then
  rm -rf ${build_x64}
fi

if [ -d ${build_x86} ]; then
  rm -rf ${build_x86}
fi

mkdir ${build_x64} ${build_x86}

cd ${build_x64}
cmake -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_SYSTEM_NAME=Linux \
  -DCMAKE_SYSTEM_PROCESSOR=x86_64 \
  -DCMAKE_C_FLAGS=-m64 \
  -DCMAKE_CXX_FLAGS=-m64 \
  -G "CodeBlocks - Unix Makefiles" ..

cd ../${build_x86}
cmake -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_SYSTEM_NAME=Linux \
  -DCMAKE_SYSTEM_PROCESSOR=x86 \
  -DCMAKE_C_FLAGS=-m32 \
  -DCMAKE_CXX_FLAGS=-m32 \
  -G "CodeBlocks - Unix Makefiles" ..
