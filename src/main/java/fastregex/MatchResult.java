package fastregex;

/**
 * Reusable zero-allocation capture result container.
 * <p>
 * Stores match boundary ranges and sub-group offsets directly in primitive arrays,
 * allowing instant integer coordinate parsing and slice extraction without creating
 * intermediate {@link String} or {@link CharSequence} objects on the Java Heap.
 * </p>
 */
public final class MatchResult {
    private static final int MAX_GROUPS = 16;
    private final int[] starts = new int[MAX_GROUPS];
    private final int[] ends = new int[MAX_GROUPS];
    private int groupCount = 0;
    private boolean matched = false;

    /**
     * Resets the match state and group counts for reuse in object-pooling or ThreadLocal loops.
     */
    public void reset() {
        groupCount = 0;
        matched = false;
    }

    /**
     * Records an overall pattern match range (group 0).
     *
     * @param start start character/byte offset
     * @param end end character/byte offset (exclusive)
     */
    public void setMatch(int start, int end) {
        this.matched = true;
        this.starts[0] = start;
        this.ends[0] = end;
        this.groupCount = Math.max(groupCount, 1);
    }

    /**
     * Records a specific capture group boundary.
     *
     * @param groupIndex capture group index (1-based for sub-groups)
     * @param start start character/byte offset
     * @param end end character/byte offset (exclusive)
     */
    public void setGroup(int groupIndex, int start, int end) {
        if (groupIndex < MAX_GROUPS) {
            this.starts[groupIndex] = start;
            this.ends[groupIndex] = end;
            this.groupCount = Math.max(groupCount, groupIndex + 1);
        }
    }

    /**
     * Returns whether the last scan resulted in a successful match.
     *
     * @return {@code true} if matched, {@code false} otherwise
     */
    public boolean isMatched() {
        return matched;
    }

    /**
     * Returns the start offset of the overall match (group 0).
     *
     * @return start offset
     */
    public int start() {
        return starts[0];
    }

    /**
     * Returns the end offset (exclusive) of the overall match (group 0).
     *
     * @return end offset
     */
    public int end() {
        return ends[0];
    }

    /**
     * Returns the start offset of a specific capture group.
     *
     * @param group capture group index (1-based for sub-groups)
     * @return start offset
     */
    public int start(int group) {
        return starts[group];
    }

    /**
     * Returns the end offset (exclusive) of a specific capture group.
     *
     * @param group capture group index (1-based for sub-groups)
     * @return end offset
     */
    public int end(int group) {
        return ends[group];
    }

    /**
     * Returns the number of capture groups recorded (including group 0).
     *
     * @return group count
     */
    public int groupCount() {
        return groupCount;
    }

    /**
     * Parses the slice corresponding to {@code group} directly as a positive integer from a byte array
     * without creating a {@link String} or performing heap allocations.
     *
     * @param source the original input byte array
     * @param group the 1-based capture group index
     * @return the parsed integer value
     */
    public int parseGroupAsInt(byte[] source, int group) {
        int s = starts[group];
        int e = ends[group];
        int val = 0;
        for (int i = s; i < e; i++) {
            byte b = source[i];
            if (b >= '0' && b <= '9') {
                val = val * 10 + (b - '0');
            }
        }
        return val;
    }

    /**
     * Parses the slice corresponding to {@code group} directly as a positive integer from a {@link CharSequence}
     * without creating a {@link String} or performing heap allocations.
     *
     * @param source the original input text
     * @param group the 1-based capture group index
     * @return the parsed integer value
     */
    public int parseGroupAsInt(CharSequence source, int group) {
        int s = starts[group];
        int e = ends[group];
        int val = 0;
        for (int i = s; i < e; i++) {
            char c = source.charAt(i);
            if (c >= '0' && c <= '9') {
                val = val * 10 + (c - '0');
            }
        }
        return val;
    }
}
