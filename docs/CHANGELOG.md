# Changelog: FastRegex

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-25
### Added
- **Zero-Allocation Regex Scanner (`FastRegex`)**: High-throughput literal prefix and structured pattern matching.
- **Zero-Allocation Match Result (`MatchResult`)**: Direct in-place capture offset tracking without substring allocation.
- **Single-Pass Whitespace Normalizer**: 13.23× faster whitespace compaction than standard JDK `replaceAll`.
- **JMH Microbenchmark Suite**: Head-to-head performance benchmarks against JDK `java.util.regex`.
