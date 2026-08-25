# FastRegex Roadmap

## Planned Features

- **v0.2.0**: Native AVX2 DFA (Deterministic Finite Automaton) acceleration kernel for general regular expressions.
- **v0.3.0**: Multi-pattern Aho-Corasick vector scanner for concurrent keyword and compliance rule scanning (`FastAIMatcher` / `FastFileContentIndex`).
- **v0.4.0**: Zero-copy byte buffer streaming from `FastSharedMemory` and `FastIO` mmap channels.
