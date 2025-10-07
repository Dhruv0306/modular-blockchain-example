# Modular Blockchain Example

A complete, functional blockchain implementation using modular-blockchain-core. Features a working consensus engine, wallet management system, transaction pool, mining operations, and REST API server.

## Features

- **SimpleConsensusEngine**: Handles agreement between nodes on blockchain state
- **WalletStore**: Manages cryptographic wallets for transaction signing
- **Multi-Miner Support**: Configurable miners competing to create new blocks
- **Transaction Pool**: Holds pending transactions waiting to be mined
- **REST API Server**: External interface for blockchain interaction
- **Configurable Mining**: Adjustable difficulty, thresholds, and intervals

## Quick Start

1. **Build and Run**:
   ```bash
   mvn clean compile exec:java
   ```

2. **Access API**: The REST server starts on port 8080 by default

## Configuration

Key parameters in `Main.java`:
- `difficulty = 4`: Mining difficulty level
- `port = 8080`: REST API server port
- `miningThreshold = 5`: Transactions required before mining starts
- `miningInterval = 2`: Time between mining attempts (minutes)
- `minerIds`: Unique identifiers for miners (miner-01, miner-02, miner-03)

## Architecture

The application initializes and coordinates:
- Blockchain with specified mining difficulty
- Transaction pool for pending transactions
- Multiple miners for block creation
- Consensus engine for network agreement
- Wallet store for transaction signing
- REST API server for external access
