package server;

/**
 * Player holds session ID, username, session GameMode and current amount 
 * of clicks for a player in a game. 
 * 
 * @author Sebastian Hoggard
 *
 */
public class Player {
	final int ID;
	final String username;
	final Communication.GameMode gameMode;
	
	ClientConnection connection;
	int clicks;
	
	/**
	 * Creates a Player from a ClientConnection object, given all other data. 
	 * 
	 * @param predecessor
	 * @param ID
	 * @param username
	 * @param gameMode
	 */
	public Player(ClientConnection predecessor, int ID, String username, Communication.GameMode gameMode) {
		this.connection = predecessor;
		this.ID = ID;
		this.username = username;
		this.gameMode = gameMode;
		this.clicks = 0;
	}
	
	/**
	 * Creates a Player from an existing Player object, taking all necessary data from it. 
	 * 
	 * @param predecessor
	 */
	public Player(Player predecessor) {
		this.connection = predecessor.connection;
		this.ID = predecessor.ID;
		this.username = predecessor.username;
		this.gameMode = predecessor.gameMode;
		this.clicks = predecessor.clicks;
	}
	
	/**
	 * Updates a Player object from a new ClientConnection object. 
	 * 
	 * @param clientConnection
	 */
	public void resume(ClientConnection clientConnection) {
		this.connection = clientConnection;
	}
	
	/**
	 * Forwards a message to this objects connected client, adding its ID. 
	 * 
	 * @param comType
	 * @param data
	 */
	public void sendMessage(int comType, byte[] data) {
		this.connection.sendMessage(comType, ID, data);
	}
	
	@Override
	public String toString() {
		return username;
	}
}
