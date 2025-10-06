/**
 * Represents a blockchain implementation with mining difficulty and block management.
 */
package com.modular.blockchain.blockchain;

import com.modular.blockchain.transaction.Transaction;
import com.modular.blockchain.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final List<Block> blocks;
    private final int difficulty;

    /**
     * Creates a new blockchain with specified mining difficulty.
     * Initializes the chain with a genesis block.
     *
     * @param difficulty The mining difficulty level
     */
    public Blockchain(int difficulty){
        Logger.info("Initializing blockchain with difficulty: " + difficulty);
        this.blocks = new ArrayList<Block>();
        this.difficulty = difficulty;
        // Create genesis block
        Block genesisBlock = genesisBlock();
        genesisBlock.mineBlock(difficulty);
        blocks.add(genesisBlock);
        Logger.info("Genesis block created and added to blockchain");
    }

    /**
     * Gets the most recently added block in the chain.
     *
     * @return The latest Block object
     */
    Block getLatestBlock() {
        return blocks.getLast();
    }

    /**
     * Adds a new block to the chain after mining it.
     * Only adds the block if its hash is valid for the current difficulty.
     *
     * @param block The Block to be added
     */
    void addBlock(Block block) {
        Logger.info("Attempting to add new block to blockchain");
        block.mineBlock(difficulty);

        if (BlockUtils.isHashValid(block.getHash(), difficulty)) {
            blocks.add(block);
            Logger.info("Block added to blockchain: " + block.getHash());
        } else {
            Logger.error("Block hash invalid, block not added: " + block.getHash());
        }
    }

    /**
     * Validates the entire blockchain.
     * Checks hash integrity and block linking.
     *
     * @return true if chain is valid, false otherwise
     */
    private boolean isChainValid() {
        Logger.info("Validating blockchain integrity");
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            if (!currentBlock.getHash().equals(BlockUtils.calculateHash(currentBlock.getHeader(), currentBlock.getTransactions()))) {
                Logger.error("Block hash mismatch at index " + i);
                return false;
            }

            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                Logger.error("Block previous hash mismatch at index " + i);
                return false;
            }
        }
        Logger.info("Blockchain is valid");
        return true;
    }

    /**
     * Creates and returns the genesis block for the blockchain.
     * The genesis block is the first block in the chain with no previous hash.
     *
     * @return The genesis Block object
     */
    public Block genesisBlock() {
        Logger.info("Creating genesis block");
        // Example values, adjust as needed for your Block constructor
        String genesisPreviousHash = "0";
        String genesisData = "Genesis Block";
        long timestamp = System.currentTimeMillis();
        int nonce = 0;
        List<Transaction> emptyTransactions = List.of();

        BlockHeader header = new BlockHeader(0, timestamp,genesisPreviousHash, "", nonce, "genesis");
        return new Block(0, timestamp, emptyTransactions, genesisPreviousHash, "genesis");
    }

    /**
     * Returns a copy of the blockchain.
     *
     * @return A new ArrayList containing all blocks in the chain
     */
    public List<Block> getChain() {
        return new ArrayList<>(blocks);
    }

    /**
     * @return The current mining difficulty level
     */
    public int getDifficulty() { return difficulty; }
}