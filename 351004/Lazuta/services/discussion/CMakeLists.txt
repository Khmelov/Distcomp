cmake_minimum_required(VERSION 3.10)
project(discussion)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

file(GLOB_RECURSE SOURCE_FILES
    "src/*.cpp"
    "src/*.cc"
)

find_package(Drogon REQUIRED)
find_package(Jsoncpp REQUIRED)

find_package(PkgConfig REQUIRED)
pkg_check_modules(MONGOCXX REQUIRED IMPORTED_TARGET libmongocxx)
pkg_check_modules(BSONCXX REQUIRED IMPORTED_TARGET libbsoncxx)
pkg_check_modules(RDKAFKA REQUIRED rdkafka)

add_executable(${PROJECT_NAME}
    ${SOURCE_FILES}
)

target_include_directories(${PROJECT_NAME} PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}/src"
    ${MONGOCXX_INCLUDE_DIRS}
    ${BSONCXX_INCLUDE_DIRS}
    ${RDKAFKA_INCLUDE_DIRS}
)

target_link_directories(${PROJECT_NAME} PRIVATE
    ${MONGOCXX_LIBRARY_DIRS}
    ${BSONCXX_LIBRARY_DIRS}
)

target_link_libraries(${PROJECT_NAME} PRIVATE
    Drogon::Drogon
    PkgConfig::MONGOCXX
    PkgConfig::BSONCXX
    ${RDKAFKA_LIBRARIES}
    rdkafka++
)

