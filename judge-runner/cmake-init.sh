#!/bin/bash
build_dir_debug="cmake-build-debug";
build_dir_release="cmake-build-release";

mkdir ${build_dir_debug} ${build_dir_release};

cd ${build_dir_debug} && cmake -DCMAKE_BUILD_TYPE=Debug -G "CodeBlocks - Unix Makefiles" ..;
cd ../${build_dir_release} && cmake -DCMAKE_BUILD_TYPE=Release -G "CodeBlocks - Unix Makefiles" ..;