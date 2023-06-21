cmake_minimum_required(VERSION 3.16)
project(judge)

set(CMAKE_CXX_STANDARD 14)
set(INSTALL_DIR /usr/local/bin)

include_directories(
        include
)

aux_source_directory(src/judge JUDGE)
aux_source_directory(src/judged JUDGED)

add_executable(judge src/common.cpp ${JUDGE})
add_executable(judged src/common.cpp ${JUDGED})
target_link_libraries(judged pthread)

install(
        TARGETS judged judge
        RUNTIME DESTINATION ${INSTALL_DIR}
)
