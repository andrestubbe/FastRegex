package fastregex.demo;

import fastregex.FastRegex;
import fastregex.MatchResult;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" ⚡ FastRegex — Zero-Allocation SIMD Pattern Scanner");
        System.out.println("=================================================");

        // 1. Fast Grounding Coordinate Pattern Matching
        String visionText = "Target Submit Button identified at [340, 520, 390, 710] on desktop.";
        FastRegex regex = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        MatchResult result = new MatchResult();

        if (regex.find(visionText, result)) {
            System.out.println("\n--- 1. Pattern Extraction ---");
            System.out.printf("Match Found: start=%d end=%d\n", result.start(), result.end());
            System.out.printf("Parsed Coordinates (Zero-Allocation): ymin=%d, xmin=%d, ymax=%d, xmax=%d\n",
                    result.parseGroupAsInt(visionText, 1),
                    result.parseGroupAsInt(visionText, 2),
                    result.parseGroupAsInt(visionText, 3),
                    result.parseGroupAsInt(visionText, 4));
        }

        // 2. High-Speed Single-Pass Whitespace Normalizer
        String rawMessyText = "  Title: \t Report \n\n Section 1: \t Detailed   analysis    summary.  ";
        String cleaned = FastRegex.normalizeWhitespace(rawMessyText);

        System.out.println("\n--- 2. Whitespace Normalization ---");
        System.out.println("Raw Input:      [" + rawMessyText + "]");
        System.out.println("Cleaned Output: [" + cleaned + "]");

        System.out.println("\n✔ FastRegex Pipeline Executed Successfully!");
    }
}
