package com.modular.blockchain.api;

import com.modular.blockchain.blockchain.Blockchain;
import com.modular.blockchain.blockchain.Miner;
import com.modular.blockchain.transaction.TransactionPool;
import com.modular.blockchain.util.Logger;

import java.util.List;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * REST API server that provides HTTP endpoints to interact with the blockchain
 */
public class RestApiServer {
    private final Blockchain blockchain;
    private final TransactionPool transactionPool;
    private final HttpServer server;

    /**
     * Creates a new REST API server
     * @param blockchain The blockchain instance to expose
     * @param transactionPool The transaction pool to expose
     * @param port The port to listen on
     * @throws IOException If the server cannot be created
     */
    public RestApiServer(Blockchain blockchain, TransactionPool transactionPool, int port) throws IOException {
        Logger.info("Initializing REST API server on port: " + port);
        this.blockchain = blockchain;
        this.transactionPool = transactionPool;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        setupEndpoints();
    }

    /**
     * Sets up the HTTP endpoints for the API server
     */
    private void setupEndpoints() {
        server.createContext("/chain", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
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
            }
        });
        server.createContext("/transaction", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Logger.debug("Received /transaction request: " + exchange.getRequestMethod());
                if ("POST".equals(exchange.getRequestMethod())) {
                    byte[] body = exchange.getRequestBody().readAllBytes();
                    String txJson = new String(body, StandardCharsets.UTF_8);
                    Logger.info("Transaction received: " + txJson);
                    // Transaction deserialization is user-implemented; here we just log or ignore
                    // transactionPool.addTransaction(userDeserializedTransaction);
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().write("{\"status\":\"received\"}".getBytes(StandardCharsets.UTF_8));
                    exchange.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }
            }
        });
        server.createContext("/peers", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
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
            }
        });
    }

    /**
     * Converts the blockchain to a JSON string
     * @return JSON string representation of the blockchain
     */
    private String blockchainToJson() {
        // For demo: just return block toString list (user should implement proper JSON serialization)
        List<String> blocks = blockchain.getChain().stream().map(Object::toString).toList();
        return blocks.toString();
    }

    /**
     * Starts the API server
     */
    public void start() {
        Logger.info("Starting REST API server");
        server.start();
    }

    /**
     * Stops the API server
     */
    public void stop() {
        Logger.info("Stopping REST API server");
        server.stop(0);
    }
}