package com.modular.blockchain.api;

import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.transaction.TransactionPool;
import com.modular.blockchain.util.Logger;
import com.modular.blockchain.transaction.SignedTransaction;
import com.modular.blockchain.wallet.SimpleWallet;
import com.modular.blockchain.wallet.WalletStore;

import java.util.List;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.io.File;
import java.io.FileWriter;

/**
 * REST API server that provides HTTP endpoints to interact with the blockchain network.
 * Exposes functionality for managing wallets, submitting transactions, and querying chain state.
 */
public class RestApiServer {
    private final Blockchain blockchain;
    private final TransactionPool transactionPool;
    private final HttpServer server;
    private final WalletStore walletStore;

    /**
     * Creates a new REST API server instance
     * @param blockchain The blockchain instance that will store blocks and validate the chain
     * @param transactionPool The pool for holding pending transactions before mining
     * @param port The TCP port number that the server will listen on
     * @param walletStore The store containing all registered wallet instances
     * @throws IOException If the HTTP server cannot bind to the specified port
     */
    public RestApiServer(Blockchain blockchain, TransactionPool transactionPool, int port, WalletStore walletStore) throws IOException {
        Logger.info("Initializing REST API server on port: " + port);
        this.blockchain = blockchain;
        this.transactionPool = transactionPool;
        this.walletStore = walletStore;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        setupEndpoints();
    }

    /**
     * Configures all HTTP endpoints and their handlers for the API server.
     * Endpoints include:
     * - /chain - Get the full blockchain
     * - /transaction - Submit new transactions
     * - /peers - Get connected peer nodes
     * - /register-wallet - Create new wallet
     * - /wallets - List all wallets
     * - /isChainValid - Check chain validity
     */
    private void setupEndpoints() {
        // Chain endpoint handler - returns the full blockchain
        server.createContext("/chain", exchange -> {
            Logger.debug("Received /chain request: " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = blockchainToJson();
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        // Transaction endpoint handler - processes new transaction submissions
        server.createContext("/transaction", exchange -> {
            Logger.debug("Received /transaction request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                byte[] body = exchange.getRequestBody().readAllBytes();
                String txJson = new String(body, StandardCharsets.UTF_8);
                Logger.info("Transaction received: " + txJson);
                try {
                    // Parse transaction JSON into components
                    String[] parts = txJson.replace("{", "").replace("}", "").replace("\"", "").split(",");
                    String sender = "", receiver = "", senderPrivateKey = "", receiverPublicKey = "";
                    double amount = 0;
                    for (String part : parts) {
                        String[] kv = part.split(":");
                        if (kv[0].trim().equals("sender")) sender = kv[1].trim();
                        if (kv[0].trim().equals("receiver")) receiver = kv[1].trim();
                        if (kv[0].trim().equals("senderPrivateKey")) senderPrivateKey = kv[1].trim();
                        if (kv[0].trim().equals("receiverPublicKey")) receiverPublicKey = kv[1].trim();
                        if (kv[0].trim().equals("amount")) amount = Double.parseDouble(kv[1].trim());
                    }

                    // Validate wallet existence and keys
                    SimpleWallet senderWallet = (SimpleWallet) walletStore.getWallet(sender);
                    SimpleWallet receiverWallet = (SimpleWallet) walletStore.getWallet(receiver);
                    if (senderWallet == null || receiverWallet == null) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"unknown sender or receiver\"}".getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        return;
                    }
                    if (senderPrivateKey.isEmpty()) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"missing sender private key\"}".getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        return;
                    }
                    if (receiverPublicKey.isEmpty()) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"missing receiver public key\"}".getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        return;
                    }

                    // Verify key authenticity
                    String senderTruePrivateKey = senderWallet.getKeys().getPrivateKeyBase64();
                    String receiverTruePublicKey = receiverWallet.getKeys().getPublicKeyBase64();
                    if(!senderTruePrivateKey.equals(senderPrivateKey)){
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"invalid sender private key\"}".getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        return;
                    }
                    if (!receiverTruePublicKey.equals(receiverPublicKey)){
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"invalid receiver public key\"}".getBytes(StandardCharsets.UTF_8));
                        exchange.close();
                        return;
                    }

                    // Create and validate transaction
                    SignedTransaction tx = senderWallet.createTransaction(receiverWallet.getPublicKeyBase64(), amount);
                    if (tx.isValid()) {
                        transactionPool.addTransaction(tx);
                        String response = String.format("{\"status\":\"accepted\",\"id\":\"%s\"}", tx.getId());
                        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                    } else {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.getResponseBody().write("{\"status\":\"invalid signature\"}".getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().write("{\"status\":\"error\"}".getBytes(StandardCharsets.UTF_8));
                }
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        // Peers endpoint handler - returns list of connected peers
        server.createContext("/peers", exchange -> {
            Logger.debug("Received /peers request: " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "[]";
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        // Wallet registration endpoint - creates new wallet and returns credentials
        server.createContext("/register-wallet", exchange -> {
            Logger.debug("Received /register-wallet request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                String userId = "user-" + System.currentTimeMillis();
                SimpleWallet newWallet = new SimpleWallet(userId);
                walletStore.addWallet(newWallet);

                // Generate wallet credentials
                KeyPair keyPair = newWallet.getKeys().getKeyPair();
                String privateKeyBase64 = java.util.Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
                String publicKeyBase64 = newWallet.getPublicKeyBase64();
                String response = String.format("{\"userId\":\"%s\",\"publicKey\":\"%s\",\"privateKey\":\"%s\"}",
                        userId, publicKeyBase64, privateKeyBase64);

                // Persist wallet data
                try {
                    File dir = new File("wallets");
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, userId + ".json");
                    try (FileWriter fw = new FileWriter(file)) {
                        fw.write(response);
                    }
                } catch (Exception e) {
                    Logger.error("Failed to write wallet file: " + e.getMessage());
                }
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        // Wallets listing endpoint - returns all registered wallets
        server.createContext("/wallets", exchange -> {
            Logger.debug("Received /wallets request: " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder sb = new StringBuilder("[");
                boolean first = true;
                for (String userId : walletStore.getAllUserIds()) {
                    SimpleWallet wallet = (SimpleWallet) walletStore.getWallet(userId);
                    if (wallet != null) {
                        if (!first) sb.append(",");
                        sb.append(String.format("{\"userId\":\"%s\",\"publicKey\":\"%s\"}",
                            userId, wallet.getPublicKeyBase64()));
                        first = false;
                    }
                }
                sb.append("]");
                String response = sb.toString();
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        // Chain validation endpoint - checks integrity of the blockchain
        server.createContext("/isChainValid", exchange -> {
            Logger.debug("Received /isChainValid request: " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                boolean isValid = blockchain.isChainValid();
                String response = String.format("{\"isValid\":%b}", isValid);
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });
    }

    /**
     * Serializes the blockchain data structure into a JSON string representation.
     * Currently returns a basic string representation - should be enhanced with proper JSON serialization.
     * @return JSON formatted string containing all blocks in the chain
     */
    private String blockchainToJson() {
        List<String> blocks = blockchain.getChain().stream().map(Object::toString).toList();
        return blocks.toString();
    }

    /**
     * Starts the HTTP server to begin accepting API requests
     */
    public void start() {
        Logger.info("Starting REST API server");
        server.start();
    }

    /**
     * Gracefully stops the HTTP server
     */
    public void stop() {
        Logger.info("Stopping REST API server");
        server.stop(0);
    }
}