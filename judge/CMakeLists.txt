cmake_minimum_required(VERSION 3.16)
project(judge)

set(CMAKE_CXX_STANDARD 14)
set(INSTALL_DIR /usr/local/bin)

include_directories(
        include
)

aux_source_directory(src JUDGE)
add_executable(judge ${JUDGE})

install(
        TARGETS judge
        RUNTIME DESTINATION ${INSTALL_DIR}
)
