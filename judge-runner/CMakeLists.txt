cmake_minimum_required(VERSION 3.16)
project(judge-runner)

set(CMAKE_CXX_STANDARD 14)
set(Boost_USE_STATIC_LIBS ON)

find_package(
        Boost
        COMPONENTS iostreams REQUIRED
)

include_directories(
        include
)

aux_source_directory(src SRC_DIR)

if (Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})

    MESSAGE( STATUS "Boost_INCLUDE_DIRS = ${Boost_INCLUDE_DIRS}.")
    MESSAGE( STATUS "Boost_LIBRARIES = ${Boost_LIBRARIES}.")
    MESSAGE( STATUS "Boost_LIB_VERSION = ${Boost_LIB_VERSION}.")

    add_executable(${PROJECT_NAME} ${SRC_DIR})
    target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})
endif ()