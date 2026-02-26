cmake_minimum_required(VERSION 3.10)
project(task310)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

file(GLOB_RECURSE SOURCE_FILES
    "src/*.cpp"
    "src/*.cc"
)

find_package(Drogon REQUIRED)

add_executable(${PROJECT_NAME}
    ${SOURCE_FILES}
)


target_include_directories(${PROJECT_NAME} PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}/src"
)

target_link_libraries(${PROJECT_NAME} Drogon::Drogon)