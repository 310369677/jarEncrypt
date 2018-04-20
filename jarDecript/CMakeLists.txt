cmake_minimum_required(VERSION 3.9)
project(jarDecript)

set(CMAKE_CXX_STANDARD 11)

include_directories(/usr/local/jdk1.8.0_152/include)
include_directories(/usr/local/jdk1.8.0_152/include/linux)

#[[add_library(StaticBubble STATIC src/StaticBubble.cpp)]]

add_library(jarDecript SHARED decript.cpp Util.cpp Util.h)

#[[add_executable(jarDecript main.cpp decript.cpp Util.cpp Util.h)]]
