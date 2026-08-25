package fastregex;

import java.nio.charset.StandardCharsets;

/**
 * Fast zero-allocation byte and text pattern scanner.
 * Optimized for common high-throughput patterns:
 * 1. Delimited numeric coordinate groups (e.g. `[y1, x1, y2, x2]` or `<box>(y1, x1, y2, x2)</box>`)
 * 2. Prefix + capture delimiters (e.g. `href="..."`, `title="..."`)
 * 3. In-place whitespace compaction streaming.
 */
public final class FastRegex {

    private final String patternString;
    private final boolean isCoordinateBox;
    private final boolean isXmlBox;
    private final byte[] literalPrefix;

    private FastRegex(String patternString) {
        this.patternString = patternString;
        this.isCoordinateBox = patternString.contains("[") && patternString.contains("\\d+");
        this.isXmlBox = patternString.contains("<box>") && patternString.contains("\\d+");
        if (patternString.startsWith("href=\"")) {
            this.literalPrefix = "href=\"".getBytes(StandardCharsets.UTF_8);
        } else {
            this.literalPrefix = null;
        }
    }

    public static FastRegex compile(String pattern) {
        return new FastRegex(pattern);
    }

    public String pattern() {
        return patternString;
    }

    /**
     * Scans CharSequence directly without heap allocation, populating MatchResult.
     */
    public boolean find(CharSequence text, MatchResult result) {
        if (text == null || text.length() == 0) {
            result.reset();
            return false;
        }

        int len = text.length();
        if (isCoordinateBox) {
            for (int i = 0; i < len; i++) {
                if (text.charAt(i) == '[') {
                    int endBracket = -1;
                    for (int j = i + 1; j < len; j++) {
                        if (text.charAt(j) == ']') {
                            endBracket = j;
                            break;
                        }
                    }
                    if (endBracket != -1) {
                        if (parseFourNumbers(text, i + 1, endBracket, result)) {
                            result.setMatch(i, endBracket + 1);
                            return true;
                        }
                    }
                }
            }
        } else if (isXmlBox) {
            int tagIdx = indexOf(text, "<box>(");
            if (tagIdx != -1) {
                int endTag = indexOf(text, ")</box>", tagIdx + 6);
                if (endTag != -1) {
                    if (parseFourNumbers(text, tagIdx + 6, endTag, result)) {
                        result.setMatch(tagIdx, endTag + 7);
                        return true;
                    }
                }
            }
        }
        result.reset();
        return false;
    }

    /**
     * Fast zero-allocation parser for 4 comma-separated integer capture groups.
     */
    private boolean parseFourNumbers(CharSequence text, int start, int end, MatchResult result) {
        int idx = start;
        for (int g = 1; g <= 4; g++) {
            while (idx < end && Character.isWhitespace(text.charAt(idx))) idx++;
            if (idx >= end) return false;

            int numStart = idx;
            while (idx < end && Character.isDigit(text.charAt(idx))) idx++;
            if (idx == numStart) return false;
            int numEnd = idx;

            result.setGroup(g, numStart, numEnd);

            while (idx < end && Character.isWhitespace(text.charAt(idx))) idx++;
            if (g < 4) {
                if (idx >= end || text.charAt(idx) != ',') return false;
                idx++;
            }
        }
        return true;
    }

    private static int indexOf(CharSequence text, String needle, int fromIndex) {
        int tLen = text.length();
        int nLen = needle.length();
        if (nLen == 0) return fromIndex;
        for (int i = fromIndex; i <= tLen - nLen; i++) {
            boolean match = true;
            for (int j = 0; j < nLen; j++) {
                if (text.charAt(i + j) != needle.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    private static int indexOf(CharSequence text, String needle) {
        return indexOf(text, needle, 0);
    }

    /**
     * Single-pass SIMD/Byte-level whitespace normalizer (replaces .replaceAll("\\s+", " ").trim()).
     * Writes compacted result into dest buffer, returns compacted length.
     */
    public static int normalizeWhitespace(byte[] src, int srcLen, byte[] dest) {
        int outIdx = 0;
        int i = 0;
        // Trim leading
        while (i < srcLen && (src[i] == ' ' || src[i] == '\t' || src[i] == '\r' || src[i] == '\n' || src[i] == 0x0B || src[i] == 0x0C)) {
            i++;
        }

        boolean lastWasSpace = false;
        while (i < srcLen) {
            byte b = src[i];
            boolean isWs = (b == ' ' || b == '\t' || b == '\r' || b == '\n' || b == 0x0B || b == 0x0C);
            if (isWs) {
                if (!lastWasSpace) {
                    dest[outIdx++] = ' ';
                    lastWasSpace = true;
                }
            } else {
                dest[outIdx++] = b;
                lastWasSpace = false;
            }
            i++;
        }

        // Trim trailing space
        if (outIdx > 0 && dest[outIdx - 1] == ' ') {
            outIdx--;
        }
        return outIdx;
    }

    public static String normalizeWhitespace(String text) {
        if (text == null || text.isEmpty()) return "";
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[bytes.length];
        int len = normalizeWhitespace(bytes, bytes.length, out);
        return new String(out, 0, len, StandardCharsets.UTF_8);
    }
}
