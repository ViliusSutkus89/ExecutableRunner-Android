cmake_minimum_required(VERSION 3.10.2)

project("ExecutableRunnerTests")

SET(CMAKE_CXX_STANDARD 17)
SET(CMAKE_CXX_FLAGS "-Wall -Wextra -pedantic")

SET(CMAKE_C_STANDARD 11)
SET(CMAKE_C_FLAGS "-Wall -Wextra -pedantic")

add_library(nativeLib SHARED nativeLib.cpp)

add_executable(simpleProgram simpleProgram.cpp)
set_target_properties(simpleProgram PROPERTIES OUTPUT_NAME libSimpleProgram.so)

add_executable(executableLinkedAgainstLibrary executableLinkedAgainstLibrary.cpp)
set_target_properties(executableLinkedAgainstLibrary PROPERTIES OUTPUT_NAME libExecutableLinkedAgainstLibrary.so)
target_link_libraries(executableLinkedAgainstLibrary nativeLib)

add_executable(verifyEnvVar verifyEnvVar.cpp)
set_target_properties(verifyEnvVar PROPERTIES OUTPUT_NAME libVerifyEnvVar.so)
