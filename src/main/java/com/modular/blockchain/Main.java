package com.modular.blockchain;

import com.modular.blockchain.api.RestApiServer;
import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.blockchain.Miner;
import com.modular.blockchain.consensus.ConsensusEngine;
import com.modular.blockchain.consensus.SimpleConsensusEngine;
import com.modular.blockchain.transaction.TransactionPool;
import com.modular.blockchain.wallet.WalletStore;
import com.modular.blockchain.util.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main entry point for the Modular Blockchain application.
 * Initializes and starts all core blockchain components including miners, consensus engine,
 * transaction pool, and REST API server.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Logger.info("Modular Blockchain starting up");

        // Configuration parameters
        int difficulty = 4;           // Mining difficulty level - higher means more computation required
        int port = 8080;             // Port number for REST API server
        int miningThreshold = 5;      // Number of transactions required before mining starts
        int miningInterval = 2;       // Time between mining attempts in minutes
        String[] minerIds = {"miner-01", "miner-02", "miner-03"}; // Unique identifiers for miners
        ArrayList<Miner> miners = new ArrayList<>();

        // Initialize core system components
        // ConsensusEngine handles agreement between nodes on blockchain state
        ConsensusEngine consensusEngine = new SimpleConsensusEngine();

        // WalletStore manages cryptographic wallets for transaction signing
        WalletStore walletStore = new WalletStore();

        // Initialize blockchain with specified mining difficulty
        Blockchain blockchain = new Blockchain(difficulty);
        Logger.info("Blockchain initialized with difficulty: " + difficulty);

        // Transaction pool holds pending transactions waiting to be mined
        TransactionPool pool = new TransactionPool();
        Logger.info("Transaction pool created");

        // Initialize miners that will compete to create new blocks
        for(String minerId : minerIds){
            Miner miner = new Miner(minerId, miningThreshold, pool, blockchain, consensusEngine);
            miners.add(miner);
            Logger.info("Miner created: " + minerId);
        }

        // Initialize and start REST API server for external interaction
        RestApiServer server = new RestApiServer(blockchain, pool, port, walletStore);
        Logger.info("REST API server initialized on port: " + port);
        server.start();
        Logger.info("REST API server started");

        // Start mining operations on all miners
        miners.forEach(miner -> {
            Logger.info("Starting mining for miner: " + miner.getMinerId());
            miner.startMining(miningInterval);
        });

        // Register shutdown hook for graceful system termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            miners.forEach(Miner::stopMining);
            Logger.info("Server and miners stopped.");
        }));
    }
}