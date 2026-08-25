# Compiling FastRegex

## Prerequisites
- JDK 17+ (JDK 21+ or JDK 26 recommended)
- Apache Maven 3.9+

## Build Commands

```bash
# Build & Run Tests
mvn clean test

# Install to Local Maven Repository
mvn clean install

# Build JMH Uber Jar
cd examples/Benchmark
mvn clean package
```
