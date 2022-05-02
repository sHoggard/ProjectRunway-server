package server;

import database.Database;

import javax.swing.JButton;
import javax.swing.JLabel;

import server.Communication.GameMode;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Server controls games of Project Railway. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin, Vilhelm Sassi, Rasha Askar
 *
 */
public class Server {
	protected static enum State {
		WAITING_FOR_EMBSYS, WAITING_FOR_PLAYER, COUNTDOWN, PLAYING, PAUSED
	}
	
	private ClientHandler clientHandler;
	private EmbSysHandler embSysHandler;
	
	private ClickUpdater clickUpdater;
	private CountdownTimer countdownTimer;
	private LostConnectionTimer lostConnectionTimer;
	
	protected Database DB;
	
	protected Chain<Player> playerQueue;
	protected Player player1;
	protected Player player2;
	protected Connection embSys;
	protected int ID_embSys;
	
	protected State state;
	protected GameMode gameMode;
	protected boolean f_automatic;
	
	protected JButton update;
	protected JLabel progress_player1;
	protected JLabel progress_player2;
	
	protected long startTime;
	protected long totalTime;

	/**
	 * Sets up a server. 
	 */
	public Server() {
		this.clientHandler = new ClientHandler(this);
		this.embSysHandler = new EmbSysHandler(this);
		this.clickUpdater = new ClickUpdater();
		this.DB = new Database();
		this.playerQueue = new Chain<Player>();
		this.gameMode = null;
		this.f_automatic = true;
		this.update = new JButton("Update GUI");
		this.progress_player1 = new JLabel("0");
		this.progress_player2 = new JLabel("0");
		changeState(State.WAITING_FOR_EMBSYS);
	}

	/**
	 * Changes the current state of the server. 
	 * 
	 * @param newState
	 */
	protected void changeState(State newState) {
		System.out.println(newState.toString());
		
		switch (newState) {
			case WAITING_FOR_EMBSYS:
				if (embSys != null) {
					embSys.sendMessage(Communication.RESET_COURSE, ID_embSys, null);
				}
				clickUpdater.cancel();
				gameMode = null;
				player1 = null;
				player2 = null;
				embSys = null;
				ID_embSys = 0;
				startTime = 0;
				totalTime = 0;
				break;
			case WAITING_FOR_PLAYER:
				startGame();
				break;
			case COUNTDOWN:
				gameMode = player1.gameMode;
				countdownTimer = new CountdownTimer();
				break;
			case PLAYING:
				if (gameMode == GameMode.CLASSIC_GAME)
					clickUpdater = new ClickUpdater();
				player1.sendMessage(Communication.PLAY_GAME, new byte[]{1, (byte) gameMode.ordinal()});
				player2.sendMessage(Communication.PLAY_GAME, new byte[]{2, (byte) gameMode.ordinal()});
				if (state == State.PAUSED) {
					embSys.sendMessage(Communication.RESUME_COURSE, ID_embSys, null);
				}
				startTime = System.currentTimeMillis();
				break;
			case PAUSED:
				if (isOnline(player1)) {
					player1.sendMessage(Communication.PAUSE_GAME, null);
				}
				if (isOnline(player2)) {
					player2.sendMessage(Communication.PAUSE_GAME, null);
				}
				if (embSys != null) {
					embSys.sendMessage(Communication.PAUSE_COURSE, ID_embSys, null);
				}
				totalTime += System.currentTimeMillis() - startTime;
				break;
		}
		
		this.state = newState;
		update.doClick();
	}

