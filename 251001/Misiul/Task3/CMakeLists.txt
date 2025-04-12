cmake_minimum_required(VERSION 3.20)
project(Lab3 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(httplib REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(libpqxx REQUIRED)
find_package(fmt REQUIRED)
find_package(cassandra-cpp-driver REQUIRED)

set(
    HEADERS
    include/Utils.hpp
    include/Entities/Creator.hpp
    include/Entities/Message.hpp
    include/Entities/Tag.hpp
    include/Entities/Topic.hpp
    include/DiscussionHandler.hpp
    include/DiscussionServer.hpp
    include/PublisherHandler.hpp
    include/PublisherServer.hpp
)

add_executable(
    ${PROJECT_NAME}
    ${HEADERS}
    src/main.cpp
    src/Utils.cpp
    src/DBController.cpp
    src/CassandraController.cpp
)

target_include_directories( ${PROJECT_NAME} PRIVATE include)

target_link_libraries(
    ${PROJECT_NAME} 
    httplib::httplib
    nlohmann_json::nlohmann_json
    libpqxx::pqxx
    fmt::fmt
    cassandra-cpp-driver::cassandra-cpp-driver
)
