package com.simpleprogrammer.infinitetcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class creates a TCP server in which Clients can connect to<br>
 * The class uses XML to serialize the data sent
 */
public class Server {

	private int port = -1;
	// Create the XMLEncoder
	private XMLSerializer serializer = new XMLSerializer();
	private ServerSocket server;
	// check if the server is bound to an port
	private boolean bound = false;
	// check if the server has started
	private boolean started = false;
	// list of all the listeners user has added
	protected static ArrayList<ServerListener> listeners = new ArrayList<ServerListener>();
	// list of all client connections
	protected static ArrayList<Connection> connections = new ArrayList<Connection>();

	/**
	 * Creates a Server bound to the specified port
	 * 
	 * @param port
	 *            the port to bind the Server to
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 */
	public Server(int port) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port " + port + " is out of range!");
		}

		this.port = port;
		bound = true;
	}

	/**
	 * Creates an unbound Server
	 */
	public Server() {

	}

	/**
	 * Binds the Server to the specified port
	 * 
	 * @param port
	 *            the port to bind the server to
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 */
	public void bind(int port) {
		if (bound) {
			throw new IllegalStateException("Server is already bound!");
		}
		this.port = port;
	}

	/**
	 * Starts the Server<br>
	 * If wanting to run other processes this should be called in a thread as it
	 * will run infinitely until the {@link #stop()} method is invoked
	 * 
	 * @exception IllegalStateException
	 *                if the Server is not bound or has already started
	 * @throws IOException
	 *             if an I/O error has occurred when starting the Server
	 * 
	 */
	public void start() throws IOException {
		// check for errors
		if (!bound) {
			throw new IllegalStateException("Server is not bound!");
		}

		if (started) {
			throw new IllegalStateException("Server already started!");
		}

		// start the server
		server = new ServerSocket(port);

		started = true;

		// start listening for clients
		while (started) {
			Socket s = server.accept();
			Connection conn = new Connection(s, serializer);
			connections.add(conn);
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).onConnect(conn);
			}
		}

	}

	/**
	 * Starts the Server<br>
	 * If wanting to run other processes this should be called in a thread as it
	 * will run infinitely until the {@link #stop()} method is invoked
	 * 
	 * @exception IllegalStateException
	 *                if the Server has not started
	 * @exception IOException
	 *                if an I/O error has occurred when stopping the Server
	 * 
	 */
	public void stop() {
		if (!started) {
			throw new IllegalStateException("Server has not started!");
		}

		started = false;

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server = null;

		started = false;
	}

	/**
	 * Returns all the connections currently connected to the Server.
	 * 
	 * @return an ArrayList holding all the connections to the Server.
	 */
	public ArrayList<Connection> getConnections() {
		return connections;
	}

	/**
	 * Adds a @code ServerListener to the Server to launch the event methods
	 * 
	 * @param listener
	 *            the ServerListener to be added
	 */
	public void addListener(ServerListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the first index of a @code ServerListener from the Server
	 * 
	 * @param listener
	 *            the ServerListener to be removed
	 * @return whether or not the ServerListener was removed
	 */
	public boolean removeListener(ServerListener listener) {
		return listeners.remove(listener);
	}

}
