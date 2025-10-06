package com.modular.blockchain.crypto;

import com.modular.blockchain.util.Logger;
import java.security.*;
import java.util.Base64;

/**
 * Utility class providing cryptographic functions for blockchain operations.
 * Includes methods for hashing, key pair generation, signing and verification.
 */
public class CryptoUtils {
    /**
     * Generates SHA-256 hash of input string.
     * @param input String to be hashed
     * @return Hexadecimal string representation of hash
     * @throws RuntimeException if hashing fails
     */
    public static String sha256(String input) {
        try {
            Logger.debug("Hashing input with SHA-256");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Logger.error("SHA-256 hashing failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a new RSA key pair.
     * @return KeyPair containing public and private RSA keys
     * @throws RuntimeException if key generation fails
     */
    public static KeyPair generateKeyPair() {
        try {
            Logger.info("Generating new RSA key pair");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            Logger.error("Key pair generation failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Signs data using RSA private key.
     * @param data Data to be signed
     * @param key Private key for signing
     * @return Signature bytes
     * @throws RuntimeException if signing fails
     */
    public static byte[] sign(byte[] data, PrivateKey key) {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(key);
            signer.update(data);
            return signer.sign();
        } catch (Exception e) {
            Logger.error("Signing data failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a signature using RSA public key.
     * @param data Original data that was signed
     * @param signature Signature to verify
     * @param key Public key for verification
     * @return true if signature is valid, false otherwise
     * @throws RuntimeException if verification fails
     */
    public static boolean verify(byte[] data, byte[] signature, PublicKey key) {
        try {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(key);
            verifier.update(data);
            return verifier.verify(signature);
        } catch (Exception e) {
            Logger.error("Verification of signature failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}