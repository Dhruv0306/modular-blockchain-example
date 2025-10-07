package com.modular.blockchain;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Integration test runner for blockchain server API
 * Tests wallet registration, transactions, and blockchain state
 */
public class ServerIntegrationRunner {
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * Main method to run integration tests
     */
    public static void main(String[] args) throws Exception {
        // Register 4 test wallets and store their credentials
        List<Map<String, String>> wallets = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Map<String, String> wallet = registerWallet();
            System.out.println("Registered wallet: " + wallet);
            wallets.add(wallet);
        }

        // Execute test transactions with both valid and invalid parameters
        for (int i = 0; i < 10; i++) {
            String txJson = getString(i, wallets);
            String response = post("/transaction", txJson);
            System.out.printf("Transaction %d response: %s%n", i + 1, response);
        }

        // Verify final state of wallets
        String walletsList = get("/wallets");
        System.out.println("Wallets on server: " + walletsList);

        // Verify final blockchain state
        String chain = get("/chain");
        System.out.println("Blockchain: " + chain);
    }

    /**
     * Generates transaction JSON with test data
     * @param i Transaction index
     * @param wallets List of registered test wallets
     * @return JSON string with transaction data
     */
    private static String getString(int i, List<Map<String, String>> wallets) {
        int senderIdx = i % 4;
        int receiverIdx = (i + 1) % 4;
        Map<String, String> sender = wallets.get(senderIdx);
        Map<String, String> receiver = wallets.get(receiverIdx);

        double amount = 10 + i;
        String senderPrivateKey = sender.get("privateKey");
        String receiverPublicKey = receiver.get("publicKey");

        // Inject invalid test cases
        if (i == 3) senderPrivateKey = "invalidkey";  // Test invalid sender key
        if (i == 6) receiverPublicKey = "invalidkey"; // Test invalid receiver key
        if (i == 8) senderPrivateKey = "";            // Test missing sender key

        String txJson = String.format(
            "{\"sender\":\"%s\",\"receiver\":\"%s\",\"senderPrivateKey\":\"%s\",\"receiverPublicKey\":\"%s\",\"amount\":%s}",
            sender.get("userId"), receiver.get("userId"), senderPrivateKey, receiverPublicKey, amount
        );
        return txJson;
    }

    /**
     * Registers a new wallet with the server
     * @return Map containing wallet credentials
     */
    private static Map<String, String> registerWallet() throws IOException {
        String response = post("/register-wallet", "");
        return parseJson(response);
    }

    /**
     * Executes HTTP POST request
     * @param path API endpoint path
     * @param body Request body
     * @return Response body as string
     */
    private static String post(String path, String body) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        if (!body.isEmpty()) {
            conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn);
    }

    /**
     * Executes HTTP GET request
     * @param path API endpoint path
     * @return Response body as string
     */
    private static String get(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    /**
     * Reads HTTP response body
     * @param conn Active HTTP connection
     * @return Response body as string
     */
    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream is = (conn.getResponseCode() < 400) ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    /**
     * Basic JSON string parser for flat objects
     * Note: This is a simplified implementation for demo purposes only
     * @param json JSON string to parse
     * @return Map of key-value pairs from JSON
     */
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        for (String part : json.split(",")) {
            String[] kv = part.split(":", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }
}