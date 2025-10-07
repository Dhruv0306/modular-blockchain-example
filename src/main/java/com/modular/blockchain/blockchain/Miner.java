package com.modular.blockchain.blockchain;

import com.modular.blockchain.consensus.ConsensusEngine;
import com.modular.blockchain.consensus.ConsensusResult;
import com.modular.blockchain.transaction.Transaction;
import com.modular.blockchain.transaction.TransactionPool;
import com.modular.blockchain.util.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A Miner node in the blockchain network that creates new blocks by validating and combining transactions.
 * The miner periodically checks the transaction pool for pending transactions and creates new blocks when
 * enough valid transactions are available. Each mined block must pass consensus validation before being
 * added to the blockchain.
 */
public class Miner {
    private final TransactionPool pool;
    private final Blockchain blockchain;
    private final ConsensusEngine consensusEngine;
    private final String minerId;
    private final int miningThreshold;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new Miner instance with the specified configuration.
     *
     * @param minerId Unique identifier for this miner node
     * @param miningThreshold Minimum number of transactions needed before attempting to create a block
     * @param pool Transaction pool containing pending transactions waiting to be mined
     * @param blockchain Reference to the main blockchain that stores all validated blocks
     * @param consensusEngine Consensus mechanism used to validate new blocks before adding to chain
     */
    public Miner(String minerId, int miningThreshold, TransactionPool pool, Blockchain blockchain, ConsensusEngine consensusEngine) {
        Logger.info("Miner created: " + minerId);
        this.minerId = minerId;
        this.miningThreshold = miningThreshold;
        this.pool = pool;
        this.blockchain = blockchain;
        this.consensusEngine = consensusEngine;
    }

    /**
     * Initiates the automated mining process on a fixed schedule.
     * The miner will wake up at the specified interval to check for new transactions
     * and attempt to create a block if enough transactions are available.
     *
     * @param minutes Time interval between mining attempts in minutes
     */
    public void startMining(int minutes) {
        Logger.info("Miner " + minerId + " started mining with interval: " + minutes + " minutes");
        service.scheduleAtFixedRate(this::checkAndMine, 0, minutes, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * Terminates the automated mining process.
     * This will stop the miner from creating any new blocks.
     */
    public void stopMining() {
        Logger.info("Miner " + minerId + " stopped mining");
        service.shutdownNow();
    }

    /**
     * Core mining logic that attempts to create and validate a new block.
     * This method:
     * 1. Checks transaction pool for available transactions
     * 2. Creates a new block if enough transactions exist
     * 3. Mines the block by finding a valid proof-of-work
     * 4. Validates the block through consensus
     * 5. Adds valid block to blockchain or returns transactions to pool on failure
     */
    private void checkAndMine() {
        try {
            Logger.debug("Miner " + minerId + " checking transaction pool");
            List<Transaction> batch = pool.getBatch(miningThreshold);
            if (batch.size() >= miningThreshold) {
                Logger.info("Miner " + minerId + " found " + batch.size() + " transactions, mining new block");
                Block newBlock = new Block(blockchain.getChain().size(), System.currentTimeMillis(), batch, blockchain.getLatestBlock().getHash(), minerId);
                newBlock.mineBlock(blockchain.getDifficulty());
                ConsensusResult result = consensusEngine.validateBlock(newBlock, blockchain);
                if (result.isSuccess()) {
                    blockchain.addBlock(newBlock);
                    Logger.info("Miner " + minerId + " successfully mined and added a new block: " + newBlock.getHash());
                    Logger.debug("Transactions added to new block by miner " + minerId + ": " + batch.stream().map(Transaction::getId).toList());
                } else {
                    Logger.error("Consensus failed for new block by miner " + minerId + ": " + result.getMessage());
                    pool.addBack((java.util.ArrayList<Transaction>) batch);
                }
            } else {
                Logger.debug("Miner " + minerId + " found insufficient transactions to mine a block");
            }
        } catch (Exception e) {
            Logger.error("Error during mining by miner " + minerId + ": " + e.getMessage());
        }
    }

    /**
     * Returns the unique identifier for this miner node.
     *
     * @return The miner's ID string
     */
    public String getMinerId() {
        return minerId;
    }
}