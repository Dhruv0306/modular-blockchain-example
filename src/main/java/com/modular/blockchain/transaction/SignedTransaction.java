package com.modular.blockchain.transaction;

import com.modular.blockchain.crypto.CryptoUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Represents a signed transaction in the blockchain.
 * Contains transaction details and cryptographic signature for verification.
 */
public class SignedTransaction implements Transaction {
    private final String id;                    // Unique transaction identifier
    private final String senderPublicKey;       // Base64 encoded public key of sender
    private final String receiverPublicKey;     // Base64 encoded public key of receiver
    private final double amount;                // Transaction amount
    private String signature;                   // Base64 encoded cryptographic signature

    /**
     * Creates a new signed transaction with the specified parameters.
     *
     * @param id Unique transaction identifier
     * @param senderPublicKey Base64 encoded public key of the sender
     * @param receiverPublicKey Base64 encoded public key of the receiver
     * @param amount Transaction amount
     */
    public SignedTransaction(String id, String senderPublicKey, String receiverPublicKey, double amount) {
        this.id = id;
        this.senderPublicKey = senderPublicKey;
        this.receiverPublicKey = receiverPublicKey;
        this.amount = amount;
    }

    /**
     * Signs the transaction using the sender's private key.
     * Concatenates transaction data and generates a cryptographic signature.
     *
     * @param privateKey The sender's private key used for signing
     */
    public void sign(PrivateKey privateKey) {
        String data = id + senderPublicKey + receiverPublicKey + amount;
        byte[] sig = CryptoUtils.sign(data.getBytes(), privateKey);
        this.signature = Base64.getEncoder().encodeToString(sig);
    }

    /**
     * Verifies the transaction signature using the sender's public key.
     *
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature() {
        try {
            PublicKey pubKey = CryptoUtils.decodePublicKey(senderPublicKey);
            String data = id + senderPublicKey + receiverPublicKey + amount;
            byte[] sig = Base64.getDecoder().decode(signature);
            return CryptoUtils.verify(data.getBytes(), sig, pubKey);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean isValid() { return verifySignature(); }

    /**
     * Converts the transaction to a JSON string representation.
     *
     * @return JSON formatted string containing transaction details
     */
    @Override
    public String toJson() {
        return String.format("{\"id\":\"%s\",\"sender\":\"%s\",\"receiver\":\"%s\",\"amount\":%f,\"signature\":\"%s\"}",
                id, senderPublicKey, receiverPublicKey, amount, signature);
    }

    // Getters and setters
    public String getSenderPublicKey() { return senderPublicKey; }
    public String getReceiverPublicKey() { return receiverPublicKey; }
    public double getAmount() { return amount; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}