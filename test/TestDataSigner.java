import org.junit.Before;
import org.junit.Test;

import java.security.*;

import static org.mockito.Mockito.*;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 07/01/2017.
 */
public class TestDataSigner {

    private static final String defaultStringToHash = "This is a mock string to be hashed";
    private static final String defaultSignatureHash = "This is mock signature";
    private static PrivateKey mockPrivateKey;
    private static PublicKey mockPublicKey;
    private static Signature mockSignature;

    private static DataSigner dataSignerUnderTest;

    @Before
    public void initialize() {
        mockPrivateKey = mock(PrivateKey.class);
        mockPublicKey = mock(PublicKey.class);
        mockSignature = mock(Signature.class);

        dataSignerUnderTest = new DataSigner(mockSignature);
    }

    @Test
    public void testSign() throws SignatureException, InvalidKeyException {
        dataSignerUnderTest.sign(defaultStringToHash, mockPrivateKey);

        verify(mockSignature, times(1)).initSign(mockPrivateKey);
        verify(mockSignature, times(1)).update(defaultStringToHash.getBytes());
        verify(mockSignature, times(1)).sign();
    }

    @Test
    public void testSignWithString() throws SignatureException, InvalidKeyException {
        dataSignerUnderTest.sign(defaultStringToHash, mockPrivateKey);

        verify(mockSignature, times(1)).initSign(mockPrivateKey);
        verify(mockSignature, times(1)).update(defaultStringToHash.getBytes());
        verify(mockSignature, times(1)).sign();
    }

    @Test
    public void testVerify() throws SignatureException, InvalidKeyException {
        dataSignerUnderTest.verify(defaultStringToHash.getBytes(), defaultSignatureHash.getBytes(), mockPublicKey);

        verify(mockSignature, times(1)).initVerify(mockPublicKey);
        verify(mockSignature, times(1)).update(defaultStringToHash.getBytes());
        verify(mockSignature, times(1)).verify(defaultSignatureHash.getBytes());
    }

    @Test
    public void testVerifyString() throws SignatureException, InvalidKeyException {
        dataSignerUnderTest.verify(defaultStringToHash, defaultSignatureHash.getBytes(), mockPublicKey);

        verify(mockSignature, times(1)).initVerify(mockPublicKey);
        verify(mockSignature, times(1)).update(defaultStringToHash.getBytes());
        verify(mockSignature, times(1)).verify(defaultSignatureHash.getBytes());
    }
}
