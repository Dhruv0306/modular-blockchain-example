package com.modular.blockchain.transaction;

/**
 * Interface representing a blockchain transaction.
 * Defines core functionality required for all transaction types.
 */
public interface Transaction {
    /**
     * Gets the unique identifier for this transaction.
     * @return String containing the transaction ID
     */
    String getId();

    /**
     * Validates if this transaction meets all required criteria.
     * @return true if transaction is valid, false otherwise
     */
    boolean isValid();

    /**
     * Converts the transaction to JSON format.
     * @return String containing JSON representation of the transaction
     */
    String toJson();
}