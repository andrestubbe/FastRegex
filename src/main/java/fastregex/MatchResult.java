package fastregex;

/**
 * Reusable zero-allocation capture result container.
 * Stores match range and capture group boundaries without allocating substring objects.
 */
public final class MatchResult {
    private static final int MAX_GROUPS = 16;
    private final int[] starts = new int[MAX_GROUPS];
    private final int[] ends = new int[MAX_GROUPS];
    private int groupCount = 0;
    private boolean matched = false;

    public void reset() {
        groupCount = 0;
        matched = false;
    }

    public void setMatch(int start, int end) {
        this.matched = true;
        this.starts[0] = start;
        this.ends[0] = end;
        this.groupCount = Math.max(groupCount, 1);
    }

    public void setGroup(int groupIndex, int start, int end) {
        if (groupIndex < MAX_GROUPS) {
            this.starts[groupIndex] = start;
            this.ends[groupIndex] = end;
            this.groupCount = Math.max(groupCount, groupIndex + 1);
        }
    }

    public boolean isMatched() {
        return matched;
    }

    public int start() {
        return starts[0];
    }

    public int end() {
        return ends[0];
    }

    public int start(int group) {
        return starts[group];
    }

    public int end(int group) {
        return ends[group];
    }

    public int groupCount() {
        return groupCount;
    }

    /**
     * Parses group range directly to int without String creation.
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
     * Parses group range directly to int from CharSequence.
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
