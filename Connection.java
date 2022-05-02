package server;

import java.io.*;
import java.net.*;

/**
 * Connection handles communication between Server and remote parts of the system. 
 *  
 * @author Sebastian Hoggard, Rasmus Svedin, Daniel Petersén
 *
 */
public abstract class Connection implements Runnable {
	protected Server server;
	protected ServerSocket serverSocket;
	protected Socket socket;
	protected DataInputStream input;
	protected DataOutputStream output;
	
	boolean f_connected;
	
	/**
	 * Initializes sockets for a connection. 
	 * 
	 * @param server
	 * @param serverSocket
	 */
	protected Connection(Server server, ServerSocket serverSocket) {
		this.f_connected = false;
		this.server = server;
		this.serverSocket = serverSocket;
		try {
			this.socket = serverSocket.accept();
			socket.setSoTimeout(1000);
			this.input = new DataInputStream(socket.getInputStream());
			this.output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Kunde inte ansluta via port " + serverSocket.getLocalPort() + ". ");
		} finally {
			this.f_connected = true;
		}
	}
	
	/**
	 * Sends a message from server. 
	 * 
	 * @param	comType	See Communication. 
	 * @param	ID		ID of host. 
	 * @param	data	According to comType. See Communication. 
	 */
	public abstract void sendMessage(int comType, int ID, byte[] data);
}
