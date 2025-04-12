cmake_minimum_required(VERSION 3.20)
project(Lab2 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(httplib REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(libpqxx REQUIRED)
find_package(fmt REQUIRED)

set(
    HEADERS
    include/Utils.hpp
    include/Entities/Creator.hpp
    include/Entities/Message.hpp
    include/Entities/Tag.hpp
    include/Entities/Topic.hpp
)

add_executable(
    ${PROJECT_NAME}
    ${HEADERS}
    src/main.cpp
    src/Utils.cpp
    src/DBController.cpp
)

target_include_directories( ${PROJECT_NAME} PRIVATE include)

target_link_libraries(
    ${PROJECT_NAME} 
    httplib::httplib
    nlohmann_json::nlohmann_json
    libpqxx::pqxx
    fmt::fmt
)
