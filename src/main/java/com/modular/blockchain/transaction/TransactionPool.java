package com.modular.blockchain.transaction;

import com.modular.blockchain.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages a thread-safe pool of pending transactions waiting to be added to the blockchain.
 * Provides synchronized methods for adding, retrieving and removing transactions.
 * The pool acts as a temporary storage for transactions before they are mined into blocks.
 */
public class TransactionPool {
    private final List<Transaction> pendingTransactions = new ArrayList<>();

    /**
     * Adds a new transaction to the pending pool after validating it.
     * Invalid transactions are rejected and logged as errors.
     * This method is synchronized to prevent concurrent modifications.
     *
     * @param tx The transaction to validate and add to the pool
     */
    public synchronized void addTransaction(Transaction tx) {
        if (tx.isValid()) {
            pendingTransactions.add(tx);
            Logger.info("Transaction added to pool: " + tx.getId());
        } else {
            Logger.error("Invalid transaction rejected: " + tx.getId());
        }
    }

    /**
     * Retrieves and removes a batch of pending transactions up to the specified size.
     * The transactions are removed from the pool to prevent double-processing.
     * This method is synchronized to prevent concurrent access.
     *
     * @param size The maximum number of transactions to retrieve
     * @return A list of up to 'size' transactions, or an empty list if pool is empty
     */
    public synchronized List<Transaction> getBatch(int size) {
        if (pendingTransactions.isEmpty()) {
            Logger.debug("Transaction pool is empty when getBatch called");
            return Collections.emptyList();
        }
        int batchSize = Math.min(size, pendingTransactions.size());
        Logger.debug("Retrieving batch of " + batchSize + " transactions from pool");
        ArrayList<Transaction> batch = new ArrayList<>(pendingTransactions.subList(0, batchSize));
        removeBatch(batch);
        return batch;
    }

    /**
     * Removes a batch of transactions from the pending pool.
     * Used internally after retrieving a batch to prevent double-processing.
     * This method is synchronized to maintain thread-safety.
     *
     * @param batch The list of transactions to remove from the pool
     */
    public synchronized void removeBatch(ArrayList<Transaction> batch) {
        pendingTransactions.removeAll(batch);
        String message = "Remaining Transection Count " + pendingTransactions.size();
        Logger.debug(message);
    }

    /**
     * Adds back a batch of transactions to the pending pool.
     * Used when transactions need to be requeued, e.g. after a failed mining attempt.
     * This method is synchronized to maintain thread-safety.
     *
     * @param batch The list of transactions to add back to the pool
     */
    public synchronized void addBack(ArrayList<Transaction> batch) {
        pendingTransactions.addAll(batch);
        String message = "Remaining Transection Count " + pendingTransactions.size();
        Logger.debug(message);
    }

    /**
     * Removes specific transactions from the pending pool.
     * Used when transactions have been successfully processed and added to the blockchain.
     * This method is synchronized to prevent concurrent modifications.
     *
     * @param txs The list of transactions to permanently remove from the pool
     */
    public synchronized void removeTransactions(List<Transaction> txs) {
        pendingTransactions.removeAll(txs);
        Logger.info("Removed " + txs.size() + " transactions from pool");
    }
}