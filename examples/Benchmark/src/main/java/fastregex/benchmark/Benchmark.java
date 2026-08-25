package fastregex.benchmark;

import fastregex.FastRegex;
import fastregex.MatchResult;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private String sampleVisionOutput;
    private String sampleWhitespaceText;
    private Pattern javaVisionPattern;
    private FastRegex fastVisionRegex;
    private MatchResult fastResult;

    @Setup
    public void setup() {
        sampleVisionOutput = "Detected UI element at [120, 250, 160, 750] with high confidence.";
        sampleWhitespaceText = "  Heading 1 \t \n\n Section A   content text here with   multiple \t whitespace characters.  ";
        javaVisionPattern = Pattern.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        fastVisionRegex = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        fastResult = new MatchResult();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public int benchmarkJavaStandardRegex() {
        Matcher m = javaVisionPattern.matcher(sampleVisionOutput);
        if (m.find()) {
            return Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2));
        }
        return 0;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public int benchmarkFastRegexZeroAlloc() {
        if (fastVisionRegex.find(sampleVisionOutput, fastResult)) {
            return fastResult.parseGroupAsInt(sampleVisionOutput, 1) + fastResult.parseGroupAsInt(sampleVisionOutput, 2);
        }
        return 0;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkJavaStandardReplaceAll() {
        return sampleWhitespaceText.replaceAll("[\\t\\f\\v]+", " ").replaceAll(" +", " ").trim();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkFastRegexNormalizeWhitespace() {
        return FastRegex.normalizeWhitespace(sampleWhitespaceText);
    }
}
