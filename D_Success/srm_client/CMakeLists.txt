cmake_minimum_required(VERSION 3.2)
project(srm-clientn VERSION 1.0.0 LANGUAGES CXX)

include(GNUInstallDirs)

# Enable C++11
set(CMAKE_CXX_STANDARD 11)

# Enable warnings for gcc
if(CMAKE_COMPILER_IS_GNUCXX)
    add_definitions(-Wall -Wextra)
endif()


set(CMAKE_AUTOMOC TRUE)

find_package(OpenCV 3.0 REQUIRED core imgcodecs imgproc)
find_package(Qt5Core REQUIRED)
find_package(Qt5Gui REQUIRED)
find_package(Qt5Widgets REQUIRED)
find_package(Qt5Concurrent REQUIRED)
find_package(Qt5Network REQUIRED)

# Program
set(PROGRAM_SOURCES
    image_display_widget.cpp
    main.cpp
    main_window.cpp)

set(PROGRAM_HEADERS
    image_display_widget.h
    main_window.h)

add_executable(srm-client ${PROGRAM_SOURCES} ${PROGRAM_HEADERS})
target_link_libraries(srm-client  PRIVATE Qt5::Core Qt5::Gui Qt5::Widgets Qt5::Network)
target_link_libraries(srm-client PRIVATE opencv_core opencv_imgproc opencv_imgcodecs)
