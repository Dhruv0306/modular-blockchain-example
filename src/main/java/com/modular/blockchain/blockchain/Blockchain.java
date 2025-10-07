/**
 * Represents a blockchain implementation with configurable mining difficulty and block management.
 * This class maintains an ordered list of blocks and ensures chain integrity through hash validation.
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
     * Initializes the chain with a genesis block and configures mining parameters.
     *
     * @param difficulty The mining difficulty level - higher values require more computational work
     */
    public Blockchain(int difficulty){
        Logger.info("Initializing blockchain with difficulty: " + difficulty);
        this.blocks = new ArrayList<>();
        this.difficulty = difficulty;
        // Initialize chain with genesis block
        Block genesisBlock = genesisBlock();
        genesisBlock.mineBlock(difficulty);
        blocks.add(genesisBlock);
        Logger.info("Genesis block created and added to blockchain");
    }

    /**
     * Gets the most recently added block in the chain.
     *
     * @return The latest Block object in the blockchain
     */
    Block getLatestBlock() {
        return blocks.getLast();
    }

    /**
     * Adds a new block to the chain after validating its hash.
     * Only adds blocks that meet the current difficulty requirement.
     *
     * @param block The Block to be added to the blockchain
     */
    void addBlock(Block block) {
        Logger.info("Attempting to add new block to blockchain");
        if (BlockUtils.isHashValid(block.getHash(), difficulty)) {
            blocks.add(block);
            Logger.info("Block added to blockchain: " + block.getHash());
        } else {
            Logger.error("Block hash invalid, block not added: " + block.getHash());
        }
    }

    /**
     * Validates the entire blockchain by checking:
     * 1. Hash integrity of each block
     * 2. Proper linking between consecutive blocks
     *
     * @return true if the entire chain is valid, false if any validation fails
     */
    public boolean isChainValid() {
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
     * The genesis block is the first block in the chain with special properties:
     * - Has no previous hash (uses "0")
     * - Contains no transactions
     * - Marks the start of the blockchain
     *
     * @return The genesis Block object with initial configuration
     */
    public Block genesisBlock() {
        Logger.info("Creating genesis block");
        // Initialize genesis block with default values
        String genesisPreviousHash = "0";
        long timestamp = System.currentTimeMillis();
        int nonce = 0;
        List<Transaction> emptyTransactions = List.of();

        BlockHeader header = new BlockHeader(0, timestamp,genesisPreviousHash, "", nonce, "genesis");
        return new Block(0, timestamp, emptyTransactions, genesisPreviousHash, "genesis");
    }

    /**
     * Returns a defensive copy of the blockchain.
     * Creates a new list to prevent external modification of the chain.
     *
     * @return A new ArrayList containing all blocks in the chain
     */
    public List<Block> getChain() {
        return new ArrayList<>(blocks);
    }

    /**
     * Returns the mining difficulty level of the blockchain.
     * Higher difficulty requires more computational work to mine blocks.
     *
     * @return The current mining difficulty level
     */
    public int getDifficulty() { return difficulty; }
}