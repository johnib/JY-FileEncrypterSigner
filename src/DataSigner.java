import java.security.*;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
@SuppressWarnings("WeakerAccess")
class DataSigner {

    private final Signature signature;

    /**
     * Initializes a new DataSigner instance
     *
     * @param sign the Signature instance of your choice
     */
    public DataSigner(Signature sign) {
        this.signature = sign;
    }

    /**
     * Signs the data with the given private key
     *
     * @param data       the data to be signed
     * @param privateKey the privateKey to be used when signing
     * @return the signature as byte array
     * @throws InvalidKeyException for privateKey issues
     * @throws SignatureException  for Signing issues
     */
    byte[] sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        signature.initSign(privateKey);
        signature.update(data);

        return signature.sign();
    }

    /**
     * Signs the data with the given private key
     *
     * @param data       the data to be signed
     * @param privateKey the privateKey to be used when signing
     * @return the signature as byte array
     * @throws InvalidKeyException for privateKey issues
     * @throws SignatureException  for Signing issues
     */
    byte[] sign(String data, PrivateKey privateKey) throws SignatureException, InvalidKeyException {
        return sign(data.getBytes(), privateKey);
    }

    /**
     * Verifies the data's signature given a public key
     *
     * @param data          the data to be verified
     * @param signatureHash the signature of the data
     * @param publicKey     the public key to verify with
     * @return true if the signatureHash matches the signature of the data
     * @throws InvalidKeyException for publicKey issues
     * @throws SignatureException  for Signing issues¬
     */
    boolean verify(byte[] data, byte[] signatureHash, PublicKey publicKey) throws InvalidKeyException, SignatureException {
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(signatureHash);
    }

    /**
     * Verifies the data's signature given a public key
     *
     * @param data          the data to be verified
     * @param signatureHash the signature of the data
     * @param publicKey     the public key to verify with
     * @return true if the signatureHash matches the signature of the data
     * @throws InvalidKeyException for publicKey issues
     * @throws SignatureException  for Signing issues¬
     */
    boolean verify(String data, byte[] signatureHash, PublicKey publicKey) throws SignatureException, InvalidKeyException {
        return verify(data.getBytes(), signatureHash, publicKey);
    }
}
