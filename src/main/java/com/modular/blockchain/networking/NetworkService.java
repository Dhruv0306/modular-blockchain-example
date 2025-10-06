package com.modular.blockchain.networking;

import com.modular.blockchain.blockchain.Block;
import com.modular.blockchain.transaction.Transaction;
import com.modular.blockchain.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing network operations in the blockchain.
 * Handles peer connections and broadcasting of blocks and transactions.
 */
public class NetworkService {
    /** List of connected peers in the network */
    private final List<Peer> peers = new ArrayList<>();

    /**
     * Broadcasts a new block to all connected peers in the network.
     * @param block The block to broadcast
     */
    public void broadcastBlock(Block block) {
        Logger.info("[NetworkService] Broadcasting block: " + block.getHash());
    }

    /**
     * Broadcasts a new transaction to all connected peers in the network.
     * @param tx The transaction to broadcast
     */
    public void broadcastTransaction(Transaction tx) {
        Logger.info("[NetworkService] Broadcasting transaction: " + tx.getId());
    }

    /**
     * Adds a new peer to the network.
     * @param peer The peer to add to the network
     */
    public void addPeer(Peer peer) {
        peers.add(peer);
        Logger.info("[NetworkService] Added peer: " + peer.getId());
    }

    /**
     * Gets the list of all connected peers.
     * @return List of connected peers
     */
    public List<Peer> getPeers() {
        return peers;
    }

    /**
     * Removes a peer from the network.
     * @param peer The peer to remove
     */
    public void removePeer(Peer peer) {
        peers.remove(peer);
        Logger.info("[NetworkService] Removed peer: " + peer.getId());
    }
}