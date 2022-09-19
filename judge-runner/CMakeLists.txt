cmake_minimum_required(VERSION 3.16)
project(judge-runner)

set(CMAKE_CXX_STANDARD 14)
set(Boost_USE_STATIC_LIBS ON)
set(INSTALL_BINDIR /opt/cloud-oj/bin)

find_package(
        Boost COMPONENTS iostreams program_options REQUIRED
)

include_directories(
        include
)

aux_source_directory(src/judge JUDGE)
aux_source_directory(src/judged JUDGED)

if (Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})

    MESSAGE(STATUS "Boost_INCLUDE_DIRS = ${Boost_INCLUDE_DIRS}.")
    MESSAGE(STATUS "Boost_LIBRARIES = ${Boost_LIBRARIES}.")
    MESSAGE(STATUS "Boost_LIB_VERSION = ${Boost_LIB_VERSION}.")

    add_executable(judge src/common.cpp ${JUDGE})
    add_executable(judged src/common.cpp ${JUDGED})
    target_link_libraries(judge ${Boost_LIBRARIES})
endif ()

install(
        TARGETS judged judge
        RUNTIME DESTINATION ${INSTALL_BINDIR}
)
