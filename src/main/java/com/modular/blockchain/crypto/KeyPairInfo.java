package com.modular.blockchain.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Encapsulates a public/private key pair and provides methods to access the keys
 * in both raw format and Base64 encoded strings. This class is immutable and thread-safe.
 */
public class KeyPairInfo {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    /**
     * Creates a new KeyPairInfo instance from an existing KeyPair.
     *
     * @param keyPair The source KeyPair containing the public and private keys
     * @throws NullPointerException if keyPair is null
     */
    public KeyPairInfo(KeyPair keyPair) {
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * Returns the public key encoded as a Base64 string.
     * This format is suitable for transmission or storage.
     *
     * @return Base64 encoded string representation of the public key
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Returns the private key encoded as a Base64 string.
     * This format is suitable for transmission or storage.
     * Note: The private key should be handled securely and never exposed.
     *
     * @return Base64 encoded string representation of the private key
     */
    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Returns the raw public key object.
     *
     * @return The PublicKey object for use in cryptographic operations
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Returns the raw private key object.
     * Note: Handle with care - the private key should be kept secure.
     *
     * @return The PrivateKey object for use in cryptographic operations
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * Creates and returns a new KeyPair instance from the stored keys.
     *
     * @return A new KeyPair containing copies of the public and private keys
     */
    public KeyPair getKeyPair() {
        return new KeyPair(publicKey, privateKey);
    }
}