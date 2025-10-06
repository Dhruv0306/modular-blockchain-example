/**
 * Abstract base class representing a blockchain wallet.
 * Provides core wallet functionality including user identification and key management.
 */
package com.modular.blockchain.wallet;

import com.modular.blockchain.crypto.KeyPairInfo;
import com.modular.blockchain.transaction.Transaction;
import com.modular.blockchain.util.Logger;

public abstract class Wallet {
    /** User identifier associated with this wallet */
    protected final String userId;

    /** Cryptographic key pair information for this wallet */
    protected final KeyPairInfo keys;

    /**
     * Creates a new wallet instance
     * @param userId Unique identifier for the wallet owner
     * @param keys Cryptographic key pair information
     */
    public Wallet(String userId, KeyPairInfo keys) {
        Logger.info("Wallet created for user: " + userId);
        this.userId = userId;
        this.keys = keys;
    }

    /**
     * Gets the user ID associated with this wallet
     * @return The wallet's user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the key pair information for this wallet
     * @return The wallet's cryptographic key pair info
     */
    public KeyPairInfo getKeys() {
        return keys;
    }

    /**
     * Signs a transaction using the wallet's private key
     * @param tx Transaction to sign
     * @return Signature as a byte array
     */
    public abstract byte[] sign(Transaction tx);

    /**
     * Gets the public blockchain address for this wallet
     * @return The wallet's public address as a string
     */
    public abstract String getAddress();

    /**
     * Signs a transaction and returns the signed transaction object
     * @param tx Transaction to sign
     * @return The signed transaction
     */
    public abstract Transaction signTransaction(Transaction tx);
}