# Overview
This project offer examples of how JDK 24 features can be used.

# AOT class loading and linking
The `aot-benchmark` module represents a simple Spring Boot REST API. It contains Gradle tasks for building the application and
running it in different AOT modes.

Usage (assuming the current directory is the project root):
1. `./gradlew :aot-benchmark:bootJar`
2. `./start-aot-record.bat` - this will open a separate console with the app
3. `./record-aot-archive.bat` - this will automatically stop the app from step 2 once done
4. run `./gradlew :aot-benchmark:coldStartTest`
5. check `aot-benchmark/build/results/jmh/results.txt`

# Class File API
The `class-file-api` module contains examples of how to use the Class File API to generate a logging proxy for an existing class.
Explore the unit tests and main sources as usual.

# Flexible constructor bodies
The `flexible-constructor-bodies` module contains examples of how to use the extended syntax for constructors.
Explore the unit tests and main sources as usual.

# Primitive pattern matching
The `primitive-pattern-matching` module contains examples of how to use primitive types in pattern matching, nested record patterns, and `instanecof`.
Explore the unit tests and main sources as usual.

# Simple source files
The `simple-source-files` module contains examples of how to run simplified Java programs without compilation. Use the `run.bat` script for testing.

# Stream gatherers
The `stream-gatherers` module contains examples of how to use the new stream gatherers API.
Explore the unit tests and main sources as usual.

# Structured concurrency
The `structured-concurrency` module contains examples of how to use the new structured concurrency API.
Explore the unit tests and main sources as usual.

# Vector API
The `vector-api` module contains examples of how to use the experimental vector API.
Explore the unit tests and main sources as usual.