	/**
	 * Initiates a game and starts the countdown, if two Players 
	 * in the queue want the same game mode. 
	 * 
	 * @return
	 */
	protected boolean startGame() {
		if (state != State.WAITING_FOR_PLAYER) {
			return false;
		}
		for (int checks = 0; checks < GameMode.values().length && checks < playerQueue.size(); checks++) {
			for (int index = checks + 1; index < playerQueue.size(); index++) {
				if (playerQueue.get(checks).gameMode == playerQueue.get(index).gameMode) {
					player1 = playerQueue.remove(checks);
					player2 = playerQueue.remove(index - 1);
					changeState(State.COUNTDOWN);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Receives and interprets messages from clients or embedded systems and 
	 * outputs the sender, comType and data to the console. 
	 * 
	 * @param comType
	 * @param ID
	 * @param data
	 * @param connection
	 */
	void receiveMessage(int comType, int ID, byte[] data, Connection connection) {
		if (comType != Communication.DISTANCE_TO_GOAL) {
			if (player1 != null && ID == player1.ID)
				System.out.print("Player 1: ");
			if (player2 != null && ID == player2.ID)
				System.out.print("Player 2: ");
			if (ID == ID_embSys)
				System.out.print("EmbSys: ");
			System.out.println("0x" + Integer.toHexString(comType) + " " + ID);
			if (data != null)
				System.out.println(Arrays.toString(data));
		}
		
		byte[] username = new byte[20];
		byte[] password = new byte[20];
		switch (comType) {
			//Messages from client
			case Communication.PLAY_PLAYER_1:
				if (state == State.WAITING_FOR_PLAYER && player1 == null) {
					player1 = new Player((ClientConnection) connection, ID, null, GameMode.CLASSIC_GAME);
					if (player2 != null) {
						changeState(State.COUNTDOWN);
					}
				}
				else {
					connection.sendMessage(Communication.REFUSE, ID, null);
				}
				break;
			case Communication.PLAY_PLAYER_2:
				if (state == State.WAITING_FOR_PLAYER && player2 == null) {
					player2 = new Player((ClientConnection) connection, ID, null, GameMode.CLASSIC_GAME);
					if (player1 != null) {
						changeState(State.COUNTDOWN);
					}
				}
				else {
					connection.sendMessage(Communication.REFUSE, ID, null);
				}
				break;
			case Communication.CANCEL:
				for (int index = 0; index < playerQueue.size(); index++) {
					if (connection == playerQueue.get(index).connection) {
						playerQueue.remove(index);
					}
				}
				break;
			case Communication.CLICK:
				if (state != State.PLAYING)
					break;
				switch (gameMode) {
					case CLASSIC_GAME: 
						if (ID == player1.ID) {
							player1.clicks++;
						}
						if (ID == player2.ID) {
							player2.clicks++;
						}
						break;
					case NUMBERS_GAME: 
						if (ID == player1.ID) {
							embSys.sendMessage(Communication.UPDATE_PLAYER_1, ID_embSys, null);
						}
						if (ID == player2.ID) {
							embSys.sendMessage(Communication.UPDATE_PLAYER_2, ID_embSys, null);
						}
						break;
				}
				break;
			case Communication.PAUSE:
				if (state != State.PLAYING)
					break;
				if (ID == player1.ID) {
					changeState(State.PAUSED);
				}
				if (ID == player2.ID) {
					changeState(State.PAUSED);
				}
				break;
			case Communication.RESUME:
				if (state != State.PAUSED)
					break;
				if (ID == player1.ID) {
					player1.resume((ClientConnection) connection);
				}
				if (ID == player2.ID) {
					player2.resume((ClientConnection) connection);
				}
				if (isOnline(player1) && isOnline(player2) && embSys != null) {
					lostConnectionTimer.cancel();
					lostConnectionTimer = null;
					changeState(State.COUNTDOWN);
				}
				break;
			case Communication.PLAY:
				for (int i = 0; i < 20; i++) {
					username[i] = data[i];
				}
				playerQueue.add(new Player((ClientConnection) connection, ID, new String(username).trim(), GameMode.values()[data[20]]), playerQueue.size());
				if (!startGame())
					update.doClick();
				break;
			case Communication.LOG_IN:
				for (int i = 0; i < 20; i++) {
					username[i] = data[i];
					password[i] = data[i+20];
				}
				if (DB.checkID(new String(username).trim(), new String(password).trim())) {
					connection.sendMessage(Communication.LOGIN_OK, ID, null);
				} else {
					connection.sendMessage(Communication.LOGIN_ERROR, ID, null);
				}
				break;
			case Communication.REGISTER_PLAYER:
				for (int i = 0; i < 20; i++) {
					username[i] = data[i];
					password[i] = data[i+20];
				}
				if (DB.createID(new String(username).trim(), new String(password).trim())) {
					connection.sendMessage(Communication.LOGIN_OK, ID, null);
				} else {
					connection.sendMessage(Communication.REGISTER_ERROR, ID, null);
				}
				break;
			case Communication.REQUEST_STATS:
				String user = new String(data).trim();
				((ClientConnection)connection).sendString(Communication.PLAYER_STATS, ID, DB.getPlayerStats(user));
				break;
			case Communication.REQUEST_HIGHSCORE:
				((ClientConnection)connection).sendString(Communication.HIGHSCORE, ID, DB.getGlobalHighscore());
				break;
			case Communication.PUNISH:
				if (state != State.PLAYING)
					break;
				switch (gameMode) {
					case CLASSIC_GAME: 
						if (ID == player1.ID) {
							player1.clicks -= 10;
						}
						if (ID == player2.ID) {
							player2.clicks -= 10;
						}
						break;
					case NUMBERS_GAME: 
						if (ID == player1.ID) {
							embSys.sendMessage(Communication.PUNISH_PLAYER_1, ID_embSys, null);
						}
						if (ID == player2.ID) {
							embSys.sendMessage(Communication.PUNISH_PLAYER_2, ID_embSys, null);
						}
						break;
				}
				break;
			
			//Messages from embedded system
			case Communication.READY_TO_PLAY:
				if (state == State.WAITING_FOR_EMBSYS) {
					embSys = connection;
					ID_embSys = ID;
					changeState(State.WAITING_FOR_PLAYER);
				}
				if (state == State.PAUSED) {
					embSys = connection;
					ID_embSys = ID;
					if (isOnline(player1) && isOnline(player2)) {
						changeState(State.COUNTDOWN);
						lostConnectionTimer.cancel();
						lostConnectionTimer = null;
					}
				}
				break;
			case Communication.RESETTING:
				if (state != State.PLAYING)
					break;
				totalTime += System.currentTimeMillis() - startTime;
				double time = Long.valueOf(totalTime).doubleValue();
				time /= 1000;
				System.out.println("Speltid: " + time);
				DB.win(player1.username, player2.username, time, gameMode.ordinal());
				if (data[0] == 1) {
					player1.sendMessage(Communication.WIN, new byte[]{Double.valueOf(time).byteValue()});
					player2.sendMessage(Communication.LOSE, null);
				}
				if (data[0] == 2) {
					player1.sendMessage(Communication.LOSE, null);
					player2.sendMessage(Communication.WIN, new byte[]{Double.valueOf(time).byteValue()});
				}
				changeState(State.WAITING_FOR_EMBSYS);
				break;
			case Communication.DISTANCE_TO_GOAL:
				progress_player1.setText(Byte.toString(data[0]));
				progress_player2.setText(Byte.toString(data[1]));
				if (isOnline(player1)) {
					player1.sendMessage(Communication.PROGRESS, data);
				}
				if (isOnline(player2)) {
					player2.sendMessage(Communication.PROGRESS, data);
				}
				break;
		}
	}

	/**
	 * Outputs a message when there is a loss of connection between
	 * parts of the system and creates a LostConnectionTimer if the game
	 * is paused as a result. 
	 * 
	 * @param connection
	 */
	void lostConnection(Connection connection) {
		if (connection == null) {
			System.out.println("null-objekt kortkopplat. ");
			return;
		}
		else if (player1 != null && connection == player1.connection) {
			player1.connection.f_connected = false;
			System.out.println("Player 1 bortkopplad. ");
		}
		else if (player2 != null && connection == player2.connection) {
			player2.connection.f_connected = false;
			System.out.println("Player 2 bortkopplad. ");
		}
		else if (connection == embSys) {
			embSys = null;
			System.out.println("EmbSys bortkopplad. ");
		}
		else {
			for (int index = 0; index < playerQueue.size(); index++) {
				if (connection == playerQueue.get(index).connection) {
					playerQueue.remove(index);
					index--;
				}
			}
			return;
		}
		
		switch (state) {
			case WAITING_FOR_EMBSYS:
			case WAITING_FOR_PLAYER:
				changeState(State.WAITING_FOR_EMBSYS);
				break;
			case COUNTDOWN:
				countdownTimer.cancel();
				countdownTimer = null;
				player1.sendMessage(Communication.STOP_GAME, null);
				player2.sendMessage(Communication.STOP_GAME, null);
				changeState(State.WAITING_FOR_EMBSYS);
				break;
			case PLAYING:
			case PAUSED:
				lostConnectionTimer = new LostConnectionTimer();
				changeState(State.PAUSED);
				break;
		}
		
		update.doClick();
	}
	
	/**
	 * Replaces ConnectionHandlers that have failed. 
	 */
	public void failedHandler(ConnectionHandler handler) {
		if (handler == clientHandler) {
			clientHandler = new ClientHandler(this);
		}
		else if (handler == embSysHandler) {
			embSysHandler = new EmbSysHandler(this);
		}
	}

	private class ClickUpdater extends TimerTask {
		private Timer timer;
		
		ClickUpdater() {
			this.timer = new Timer();
			timer.scheduleAtFixedRate(this, 0, 500);
		}
		
		@Override
		public void run() {
			if (state == State.PLAYING) {
				byte[] clicks = new byte[2];
				switch (gameMode) {
					case CLASSIC_GAME:
						clicks[0] = (byte) (player1.clicks/2);
						player1.clicks = player1.clicks%2;
						clicks[1] = (byte) (player2.clicks/2);
						player2.clicks = player2.clicks%2;
						break;
					case NUMBERS_GAME:
						clicks[0] = (byte) player1.clicks;
						player1.clicks = 0;
						clicks[1] = (byte) player2.clicks;
						player2.clicks = 0;
						break;
				}
				embSys.sendMessage(Communication.UPDATE_PLAYERS, ID_embSys, clicks);
			}
		}
	}
	
	private class CountdownTimer extends TimerTask {
		private Timer timer;
		private byte countdown;
		
		CountdownTimer() {
			this.timer = new Timer();
			this.countdown = 3;
			timer.scheduleAtFixedRate(this, 0, 1000);
		}
		
		@Override
		public void run() {
			if (countdown == 0) {
				changeState(State.PLAYING);
				cancel();
				countdownTimer = null;
				return;
			}
			player1.sendMessage(Communication.COUNTDOWN, new byte[]{countdown});
			player2.sendMessage(Communication.COUNTDOWN, new byte[]{countdown});
			countdown--;
		}
	}
	
	private class LostConnectionTimer extends TimerTask {
		private Timer timer;
		
		LostConnectionTimer() {
			this.timer = new Timer();
			timer.scheduleAtFixedRate(this, 30000, 1000);
		}
		
		@Override
		public void run() {
			System.out.println("Resetting after lost connection. ");
			if (player1 != null) {
				player1.sendMessage(Communication.STOP_GAME, null);
			}
			if (player2 != null) {
				player2.sendMessage(Communication.STOP_GAME, null);
			}
			changeState(State.WAITING_FOR_EMBSYS);
			cancel();
			lostConnectionTimer = null;
		}
	}
	
	protected static boolean isOnline(Player player) {
		return (player != null && player.connection.f_connected);
	}
	
	public static void main(String[] args) {
		Server server = new Server();
	}
}
