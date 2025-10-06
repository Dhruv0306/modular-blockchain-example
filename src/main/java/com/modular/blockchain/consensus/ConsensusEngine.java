package com.modular.blockchain.consensus;

import com.modular.blockchain.blockchain.Block;
import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.transaction.Transaction;
import java.util.List;

/**
 * Interface defining the consensus mechanism for the blockchain.
 * Implementations of this interface handle block mining and validation.
 */
public interface ConsensusEngine {
    /**
     * Mines a new block with the given transactions.
     *
     * @param txs List of transactions to include in the block
     * @param blockchain Reference to the current blockchain
     * @param minerId Identifier of the miner creating this block
     * @return The newly mined block
     */
    Block mineBlock(List<Transaction> txs, Blockchain blockchain, String minerId);

    /**
     * Validates a block according to the consensus rules.
     *
     * @param block The block to validate
     * @param blockchain Reference to the current blockchain
     * @return Result of the validation
     */
    ConsensusResult validateBlock(Block block, Blockchain blockchain);
}