package com.modular.blockchain.consensus;

import com.modular.blockchain.blockchain.Block;
import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.transaction.Transaction;
import com.modular.blockchain.transaction.SignedTransaction;
import java.util.List;

/**
 * A simple consensus engine implementation that handles block mining and validation
 * using basic Proof of Work (PoW) consensus rules.
 */
public class SimpleConsensusEngine implements ConsensusEngine {

    /**
     * Mines a new block by creating it with the given transactions and mining until
     * the PoW difficulty requirement is met.
     *
     * @param txs List of transactions to include in the block
     * @param blockchain The blockchain to mine the block for
     * @param minerId ID of the miner creating this block
     * @return The mined block that meets the difficulty requirement
     */
    @Override
    public Block mineBlock(List<Transaction> txs, Blockchain blockchain, String minerId) {
        // Create new block with transactions and previous block's hash
        Block block = new Block(blockchain.getChain().size(), System.currentTimeMillis(), txs, blockchain.getChain().getLast().getHash(), minerId);
        // Mine block until it meets difficulty requirement
        block.mineBlock(blockchain.getDifficulty());
        return block;
    }

    /**
     * Validates a block by checking transaction signatures and PoW difficulty.
     *
     * @param block The block to validate
     * @param blockchain The blockchain to validate against
     * @return ConsensusResult indicating if block is valid
     */
    @Override
    public ConsensusResult validateBlock(Block block, Blockchain blockchain) {
        // Validate that all transactions are signed and have valid signatures
        for (Transaction tx : block.getTransactions()) {
            if (tx instanceof SignedTransaction) {
                if (!tx.isValid()) {
                    return ConsensusResult.fail("Invalid transaction signature: " + tx.getId());
                }
            } else {
                return ConsensusResult.fail("Block contains unsupported transaction type: " + tx.getClass().getSimpleName());
            }
        }
        // Verify block hash meets required number of leading zeros for PoW
        if (!block.getHash().startsWith("0".repeat(blockchain.getDifficulty()))) {
            return ConsensusResult.fail("Block hash does not meet difficulty");
        }
        return ConsensusResult.ok(block);
    }
}