PROJECT(cpp)
CMAKE_MINIMUM_REQUIRED(VERSION 2.8 FATAL_ERROR)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

ADD_EXECUTABLE( senzorji src/serial_comunnication.cpp src/libserial.cpp src/sensors)
