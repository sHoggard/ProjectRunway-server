package server;

/**
 * Communication defines types of messages between parts of Project Railway. 
 * 
 * @author Sebastian Hoggard, Rasmus Svedin, Vilhelm Sassi
 *
 */
public class Communication {
	public static enum GameMode {
		CLASSIC_GAME, NUMBERS_GAME
	}
	
	//Client -> Server
	public static final int PLAY_PLAYER_1 = 0x10;
	public static final int PLAY_PLAYER_2 = 0x11;
	public static final int CANCEL = 0x12;
	public static final int CLICK = 0x13;
	public static final int PAUSE = 0x14;
	public static final int RESUME = 0x15;
	public static final int PLAY = 0x16;
	public static final int STOP = 0x17;
	public static final int LOG_IN = 0x18;
	public static final int REGISTER_PLAYER = 0x19;
	public static final int REQUEST_STATS = 0x1A;
	public static final int REQUEST_HIGHSCORE = 0x1B;
	public static final int PUNISH = 0x1C;
	
	//Server -> Client
	public static final int REFUSE = 0x20;
	public static final int PLAY_GAME = 0x21;
	public static final int PAUSE_GAME = 0x22;
	public static final int STOP_GAME = 0x23;
	public static final int WIN = 0x24;
	public static final int LOSE = 0x25;
	public static final int COUNTDOWN = 0x26;
	public static final int LOGIN_OK = 0x27;
	public static final int LOGIN_ERROR = 0x28;
	public static final int REGISTER_ERROR = 0x29;
	public static final int PLAYER_STATS = 0x2A;
	public static final int HIGHSCORE = 0x2B;
	public static final int PROGRESS = 0x2C;
	
	//Embedded system -> Server
	public static final int READY_TO_PLAY = 0x30;
	public static final int RESETTING = 0x31;
	public static final int DISTANCE_TO_GOAL = 0x32;
	
	//Server -> Embedded system
	public static final int UPDATE_PLAYER_1 = 0x40;
	public static final int UPDATE_PLAYER_2 = 0x41;
	public static final int RESET_COURSE = 0x42;
	public static final int PAUSE_COURSE = 0x43;
	public static final int RESUME_COURSE = 0x44;
	public static final int UPDATE_PLAYERS = 0x45;
	public static final int PUNISH_PLAYER_1 = 0x46;
	public static final int PUNISH_PLAYER_2 = 0x47;
	
	//-> Server
	public static final int PING = 0xFF;
}
