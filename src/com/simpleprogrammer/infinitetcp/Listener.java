package com.simpleprogrammer.infinitetcp;

/**
 * This interface defines a {@link #onDisconnect()} method and an
 * {@link #onReceive(Object)} method<br>
 * The {@link #onDisconnect()} method is called when the Client disconnects from
 * the Server and<br>
 * the other method {@link #onReceive(Object)} is called when an object is
 * received from the Server.
 */
public interface Listener {

	/**
	 * Should be called only when a Client disconnects from a Server.<br>
	 * Is called by the Client class when that Client disconnects.
	 */
	public void onDisconnect();

	/**
	 * Should be called only when a Client receives something from a Server.<br>
	 * Is called by the Client class when that Client receives something.
	 * 
	 * @param obj
	 *            the Object received by the Client
	 */
	public void onReceive(Object obj);

}
