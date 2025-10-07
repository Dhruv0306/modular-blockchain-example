package com.modular.blockchain.crypto;

import com.modular.blockchain.util.Logger;
import java.security.*;
import java.util.Base64;

/**
 * Utility class providing cryptographic functions for blockchain operations.
 * Includes methods for secure hashing, asymmetric key pair generation, digital signatures and key encoding.
 * Uses SHA-256 for hashing and RSA for public key cryptography operations.
 */
public class CryptoUtils {
    /**
     * Generates SHA-256 cryptographic hash of input string.
     * @param input String data to be hashed
     * @return 64-character hexadecimal string representation of the SHA-256 hash
     * @throws RuntimeException if the hashing operation fails
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
     * Generates a new RSA public/private key pair for asymmetric cryptography.
     * Uses 2048-bit key size for strong security.
     * @return KeyPair containing matching RSA public and private keys
     * @throws RuntimeException if key pair generation fails
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
     * Creates a digital signature for data using RSA private key.
     * Uses SHA256withRSA algorithm for signing.
     * @param data The byte array to be signed
     * @param privateKey RSA private key used for signing
     * @return Byte array containing the digital signature
     * @throws RuntimeException if signing operation fails
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            Logger.error("Signing failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a digital signature using RSA public key.
     * Uses SHA256withRSA algorithm for verification.
     * @param data Original data that was signed
     * @param signatureBytes Digital signature to verify
     * @param publicKey RSA public key corresponding to the private key used for signing
     * @return true if signature is valid, false if invalid or verification fails
     */
    public static boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            Logger.error("Verification failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Decodes a Base64-encoded RSA public key string into a PublicKey object.
     * Expects X.509 encoded key format.
     * @param base64 Base64 encoded string representation of public key
     * @return Decoded RSA PublicKey object
     * @throws RuntimeException if decoding or key generation fails
     */
    public static PublicKey decodePublicKey(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            Logger.error("Public key decoding failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}