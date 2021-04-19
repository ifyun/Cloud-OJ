cmake_minimum_required(VERSION 3.16)
project(judge-runner)

set(CMAKE_CXX_STANDARD 14)
include_directories(
        include
)
aux_source_directory(src SRC_DIR)

add_executable(${PROJECT_NAME} ${SRC_DIR})