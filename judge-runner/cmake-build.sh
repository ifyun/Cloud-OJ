#!/bin/bash
set -e
build_dir="cmake-build-release"

if [ -d ${build_dir} ]; then
  rm -rf ${build_dir}
fi

mkdir ${build_dir}

cd ${build_dir}
cmake -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_SYSTEM_NAME=Linux \
  -DCMAKE_SYSTEM_PROCESSOR=x86_64 \
  -DCMAKE_C_FLAGS=-m64 \
  -DCMAKE_CXX_FLAGS=-m64 \
  -G "CodeBlocks - Unix Makefiles" ..

cd ../

if [ "$1" == "install" ]; then
  cmake --build ${build_dir} --target all install
  ln -sf /opt/cloud-oj/bin/judge /usr/bin/judge
  ln -sf /opt/cloud-oj/bin/judged /usr/bin/judged
else
  cmake --build ${build_dir} --target all
fi
