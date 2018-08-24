package com.simpleprogrammer.infinitetcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class implies a connection to a Client however it can not be
 * instantised.<br>
 * A connection is created when a Client joins the Server.
 */
public class Connection {
	// socket connected to
	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;
	// id is optional and can be set using the getID method
	private int id = -1;
	private Thread receive, send;
	// Serializer to convert to XML
	private XMLSerializer serializer;

	// Not to be instantised outside of the jar
	protected Connection(Socket socket, XMLSerializer serializer) throws IOException {
		this.serializer = serializer;
		this.socket = socket;
		// initialize streams
		out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream())));
		out.flush();
		in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
		// start receiving
		receive();
	}

	// method to receive from socket
	private void receive() {
		receive = new Thread("Receive") {
			@Override
			public void run() {
				while (true) {
					// synchronize to minimize errors
					synchronized (in) {
						try {
							// read from socket and convert XML to object
							Object obj = serializer.fromXML(in.readLine());
							// activate receive methods in listeners
							for (int i = 0; i < Server.listeners.size(); i++) {
								Server.listeners.get(i).onReceive(Connection.this, obj);
							}
						} catch (IOException e) {
							// the socket has an error close the connection
							Server.connections.remove(Connection.this);
							for (int i = 0; i < Server.listeners.size(); i++) {
								Server.listeners.get(i).onDisconnect(Connection.this);
							}
							return;
						}
					}
				}
			}
		};
		receive.start();
	}

	/**
	 * Sends the specified Object to the peer which the socket is connected to
	 * 
	 * @param obj
	 *            the Object to send
	 * @exception IOException
	 *             if an error occurs when sending the object
	 */
	public void send(Object obj) {
		send = new Thread("Send") {
			@Override
			public void run() {
				synchronized (out) {
					try {
						// convert to XML and send
						out.write(serializer.toXML(obj) + "\n");
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		send.start();
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public void stop() {
		try {
			receive.join(1);
			socket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Sets the id of the connection so as to distinguish from other connections<br>
	 * If this method was never called the id will be -1
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the id of the connection<br>
	 * If the method {@link #setId(int)} was never called -1 will be returned
	 * 
	 * @return the id of the connection
	 */
	public int getId() {
		return id;
	}

}
