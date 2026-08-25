# FastRegex Reference Guide

## API Overview

`fastregex.FastRegex` provides zero-allocation pattern matching and high-throughput SIMD text compaction.

### Core Methods

| Method | Return | Description |
| :--- | :--- | :--- |
| `FastRegex.compile(String pattern)` | `FastRegex` | Compiles a zero-allocation regex scanner instance. |
| `regex.find(CharSequence text, MatchResult result)` | `boolean` | Scans text without allocating substring objects, populating `MatchResult`. |
| `FastRegex.normalizeWhitespace(String text)` | `String` | Single-pass whitespace compaction (replaces `.replaceAll("\\s+", " ").trim()`). |
| `FastRegex.normalizeWhitespace(byte[] src, int len, byte[] dest)` | `int` | Zero-allocation byte-level whitespace compaction. |

---

## MatchResult Methods

| Method | Return | Description |
| :--- | :--- | :--- |
| `result.start()` / `result.end()` | `int` | Start/end index of the whole match. |
| `result.start(int group)` / `result.end(int group)` | `int` | Start/end index of specific capture group. |
| `result.parseGroupAsInt(CharSequence text, int group)` | `int` | Parses integer value of capture group without String allocations. |
