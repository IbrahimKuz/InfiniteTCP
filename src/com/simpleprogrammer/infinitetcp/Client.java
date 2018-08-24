package com.simpleprogrammer.infinitetcp;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * The class in which an connection is issued to a TCP server<br>
 * The class uses XML to serialize the data Data is sent using the @link
 * #send(Object) method
 */
public class Client {

	// Internet Protocol address for socket to connect to
	private InetAddress ip = null;
	// Port for socket to connect to
	private int port = -1;
	// XMLSerializer to send objects as XML string over socket XMLSerializer uses
	// xStream library
	private XMLSerializer serializer = new XMLSerializer();
	// The main Socket
	private Socket socket;
	// Check if the socket has started
	private boolean started = false;
	// check if the socket is bound to an ip and port
	private boolean bound = false;
	// Threads to receive and send data
	private Thread receive, send;
	// BufferedReader to read XML data
	private BufferedReader in;
	// BufferedWriter to write XML data
	private BufferedWriter out;
	// List of listeners user has added
	private ArrayList<Listener> listeners = new ArrayList<Listener>();

	/**
	 * Constructs a client which is bound to the specified String as an InetAddress
	 * and a int port
	 * 
	 * @exception UnknownHostException
	 *                if the ip is not valid
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 * 
	 * @param ip
	 *            the String from which an InetAddress is formed
	 * @param port
	 *            the port which the socket is connected to<br>
	 *            Both of the parameters can be changed by calling the
	 *            {@link #bind(String, int)} method before the client has started
	 */
	public Client(String ip, int port) {
		try {
			InetAddress host = InetAddress.getByName(ip);
			this.ip = host;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port out of range: " + port + "!");
		}

		this.port = port;
		bound = true;
	}

	/**
	 * Constructs a client which is bound to the specified InetAddress and a int
	 * port
	 * 
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 * @param ip
	 *            the InetAddress in which the socket is connected to
	 * @param port
	 *            the port which the socket is connected to<br>
	 *            Both of the parameters can be changed by calling the
	 *            {@link #bind(InetAddress, int)} method before the client has
	 *            started
	 */
	public Client(InetAddress ip, int port) {
		this.ip = ip;
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port out of range: " + port + "!");
		}

		this.port = port;
		bound = true;
	}

	/**
	 * Constructs a client which is bound to no IP or port
	 **/
	public Client() {

	}

	/**
	 * Binds the client to the specified InetAddress as a string and a port
	 * 
	 * @exception IllegalStateException
	 *                if the Socket is already bound
	 * @exception UnknownHostException
	 *                if the String is not a valid IP
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 * @param ip
	 *            the InetAddress in which the Socket will be bound to
	 * @param port
	 *            thte port in which the Socket will be bound to
	 */
	public void bind(String ip, int port) {
		if (bound) {
			throw new IllegalStateException("Socket is already bound!");
		}
		try {
			InetAddress host = InetAddress.getByName(ip);
			this.ip = host;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port out of range: " + port + "!");
		}

		this.port = port;
		bound = true;
	}

	/**
	 * Binds the client to the specified InetAddress and port
	 * 
	 * @exception IllegalStateException
	 *                if the Socket is already bound
	 * @exception IllegalArgumentException
	 *                if the port is out of range
	 * @param ip
	 *            the InetAddress in which the Socket will be bound to
	 * @param port
	 *            the port in which the Socket will be bound to
	 */
	public void bind(InetAddress ip, int port) {
		if (bound) {
			throw new IllegalStateException("Socket is already bound!");
		}

		this.ip = ip;
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port out of range: " + port + "!");
		}

		this.port = port;
		bound = true;
	}

	/**
	 * Starts the clients socket and starts receiving from it.
	 * 
	 * @throws IOException
	 *             if an I/O exception happens when starting the socket
	 **/
	public void start() throws IOException {
		if (!bound) {
			throw new IllegalStateException("Socket is not bound!");
		}
		checkStarted();

		this.socket = new Socket(ip, port);
		out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream())));
		out.flush();
		in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));

		receive();

		started = true;

	}

	/**
	 * Stops the clients socket and the input and output streams.
	 * 
	 * @throws IOException
	 *             if an I/O exception happens when stopping the socket
	 **/
	public void stop() throws IOException {
		if (started) {
			started = false;
			if (receive.isAlive()) {
				try {
					receive.join(1);
				} catch (InterruptedException e) {

				}
			}

			socket.close();
			in.close();
			out.close();
			in = null;
			out = null;
			socket = null;
		}
	}

	// checks if the socket is started and throws and IllegalStateException if it is
	private void checkStarted() {
		if (started) {
			throw new IllegalStateException("Socket already connected!");
		}
	}

	/**
	 * @return whether or not the socket is connected to a server
	 */
	public boolean isConnected() {
		return started;
	}

	/**
	 * Sends the specified object to the server in XML format
	 * 
	 * @param obj
	 *            the Object to send
	 * @exception IllegalStateException
	 *                if the socket is not connected
	 * @exception IOException
	 *                if an I/O error occurs when writing to the server
	 **/
	public void send(Object obj) {
		send = new Thread("Send") {

			@Override
			public void run() {

				if (!started) {
					throw new IllegalStateException("Socket not connected!");
				}

				// synchronize out to minimize errors
				synchronized (out) {
					try {
						// serialize to XML and send
						out.write(serializer.toXML(obj) + "\n");
						// flush the outputstream
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		};
		send.start();
	}

	// receive from the server and activate the listeners
	private void receive() {
		receive = new Thread("Receive") {
			@Override
			public void run() {
				while (started) {
					// synchronize to minimize errors
					synchronized (in) {
						try {
							// read from Inputstream and convert to object from XML
							Object obj = serializer.fromXML(in.readLine());
							// activate all the listeners receive methods
							for (int i = 0; i < listeners.size(); i++) {
								listeners.get(i).onReceive(obj);
							}
						} catch (IOException e) {
							// catch an IOException and stop the socket
							try {
								Client.this.stop();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							// activate the listeners disconnect methods
							for (int i = 0; i < listeners.size(); i++) {
								listeners.get(i).onDisconnect();
							}
							return;
						} catch (NullPointerException e) {
							// Do nothing when receiving null
						}
					}
				}
			}
		};
		receive.start();
	}

	/**
	 * Adds a @code Listener to the client for the receive and disconnect methods
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the first instance of the @code Listener in the client
	 * 
	 * @param listener
	 *            the listener to be removed
	 * 
	 * @return whether or not the listener was removed
	 */
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

}
