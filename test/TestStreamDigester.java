import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 07/01/2017.
 */
public class TestStreamDigester {

    private static final String defaultStreamMessage = "mock message";
    private static InputStream inputStream;

    private static MessageDigest mockMessageDigest;
    private static StreamDigester streamDigesterUnderTest;

    @Before
    public void initialize() {
        mockMessageDigest = mock(MessageDigest.class);
        inputStream = new ByteArrayInputStream(defaultStreamMessage.getBytes());

        streamDigesterUnderTest = new StreamDigester(mockMessageDigest);
    }

    @Test
    public void testDigestStream() throws IOException {
        streamDigesterUnderTest.digestStream(inputStream);

        verify(mockMessageDigest, times(1)).reset();
        verify(mockMessageDigest, times(1)).update(any(byte[].class), eq(0), eq(defaultStreamMessage.getBytes().length));
        verify(mockMessageDigest, times(1)).digest();
    }

    @Test
    public void testDigestStreamOnLargeStream() throws IOException {
        int fiftyKBs = 50 * 1024;
        int times = fiftyKBs / defaultStreamMessage.length();

        InputStream inputStream = new ByteArrayInputStream(String.join("", Collections.nCopies(times, defaultStreamMessage)).getBytes());

        streamDigesterUnderTest.digestStream(inputStream);

        verify(mockMessageDigest, times(49)).update(any(byte[].class), eq(0), eq(1024));
        verify(mockMessageDigest, times(1)).update(any(byte[].class), eq(0), eq(times * defaultStreamMessage.length() - 49 * 1024));
        verify(mockMessageDigest, times(1)).digest();
    }
}
