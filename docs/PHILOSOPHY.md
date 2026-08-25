# FastRegex Philosophy

## Core Design Principles

1. **Zero Heap Allocation in Hot Loops**: Standard Java `java.util.regex.Pattern` allocates a new `Matcher` and multiple `String` instances per match. FastRegex uses reusable `MatchResult` structs.
2. **Deterministic Linear Scans**: Eliminates non-deterministic NFA backtracking latency.
3. **Single-Pass Stream Processing**: Operations like whitespace compaction are performed in a single vectorizable pass instead of chaining multiple regex passes.
