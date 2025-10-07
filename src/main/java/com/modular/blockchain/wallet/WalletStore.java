package com.modular.blockchain.wallet;

import com.modular.blockchain.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * WalletStore provides persistent storage and management of user cryptocurrency wallets.
 * Implements an in-memory HashMap data structure to store wallet objects, using unique user IDs as keys.
 * Provides methods for adding, retrieving, removing and listing wallets.
 */
public class WalletStore {
    /** In-memory map that stores wallet objects indexed by their associated user ID */
    private final Map<String, Wallet> store = new HashMap<>();

    /**
     * Adds a new wallet to the store.
     * If a wallet already exists for the user ID, it will be overwritten.
     * @param w The wallet object to store
     */
    public void addWallet(Wallet w) {
        store.put(w.getUserId(), w);
        Logger.info("Wallet added to store for user: " + w.getUserId());
    }

    /**
     * Retrieves a wallet from the store by user ID.
     * @param userId The unique identifier of the user whose wallet to retrieve
     * @return The wallet object if found in the store, null if no wallet exists for the user ID
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
     * Removes a wallet from the store.
     * If no wallet exists for the user ID, this operation has no effect.
     * @param userId The unique identifier of the user whose wallet to remove
     */
    public void removeWallet(String userId) {
        store.remove(userId);
        Logger.info("Wallet removed from store for user: " + userId);
    }

    /**
     * Returns an Iterable containing all user IDs that have wallets in the store.
     * @return Iterable of user ID strings
     */
    public Iterable<String> getAllUserIds() {
        return store.keySet();
    }
}