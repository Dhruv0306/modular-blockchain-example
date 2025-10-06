package com.modular.blockchain.consensus;

import com.modular.blockchain.blockchain.Block;

/**
 * Represents the result of a consensus operation in the blockchain.
 * Contains information about the proposed block, success status, and any relevant messages.
 */
public class ConsensusResult {
    private final Block proposedBlock;
    private final boolean success;
    private final String message;

    /**
     * Constructs a new ConsensusResult with the specified parameters.
     *
     * @param proposedBlock The block that was proposed in the consensus operation
     * @param success Whether the consensus operation was successful
     * @param message A message describing the result of the operation
     */
    public ConsensusResult(Block proposedBlock, boolean success, String message) {
        this.proposedBlock = proposedBlock;
        this.success = success;
        this.message = message;
    }

    /**
     * Gets the block that was proposed in this consensus operation.
     *
     * @return The proposed Block object
     */
    public Block getProposedBlock() {
        return proposedBlock;
    }

    /**
     * Checks if the consensus operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the message describing the result of the consensus operation.
     *
     * @return The result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Creates a successful ConsensusResult with the given block.
     *
     * @param block The block that was successfully processed
     * @return A new ConsensusResult indicating success
     */
    public static ConsensusResult ok(Block block) {
        return new ConsensusResult(block, true, "OK");
    }

    /**
     * Creates a failed ConsensusResult with the given error message.
     *
     * @param message The error message describing why consensus failed
     * @return A new ConsensusResult indicating failure
     */
    public static ConsensusResult fail(String message) {
        return new ConsensusResult(null, false, message);
    }
}