import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

public interface IDataSigner {

    /**
     * Signs the data with the given private key
     *
     * @param data       the data to be signed
     * @param privateKey the privateKey to be used when signing
     * @return the signature as byte array
     */
    byte[] sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException, SignatureException;

    /**
     * Verifies the data's signature given a public key
     *
     * @param data          the data to be verified
     * @param signatureHash the signature of the data
     * @param publicKey     the public key to verify with
     * @return true if the signatureHash matches the signature of the data
     */
    boolean verify(byte[] data, byte[] signatureHash, PublicKey publicKey) throws InvalidKeyException, SignatureException;
}
