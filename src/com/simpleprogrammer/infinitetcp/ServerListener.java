package com.simpleprogrammer.infinitetcp;

/**
 * This interface defines a {@link #onDisconnect(Connection)} method, a
 * {@link #onReceive(Connection, Object)} method, and a
 * {@link #onConnect(Connection)} method<br>
 * The {@link #onDisconnect(Connection)} method is called when a Client
 * disconnects from the Server and<br>
 * the method {@link #onReceive(Connection, Object)} is called when an object is
 * received from a Client and<br>
 * the method {@link #onConnect(Connection)} is called when a Client connects to
 * the Server .
 */
public interface ServerListener {

	/**
	 * This method should be called when a Client connects to the Server<br>
	 * It is called by the Server class when a Client connects
	 * 
	 * @param conn
	 *            the Connection to the Client
	 */
	public void onConnect(Connection conn);

	/**
	 * This method should be called when the Server received something from a Client
	 * It is called by the Connection class when it receives something
	 * 
	 * @param obj
	 *            the Object received
	 * @param conn
	 *            the Connection that the Server received the Object from
	 */
	public void onReceive(Connection conn, Object obj);

	/**
	 * This method should be called when a Client disconnects from the Server<br>
	 * It is called by the Connection class when the Client disconnects
	 * 
	 * @param conn
	 *            the Connection to the Client that disconnected
	 */
	public void onDisconnect(Connection conn);

}
