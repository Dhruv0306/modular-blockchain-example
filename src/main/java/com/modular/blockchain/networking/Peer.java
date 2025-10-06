package com.modular.blockchain.networking;

import com.modular.blockchain.util.Logger;

/**
 * Represents a peer node in the blockchain network.
 * Contains identification and network location information for a peer.
 */
public class Peer {
    /** Unique identifier for the peer */
    private String id;

    /** Network address of the peer */
    private String address;

    /** Port number the peer is listening on */
    private int port;

    /**
     * Creates a new Peer with the specified identification and network details
     * @param id Unique identifier for the peer
     * @param address Network address of the peer
     * @param port Port number the peer is listening on
     */
    public Peer(String id, String address, int port) {
        Logger.info("Peer created: id=" + id + ", address=" + address + ", port=" + port);
        this.id = id;
        this.address = address;
        this.port = port;
    }

    /**
     * Gets the peer's unique identifier
     * @return The peer's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the peer's unique identifier
     * @param id The ID to set
     */
    public void setId(String id) {
        Logger.debug("Peer id updated from " + this.id + " to " + id);
        this.id = id;
    }

    /**
     * Gets the peer's network address
     * @return The peer's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the peer's network address
     * @param address The address to set
     */
    public void setAddress(String address) {
        Logger.debug("Peer address updated from " + this.address + " to " + address);
        this.address = address;
    }

    /**
     * Gets the peer's port number
     * @return The peer's port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the peer's port number
     * @param port The port to set
     */
    public void setPort(int port) {
        Logger.debug("Peer port updated from " + this.port + " to " + port);
        this.port = port;
    }

    /**
     * Returns a string representation of the Peer
     * @return String containing the peer's ID, address and port
     */
    @Override
    public String toString() {
        return "Peer{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

    /**
     * Checks if this peer is equal to another object
     * @param o Object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        if (port != peer.port) return false;
        if (!id.equals(peer.id)) return false;
        return address.equals(peer.address);
    }

    /**
     * Generates a hash code for the peer
     * @return Hash code based on the peer's ID, address and port
     */
    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + port;
        return result;
    }
}