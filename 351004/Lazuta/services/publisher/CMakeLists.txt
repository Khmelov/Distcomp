cmake_minimum_required(VERSION 3.14)
project(publisher)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

include(FetchContent)

FetchContent_Declare(
        jwt_cpp
        GIT_REPOSITORY https://github.com/Thalhammer/jwt-cpp.git
        GIT_TAG v0.7.0
        GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(jwt_cpp)

file(GLOB_RECURSE SOURCE_FILES
        "src/*.cpp"
        "src/*.cc"
)

find_package(Drogon REQUIRED)
find_package(Jsoncpp REQUIRED)

find_package(PkgConfig REQUIRED)
pkg_check_modules(RDKAFKA REQUIRED rdkafka)


add_executable(${PROJECT_NAME}
        ${SOURCE_FILES}
)

target_include_directories(${PROJECT_NAME} PRIVATE
        "${CMAKE_CURRENT_SOURCE_DIR}/src"
        ${RDKAFKA_INCLUDE_DIRS}
        ${jwt_cpp_SOURCE_DIR}/include
)

target_link_libraries(${PROJECT_NAME}
        Drogon::Drogon
        ${RDKAFKA_LIBRARIES}
        rdkafka++
)