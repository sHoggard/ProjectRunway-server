package server;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * EmbSysConnection handles communication between Server and an embedded system. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin, Daniel Petersén
 *
 */
public class EmbSysConnection extends Connection {
	/**
	 * Creates a connection between the server and a embedded system, 
	 * complete with a definition for sending and receiving messages. 
	 * 
	 * @param server
	 * @param serverSocket
	 */
	public EmbSysConnection(Server server, ServerSocket serverSocket) {
		super(server, serverSocket);
	}
	
	/**
	 * Sends a message to its connected embedded system. 
	 */
	@Override
	public void sendMessage(int comType, int ID, byte[] data) {
		System.out.println("Server: 0x" + Integer.toHexString(comType) + " " + ID);
		if (data != null)
			System.out.println(Arrays.toString(data));
		try {
			output.writeInt(ID);
			output.write(comType);
			if (data != null) {
				for (byte octet : data) {
					output.writeByte(octet);
				}
			}
			output.flush();
		} catch (IOException e){
			System.out.println(e.getMessage());
			System.out.println("Kan ej skicka meddelande till arduino. ");
		}
	}
	
	/**
	 * Reads messages from its connected embedded system. 
	 */
	@Override
	public void run() {
		while (true) {
			int comType, ID;
			byte[] data = null;
			try {
				ID = input.readInt();
				comType = input.read();
				switch (comType) {
					case Communication.RESETTING:
						data = new byte[1];
						data[0] = input.readByte();
						break;
					case Communication.DISTANCE_TO_GOAL:
						data = new byte[2];
						data[0] = input.readByte();
						data[1] = input.readByte();
						break;
					case Communication.PING:
						continue;
				}
//			} catch (EOFException e) {
//				System.out.println(e.getMessage());
//				System.out.println("Arduino bortkopplad. ");
//				return;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println("Arduino bortkopplad. ");
				server.lostConnection(this);
				return;
			}
			server.receiveMessage(comType, ID, data, this);
		}
	}
}
