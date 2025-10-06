package com.modular.blockchain.wallet;

import com.modular.blockchain.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * WalletStore manages storage and retrieval of user wallets
 * Uses an in-memory HashMap to store wallet objects indexed by user ID
 */
public class WalletStore {
    /** Map storing user wallets with user ID as key */
    private final Map<String, Wallet> store = new HashMap<>();

    /**
     * Adds a wallet to the store
     * @param w The wallet to add
     */
    public void addWallet(Wallet w) {
        store.put(w.getUserId(), w);
        Logger.info("Wallet added to store for user: " + w.getUserId());
    }

    /**
     * Retrieves a wallet by user ID
     * @param userId The ID of the user whose wallet to retrieve
     * @return The wallet object if found, null otherwise
     */
    public Wallet getWallet(String userId) {
        Wallet wallet = store.get(userId);
        if (wallet != null) {
            Logger.debug("Wallet retrieved for user: " + userId);
        } else {
            Logger.error("Wallet not found for user: " + userId);
        }
        return wallet;
    }

    /**
     * Removes a wallet from the store
     * @param userId The ID of the user whose wallet to remove
     */
    public void removeWallet(String userId) {
        store.remove(userId);
        Logger.info("Wallet removed from store for user: " + userId);
    }
}