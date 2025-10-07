package com.modular.blockchain.wallet;

import com.modular.blockchain.crypto.CryptoUtils;
import com.modular.blockchain.crypto.KeyPairInfo;
import com.modular.blockchain.transaction.SignedTransaction;
import com.modular.blockchain.transaction.Transaction;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.UUID;

/**
 * A simple cryptocurrency wallet implementation that manages keys and can sign transactions
 */
public class SimpleWallet extends Wallet {
    /**
     * Creates a new wallet with the given user ID and generates a new key pair
     * @param userId The unique identifier for this wallet's owner
     */
    public SimpleWallet(String userId) {
        super(userId, new KeyPairInfo(CryptoUtils.generateKeyPair()));
    }

    /**
     * Gets the wallet's public key encoded in Base64 format
     * @return Base64 encoded public key string
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keys.getKeyPair().getPublic().getEncoded());
    }

    /**
     * Creates and signs a new transaction to transfer funds to a receiver
     * @param receiverPublicKey The receiver's public key in Base64 format
     * @param amount The amount to transfer
     * @return A signed transaction ready to be submitted to the network
     */
    public SignedTransaction createTransaction(String receiverPublicKey, double amount) {
        String id = UUID.randomUUID().toString();
        SignedTransaction tx = new SignedTransaction(id, getPublicKeyBase64(), receiverPublicKey, amount);
        tx.sign(keys.getKeyPair().getPrivate());
        return tx;
    }

    /**
     * Signs transaction data with the wallet's private key
     * @param tx The transaction to sign
     * @return Signature bytes
     * @throws IllegalArgumentException if transaction type is not supported
     */
    @Override
    public byte[] sign(Transaction tx) {
        if (tx instanceof SignedTransaction) {
            // Concatenate transaction fields to create signing data
            String data = tx.getId() + ((SignedTransaction)tx).getSenderPublicKey() + ((SignedTransaction)tx).getReceiverPublicKey() + ((SignedTransaction)tx).getAmount();
            return CryptoUtils.sign(data.getBytes(), keys.getKeyPair().getPrivate());
        }
        throw new IllegalArgumentException("Unsupported transaction type");
    }

    /**
     * Gets the wallet's address, which is the Base64-encoded public key in this implementation
     * @return The wallet's address as a Base64 string
     */
    @Override
    public String getAddress() {
        return getPublicKeyBase64();
    }

    /**
     * Signs a transaction using the wallet's private key
     * @param tx The transaction to sign
     * @return The signed transaction
     * @throws IllegalArgumentException if transaction type is not supported
     */
    @Override
    public Transaction signTransaction(Transaction tx) {
        if (tx instanceof SignedTransaction) {
            ((SignedTransaction) tx).sign(keys.getKeyPair().getPrivate());
            return tx;
        }
        throw new IllegalArgumentException("Unsupported transaction type");
    }
}