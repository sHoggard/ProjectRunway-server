package server;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * ClientConnection handles communication between Server and a client. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin, Daniel Petersén
 *
 */
public class ClientConnection extends Connection {
	/**
	 * Creates a connection between the server and a client, 
	 * complete with a definition for sending and receiving messages. 
	 * 
	 * @param server
	 * @param serverSocket
	 */
	public ClientConnection(Server server, ServerSocket serverSocket) {
		super(server, serverSocket);
	}
	
	/**
	 * Sends a String to its connected client, containing player statistics or global game statistics. 
	 */
	public void sendString(int comType, int ID, String str) {
		System.out.println("Server: 0x" + Integer.toHexString(comType) + " " + ID);
		System.out.println(str);
		try {
			output.writeInt(comType);
			output.writeInt(ID);
			output.writeUTF(str);
			output.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Sends a message to its connected client. 
	 */
	@Override
	public void sendMessage(int comType, int ID, byte[] data) {
		if (comType != Communication.PING)
			System.out.println("Server: 0x" + Integer.toHexString(comType) + " " + ID);
		if (data != null)
			System.out.println(Arrays.toString(data));
		try {
			output.writeInt(comType);
			output.writeInt(ID);
			if (data != null) {
				for (byte octet : data) {
					output.writeByte(octet);
				}
			}
			output.flush();
		} catch (IOException e){
			System.out.println(e.getMessage());
			System.out.println("Kan ej skicka meddelande till klient. ");
		}
	}
	
	/**
	 * Reads messages from its connected client. 
	 */
	@Override
	public void run() {
		while (true) {
			int comType, ID;
			byte[] data = null;
			try {
				comType = input.readInt();
				ID = input.readInt();
				switch (comType) {
					case Communication.PLAY:
						data = new byte[21];
						input.read(data);
						break;
					case Communication.LOG_IN:
					case Communication.REGISTER_PLAYER:
						data = new byte[40];
						input.read(data);
						break;
					case Communication.REQUEST_STATS:
						data = new byte[21];
						input.read(data);
						break;
					case Communication.PING:
						sendMessage(Communication.PING, ID, null);
						continue;
				}
//			} catch (EOFException e) {
//				System.out.println(e.getMessage());
//				System.out.println("Klient bortkopplad. ");
//				return;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println("Klient bortkopplad. ");
				server.lostConnection(this);
				return;
			}
			server.receiveMessage(comType, ID, data, this);
		}
	}
}
