package fastregex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FastRegexTest {

    @Test
    public void testCoordinateBoxPattern() {
        FastRegex regex = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        MatchResult result = new MatchResult();

        String input = "The target is located at [120, 250, 160, 750] on screen.";
        boolean found = regex.find(input, result);

        assertTrue(found);
        assertEquals(5, result.groupCount());
        assertEquals(120, result.parseGroupAsInt(input, 1));
        assertEquals(250, result.parseGroupAsInt(input, 2));
        assertEquals(160, result.parseGroupAsInt(input, 3));
        assertEquals(750, result.parseGroupAsInt(input, 4));
    }

    @Test
    public void testXmlBoxPattern() {
        FastRegex regex = FastRegex.compile("<box>\\((\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\)</box>");
        MatchResult result = new MatchResult();

        String input = "Found: <box>(450, 300, 520, 700)</box>";
        boolean found = regex.find(input, result);

        assertTrue(found);
        assertEquals(450, result.parseGroupAsInt(input, 1));
        assertEquals(300, result.parseGroupAsInt(input, 2));
        assertEquals(520, result.parseGroupAsInt(input, 3));
        assertEquals(700, result.parseGroupAsInt(input, 4));
    }

    @Test
    public void testNormalizeWhitespace() {
        String raw = "  Hello \t \n\n world!   Multiple   spaces \t here.  ";
        String normalized = FastRegex.normalizeWhitespace(raw);
        assertEquals("Hello world! Multiple spaces here.", normalized);
    }
}
