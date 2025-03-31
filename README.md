# Overview
This project offer examples of how JDK 24 features can be used.

# Pre-requisites
*BEFORE YOU BUILD/RUN THE CODE*:
- as of time of preparing these code samples, Gradle did not support JDK 24 as its runtime
- so, you have to point your `JAVA_HOME` to a JDK 17-23
- in addition to that, make sure you have a JDK 24 distribution specified in your Gradle properties, one way of doing that is to edit
`<user_home>/.gradle/gradle.properties` like this (**note**: for Windows, start with `/` before your volume letter and use `/` instead of `\`):
```
org.gradle.java.installations.paths=/D:/sdk/openjdk-24-rc-06-02-25
```

# Class File API
The `class-file-api` module contains examples of how to use the Class File API to generate a logging proxy for an existing class.

What to explore:
- main sources: a simple factory that is able to instrument an arbitrary (almost) class with logging
- unit tests: tests for the proxy factory

# Flexible constructor bodies
The `flexible-constructor-bodies` module contains examples of how to use the extended syntax for constructors.

What to explore:
- main sources: an example of how the flexible constructor bodies feature may help with extending a 3rd party class which presumably is heavy to initialise
- unit tests: tests for the extended cache

# Primitive pattern matching
The `primitive-pattern-matching` module contains examples of how to use primitive types in pattern matching, nested record patterns, and `instanecof`.

What to explore:
- main sources:
  - example - `PrimitiveMatcher` which demonstrates `instanceof` with primitive types
  - example - `CpuClassifier` which demonstrates how to use pattern matching over primitive types in combination with guard clauses
  - example - `KeyValueStorage` which demonstrates how to use nested record matching against records having primitive fields
- unit tests: tests for the above examples

# Simple source files
The `simple-source-files` module contains an example of how to run simplified Java programs without compilation. Use the `run.bat` script for testing.
**NOTE**: Requires JDK 23+ on your `JAVA_HOME`.

# Stream gatherers
The `stream-gatherers` module contains examples of how to use the new stream gatherers API.

What to explore:
- unit tests:
  - `GatherersMotivator` compares implementations of the sliding window aggregate using plain loops, Stream with a collector, and Stream with a gatherer
  - `JdkBuiltInGatherers` demonstrates gatherer implementations available in JDK 24 out of box
- main sources: some code supporting the `GatherersMotivator`
- JMH sources:
  - a benchmark comparing how a large stream of numbers is handled by an aggregation backed by a collector vs gatherer
  - spoiler: the variant with the collector fails with OOM, as expected
  - run the `jmh` Gradle task and look into the console and/or `build/results/jmh`

# Structured concurrency
The `structured-concurrency` module contains examples of how to use the new structured concurrency API.

What to explore:
- main sources:
  - compares an Executor-based multithreaded `ExecutorBasedInvoiceService` against a structured task-scoped `StructuredConcurrencyInvoiceService`
  - `TracingContext` and `Task` show how scoped values may replace thread locals in structured concurrency for tracing purposes
- unit tests:
  - tests for the above examples
  - `StructuredConcurrencyCornerCases` demonstrates some extra corner cases related to structured concurrency

# Vector API
The `vector-api` module contains examples of how to use the experimental vector API.

What to explore:
- main sources: an implementation of vector addition based on plain loops vs the Vector API
- unit tests: tests for the vector math above
- JMH sources: a benchmark that compares vector addition performance with plain loops vs Vector API (run the `jmh` Gradle task and look into the console and/or `build/results/jmh`)
