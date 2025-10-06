package com.modular.blockchain;

import com.modular.blockchain.api.RestApiServer;
import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.blockchain.Miner;
import com.modular.blockchain.consensus.ConsensusEngine;
import com.modular.blockchain.transaction.TransactionPool;
import com.modular.blockchain.wallet.Wallet;
import com.modular.blockchain.util.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger.info("Modular Blockchain starting up");
        // -- Configuration and initialization code would go here --
        int difficulty = 4; // Example difficulty level
        int port = 8080; // Example port number
        int miningThreshold = 5; // Example mining threshold : number of transactions
        int miningInterval = 2; // Example mining interval in minutes
        String[] minerIds = {"miner-01", "miner-02", "miner-03"}; // Example miner IDs
        ArrayList<Miner> miners = new ArrayList<>();

        // --- REQUIRED USER IMPLEMENTATIONS ---
        // NOTE: Users must implement these themselves
        // 1. ConsensusEngine implementation
        ConsensusEngine consensusEngine = null; // TODO: Replace with actual implementation
        // 2. Wallet implementation
        Wallet wallet = null; // TODO: Replace with actual implementation

        // --- CORE COMPONENTS ---
        Blockchain blockchain = new Blockchain(difficulty); // Blockchain with specified difficulty
        Logger.info("Blockchain initialized with difficulty: " + difficulty);
        TransactionPool pool = new TransactionPool(); // Transaction pool
        Logger.info("Transaction pool created");

        // Create Miners
        for(String minerId : minerIds){
            Miner miner = new Miner(minerId, miningThreshold, pool, blockchain, consensusEngine); // Create a miner
            miners.add(miner);
            Logger.info("Miner created: " + minerId);
        }

        // RestAPI server starter
        RestApiServer server = new RestApiServer(blockchain, pool, port);
        Logger.info("REST API server initialized on port: " + port);
        server.start();
        Logger.info("REST API server started");

        // Start miners
        miners.forEach(miner -> {
            Logger.info("Starting mining for miner: " + miner.getMinerId());
            miner.startMining(miningInterval);
        });

        // Add shutdown hook for graceful exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            miners.forEach(Miner::stopMining);
            Logger.info("Server and miners stopped.");
        }));
    }
}
