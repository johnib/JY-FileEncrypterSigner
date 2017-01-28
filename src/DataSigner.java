import java.security.*;

@SuppressWarnings("WeakerAccess")
public class DataSigner implements IDataSigner {

    private final Signature signature;

    /**
     * Initializes a new DataSigner instance
     *
     * @param signature the Signature instance of your choice
     */
    public DataSigner(Signature signature) {
        this.signature = signature;
    }

    /**
     * Initializes a new DataSigner instance
     *
     * @param algorithm the requested Signature algorithm
     * @throws NoSuchAlgorithmException in case algorithm not found
     */
    public DataSigner(String algorithm) throws NoSuchAlgorithmException {
        this(Signature.getInstance(algorithm));
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
    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
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
    public byte[] sign(String data, PrivateKey privateKey) throws SignatureException, InvalidKeyException {
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
    @Override
    public boolean verify(byte[] data, byte[] signatureHash, PublicKey publicKey) throws InvalidKeyException, SignatureException {
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
    public boolean verify(String data, byte[] signatureHash, PublicKey publicKey) throws SignatureException, InvalidKeyException {
        return verify(data.getBytes(), signatureHash, publicKey);
    }
}
