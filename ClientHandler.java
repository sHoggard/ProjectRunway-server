package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * ClientHandler makes sure there is always a ClientConnection ready for connecting. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin
 *
 */
public class ClientHandler extends ConnectionHandler {
	/**
	 * Makes sure clients can always connect to the server. 
	 * 
	 * @param server
	 */
	public ClientHandler(Server server) {
		super(server);
		try {
			this.serverSocket = new ServerSocket(80);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Kan ej starta ServerSocket för port 80. ");
			try {
				serverSocket.close();
			} catch (IOException ee) {
				System.out.println(ee.getMessage());
			}
			server.failedHandler(this);
		}
		this.openConnection = null;
		new Thread(this).start();
	}
	
	/**
	 * Continually checks if openConnection is null or has connected to a client, 
	 * in which case it creates a new ClientConnection in its place. 
	 */
	@Override
	public void run() {
		setDaemon(true);		//Make sure Thread terminates when JVM exits
		while(true) {
			if (openConnection == null || openConnection.f_connected) {
				openConnection = new ClientConnection(server, serverSocket);
				new Thread(openConnection).start();
			}
		}
	}
}
