cmake_minimum_required(VERSION 3.16)
project(judge)

set(CMAKE_CXX_STANDARD 17)
set(BIN_DIR /usr/local/bin)

if (${CMAKE_BUILD_TYPE} STREQUAL "Debug")
    add_definitions(-DDEBUG)
endif ()

include_directories(
        include
)

aux_source_directory(src JUDGE)
add_executable(judge ${JUDGE})

install(
        TARGETS judge
        RUNTIME DESTINATION ${BIN_DIR}
)
