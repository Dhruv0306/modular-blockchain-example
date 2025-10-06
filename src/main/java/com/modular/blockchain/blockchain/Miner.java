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
 * Represents a miner in the blockchain network that is responsible for creating new blocks
 * by validating and combining transactions from the transaction pool.
 */
public class Miner {
    private final TransactionPool pool;
    private final Blockchain blockchain;
    private final ConsensusEngine consensusEngine;
    private final String minerId;
    private final int miningThreshold;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a new Miner instance.
     *
     * @param minerId Unique identifier for this miner
     * @param miningThreshold Minimum number of transactions required to create a block
     * @param pool Transaction pool to get pending transactions from
     * @param blockchain Reference to the blockchain
     * @param consensusEngine Engine used to validate blocks and achieve consensus
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
     * Starts the mining process, which periodically checks the transaction pool
     * and attempts to mine new blocks if enough transactions are available.
     *
     * @param minutes Interval in minutes to check the transaction pool
     */
    public void startMining(int minutes) {
        Logger.info("Miner " + minerId + " started mining with interval: " + minutes + " minutes");
        service.scheduleAtFixedRate(this::checkAndMine, 0, minutes, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * Stops the mining process.
     */
    public void stopMining() {
        Logger.info("Miner " + minerId + " stopped mining");
        service.shutdownNow();
    }

    /**
     * Checks the transaction pool and mines a new block if enough transactions are available.
     */
    private void checkAndMine() {
        try {
            Logger.debug("Miner " + minerId + " checking transaction pool");
            List<Transaction> batch = pool.getBatch(miningThreshold);
            if (batch.size() >= miningThreshold) {
                Logger.info("Miner " + minerId + " found " + batch.size() + " transactions, mining new block");
                Block newBlock = new Block(blockchain.getChain().size(), System.currentTimeMillis(), batch, blockchain.getChain().getLast().getHash(), minerId);
                newBlock.mineBlock(blockchain.getDifficulty());
                ConsensusResult result = consensusEngine.validateBlock(newBlock, blockchain);
                if (result.isSuccess()) {
                    blockchain.addBlock(newBlock);
                    Logger.info("Miner " + minerId + " successfully mined and added a new block: " + newBlock.getHash());
                } else {
                    Logger.error("Consensus failed for new block by miner " + minerId + ": " + result.getMessage());
                }
            } else {
                Logger.debug("Miner " + minerId + " found insufficient transactions to mine a block");
            }
        } catch (Exception e) {
            Logger.error("Error during mining by miner " + minerId + ": " + e.getMessage());
        }
    }

    public String getMinerId() {
        return minerId;
    }
}