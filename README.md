# FastRegex 0.1.0 [ALPHA] — Zero-Allocation SIMD Byte-Pattern and Regex Scanner

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastRegex/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastRegex)

---

**⚡ Ultra-fast zero-allocation byte pattern matching, capture extraction, and single-pass whitespace normalization for Java.**

**FastRegex** eliminates the heap allocation overhead and non-deterministic backtracking latency of `java.util.regex`. It is designed for multi-gigabyte log analysis, AI vision grounding parsing, and high-speed compiler/tokenizer text pipelines.

---

## Quick Start

```java
import fastregex.FastRegex;
import fastregex.MatchResult;

public class Demo {
    public static void main(String[] args) {
        // 1. Compile zero-allocation pattern
        FastRegex regex = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        MatchResult result = new MatchResult();

        // 2. Scan text with 0 heap allocation
        String input = "Detected element at [120, 250, 160, 750] on screen.";
        if (regex.find(input, result)) {
            int ymin = result.parseGroupAsInt(input, 1);
            int xmin = result.parseGroupAsInt(input, 2);
            int ymax = result.parseGroupAsInt(input, 3);
            int xmax = result.parseGroupAsInt(input, 4);
            System.out.printf("Coordinates: %d, %d, %d, %d\n", ymin, xmin, ymax, xmax);
        }

        // 3. Single-pass Whitespace Compaction (replaces .replaceAll("\\s+", " ").trim())
        String messy = "  Heading 1 \t \n\n Section A   content text.  ";
        String clean = FastRegex.normalizeWhitespace(messy);
    }
}
```

---

## Performance Benchmarks (JMH Verified)

Benchmarked on **JDK 26 HotSpot 64-Bit** measuring single-thread operations throughput:

| Benchmark Operation | JDK Standard (`java.util.regex`) | **FastRegex (Zero-Alloc / SIMD)** | Measured Speedup |
|---|---|---|---|
| **Vision Grounding Coordinate Extraction** | 2,808 ops/ms | **7,102 ops/ms** | **2.53× Faster** (Zero Heap Allocation) |
| **Whitespace Normalization (`collapseWhitespace`)** | 265 ops/ms | **3,514 ops/ms** | **13.23× Faster** (Single-Pass SIMD) |

*Run benchmarks locally:* `.\run-benchmark.bat`

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Regex Demo** | [Demo.java](examples/Demo/src/main/java/fastregex/demo/Demo.java) | `run-demo.bat` | Zero-allocation coordinate matching and whitespace compaction. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastregex/benchmark/Benchmark.java) | `run-benchmark.bat` | Direct head-to-head comparison against standard JDK `Pattern`/`Matcher`. |

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
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ⚡ **[FastRegex-0.1.0.jar](https://github.com/andrestubbe/FastRegex/releases/download/0.1.0/FastRegex-0.1.0.jar)** (Zero-Allocation Regex Scanner)
2. 🚀 **[FastSIMD-0.1.3.jar](https://github.com/andrestubbe/FastSIMD/releases/download/0.1.3/FastSIMD-0.1.3.jar)** (Hardware SIMD Vectorization)
3. ⚙️ **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (Bit-Packing & VarInt Utilities)
4. 📦 **[fastcore-0.1.0.jar](https://github.com/andrestubbe/fastcore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

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
