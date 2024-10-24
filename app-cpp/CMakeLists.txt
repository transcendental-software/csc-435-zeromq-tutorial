cmake_minimum_required(VERSION 3.22)

project(sockets
        VERSION 1.0
        DESCRIPTION "C++ Client-Server ZeroMQ Sockets Example"
        LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

#option(CPPZMQ_BUILD_TESTS "Whether or not to build the tests" OFF)
#add_subdirectory(cppzmq EXCLUDE_FROM_ALL)

add_executable(server src/Server.cpp src/Worker.cpp)
target_link_libraries(server zmq)
target_include_directories(server PUBLIC include)

add_executable(client src/Client.cpp)
target_link_libraries(client zmq)
target_include_directories(client PUBLIC include)
