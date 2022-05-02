package server;

import java.net.ServerSocket;

/**
 * ConnectionHandler makes sure there is always a Connection ready for connecting. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin
 *
 */
public abstract class ConnectionHandler extends Thread implements Runnable {
	protected Server server;
	protected ServerSocket serverSocket;
	protected Connection openConnection;
	
	/**
	 * Sets server instance, for communicative purposes. 
	 * 
	 * @param server
	 */
	protected ConnectionHandler(Server server) {
		this.server = server;
	}
}
