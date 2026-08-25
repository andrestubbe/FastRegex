# FastRegex 0.1.0 [ALPHA] — Zero-Allocation SIMD Byte-Pattern and Regex Scanner

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastRegex/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastRegex)

---

**⚡ Ultra-fast zero-allocation byte pattern matching, hardware AVX2 vector scanning, capture extraction, and single-pass whitespace normalization for Java.**

**FastRegex** replaces the heavy heap allocation overhead and non-deterministic backtracking latency of standard `java.util.regex.Pattern`. By combining reusable zero-allocation `MatchResult` structures with hardware-accelerated `FastSIMD` byte scanning, FastRegex delivers deterministic high-throughput scanning for multi-gigabyte log analysis, AI vision grounding parsing, and high-speed compiler text pipelines.

---

## 📑 Table of Contents

- [Why FastRegex?](#why-fastregex)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Real-World Scenarios](#real-world-scenarios)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [License](#license)

---

## Why FastRegex?

> [!IMPORTANT]
> **"Deterministic Single-Pass Scans Over Exponential Backtracking. Zero Heap Allocations Over Matcher Garbage."**

Standard Java regular expressions (`java.util.regex`) are built for general-purpose text manipulation, but introduce severe performance bottlenecks in high-throughput JVM applications:

1. **Massive Garbage Collector Pressure**: Every regex evaluation allocates a new `Matcher` object on the heap, and every capture group extraction (`matcher.group(n)`) copies characters into brand-new `String` instances.
2. **Exponential Backtracking (ReDoS Risk)**: Standard NFA engines suffer from non-deterministic backtracking latency when matching complex patterns against large inputs.
3. **Multi-Pass Text Normalization Overhead**: Common operations like whitespace collapsing (`.replaceAll("\\s+", " ").trim()`) parse the input text multiple times, creating redundant intermediate strings.

**FastRegex solves this** by executing deterministic, single-pass linear scans directly across byte arrays and native memory buffers (`FastPointer`), writing match boundaries into reusable structs without allocating a single byte of heap garbage.

---

## Quick Start

```java
import fastregex.FastRegex;
import fastregex.MatchResult;

public class Demo {
    public static void main(String[] args) {
        // 1. Compile zero-allocation pattern scanner
        FastRegex regex = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        MatchResult result = new MatchResult();

        // 2. Scan text with 0 heap allocation
        String input = "Target element detected at [120, 250, 160, 750] on desktop screen.";
        if (regex.find(input, result)) {
            int ymin = result.parseGroupAsInt(input, 1);
            int xmin = result.parseGroupAsInt(input, 2);
            int ymax = result.parseGroupAsInt(input, 3);
            int xmax = result.parseGroupAsInt(input, 4);
            System.out.printf("Bounding Coordinates: %d, %d, %d, %d\n", ymin, xmin, ymax, xmax);
        }

        // 3. Single-pass Whitespace Normalization (replaces .replaceAll("\\s+", " ").trim())
        String messy = "  Header \t \n\n Section 1:   Analysis    summary.  ";
        String clean = FastRegex.normalizeWhitespace(messy);
        System.out.println("Cleaned: " + clean);
    }
}
```

---

## Key Features

- **🚀 Zero Heap Allocations** — Reusable `MatchResult` containers eliminate `Matcher` and intermediate substring allocations entirely.
- **⚡ Hardware SIMD / AVX2 Vectorization** — Direct off-heap byte buffer scanning using `FastSIMD` scanning 32 bytes per cycle.
- **🎯 Structured Coordinate Scanning** — Sub-microsecond coordinate and delimiter parsing for VLM models (Qwen2-VL, SmolVLM).
- **🧹 Single-Pass Whitespace Normalization** — Inline whitespace compaction outperforming chained JDK regex `replaceAll` calls by **>13×**.
- **🔒 Linear Time Guarantees** — Deterministic scans without regex exponential backtracking vulnerabilities (ReDoS-free).

---

## Real-World Scenarios

- **🤖 Multimodal AI Vision & Grounding** — Ultra-low latency coordinate parsing in `FastAIVision` screen automation pipelines.
- **📑 Document Parsing & OCR Cleanup** — Fast whitespace and layout normalization in `FastContentParse` without GC pressure.
- **🔍 High-Speed Log & Compliance Audits** — Streaming multi-gigabyte server and change logs in `FastAIMatcher`.
- **💻 Source Code & Syntax Analyzers** — High-speed delimiter and literal token isolation in `FastTokenize`.

---

## Performance Benchmarks

Benchmarked on **JDK 26 HotSpot 64-Bit** measuring single-thread operations throughput:

| Benchmark Operation | JDK Standard (`java.util.regex`) | **FastRegex (Zero-Alloc / SIMD)** | Measured Speedup | Memory Overhead |
|---|---|---|---|---|
| **Vision Grounding Coordinate Extraction** | 2,808 ops/ms | **7,102 ops/ms** | **2.53× Faster** | **0 bytes (Zero-Alloc)** |
| **Whitespace Normalization (`collapseWhitespace`)** | 265 ops/ms | **3,514 ops/ms** | **13.23× Faster** | **Single-Pass Stream** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `FastRegex.compile(pattern)` | Compiles a zero-allocation regex scanner instance. |
| `regex.find(text, result)` | Scans text without allocating substring objects, populating `MatchResult`. |
| `regex.find(pointer, len, result)` | Scans off-heap memory directly using hardware AVX2 `FastSIMD` byte scanning. |
| `FastRegex.normalizeWhitespace(text)` | Single-pass whitespace compaction string convenience method. |
| `FastRegex.normalizeWhitespace(src, len, dest)` | Zero-allocation byte buffer whitespace compaction. |
| `result.parseGroupAsInt(text, group)` | Parses integer value of capture group without intermediate String creation. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Regex Demo** | [Demo.java](examples/Demo/src/main/java/fastregex/demo/Demo.java) | `run-demo.bat` | Zero-allocation coordinate matching and single-pass whitespace compaction. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastregex/benchmark/Benchmark.java) | `run-benchmark.bat` | Head-to-head performance benchmarks against standard JDK `Pattern`/`Matcher`. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastRegex</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastSIMD</artifactId>
        <version>0.1.3</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastPointer</artifactId>
        <version>0.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastBinary</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastRegex:0.1.0'
    implementation 'com.github.andrestubbe:FastSIMD:0.1.3'
    implementation 'com.github.andrestubbe:FastPointer:0.1.1'
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ⚡ **[FastRegex-0.1.0.jar](https://github.com/andrestubbe/FastRegex/releases/download/0.1.0/FastRegex-0.1.0.jar)** (Zero-Allocation Regex Scanner)
2. 🚀 **[FastSIMD-0.1.3.jar](https://github.com/andrestubbe/FastSIMD/releases/download/0.1.3/FastSIMD-0.1.3.jar)** (Hardware SIMD Vectorization)
3. 📍 **[FastPointer-0.1.1.jar](https://github.com/andrestubbe/FastPointer/releases/download/0.1.1/FastPointer-0.1.1.jar)** (Native Address Arithmetic)
4. ⚙️ **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (Bit-Packing & VarInt Utilities)
5. 📦 **[fastcore-0.1.0.jar](https://github.com/andrestubbe/fastcore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and memory model.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀🔍*
