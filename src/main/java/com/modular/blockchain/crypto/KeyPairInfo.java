package com.modular.blockchain.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Class that holds public/private key pair information and provides methods to access them
 * in both raw and Base64 encoded formats.
 */
public class KeyPairInfo {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    /**
     * Constructs a KeyPairInfo object from a KeyPair
     * @param keyPair The KeyPair containing public and private keys
     */
    public KeyPairInfo(KeyPair keyPair) {
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * Gets the public key encoded in Base64 format
     * @return Base64 encoded string representation of the public key
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Gets the private key encoded in Base64 format
     * @return Base64 encoded string representation of the private key
     */
    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Gets the raw public key
     * @return The PublicKey object
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Gets the raw private key
     * @return The PrivateKey object
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}