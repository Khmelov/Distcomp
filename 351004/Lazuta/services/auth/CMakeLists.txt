cmake_minimum_required(VERSION 3.14)
project(auth)

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

# Add bcrypt as a subdirectory
add_subdirectory(thirdparty/Bcrypt.cpp)

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
    ${jwt_cpp_SOURCE_DIR}/include
    thirdparty/Bcrypt.cpp/include
)

target_link_libraries(${PROJECT_NAME}
    Drogon::Drogon
    bcrypt
)
