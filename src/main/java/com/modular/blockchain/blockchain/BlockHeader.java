package com.modular.blockchain.blockchain;

import com.modular.blockchain.util.Logger;

/**
 * Represents the header of a block in the blockchain.
 * Contains essential metadata about the block including its position, timestamp,
 * previous block reference, merkle root hash, proof-of-work nonce and miner ID.
 */
public class BlockHeader {
    /** Index/height of this block in the chain */
    private final int index;
    /** Timestamp when this block was created */
    private final long timestamp;
    /** Hash of the previous block in the chain */
    private final String previousHash;
    /** Merkle root hash of all transactions in this block */
    private final String merkleRoot;
    /** Proof-of-work nonce value */
    private int nonce;
    /** ID of the miner who created this block */
    private final String minerId;

    /**
     * Creates a new block header with the specified parameters.
     *
     * @param index The index/height of this block in the chain
     * @param timestamp The timestamp when this block was created
     * @param previousHash Hash of the previous block
     * @param merkleRoot Merkle root hash of transactions
     * @param nonce Initial proof-of-work nonce value
     * @param minerId ID of the miner who created this block
     */
    public BlockHeader(int index, long timestamp, String previousHash, String merkleRoot, int nonce, String minerId) {
        Logger.debug("Creating BlockHeader: index=" + index + ", minerId=" + minerId);
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
        this.minerId = minerId;
    }

    /**
     * Sets a new nonce value for this block header.
     * Used during the mining process to find a valid proof-of-work.
     *
     * @param nonce The new nonce value
     */
    public void setNonce(int nonce) {
        Logger.debug("BlockHeader nonce updated: index=" + index + ", new nonce=" + nonce);
        this.nonce = nonce;
    }

    /**
     * Returns a string representation of this block header,
     * concatenating all fields in sequence.
     *
     * @return String containing all block header fields
     */
    @Override
    public String toString() {
        return index + timestamp + previousHash + merkleRoot + nonce + minerId;
    }
}