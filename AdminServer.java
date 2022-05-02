package server;

import database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AdminServer adds a GUI to Server, and an admin can supervise the game. 
 *  
 * @author Sebastian Hoggard
 *
 */
public class AdminServer extends Server {
	private static class BoxOption {
		public String string;
		public int comType;
		
		public BoxOption(String string, int comType) {
			this.string = string;
			this.comType = comType;
		}
		
		@Override
		public String toString(){
			return string;
		}
	}
	private static final BoxOption[] comTypes_client = {new BoxOption("Avböj", Communication.REFUSE), new BoxOption("Spela spel", Communication.PLAY_GAME), 
		new BoxOption("Pausa spel", Communication.PAUSE_GAME), new BoxOption("Stoppa spel", Communication.STOP_GAME), new BoxOption("Vinnare", Communication.WIN), 
		new BoxOption("Förlorare", Communication.LOSE), new BoxOption("Starta nedräkning", Communication.COUNTDOWN), 
		new BoxOption("Acceptera inloggning", Communication.LOGIN_OK), new BoxOption("Avböj inloggning", Communication.LOGIN_ERROR), 
		new BoxOption("Avböj registrering", Communication.REGISTER_ERROR)};
	private static final BoxOption[] comTypes_embSys = {new BoxOption("Uppdatera spelare 1", Communication.UPDATE_PLAYER_1), 
		new BoxOption("Uppdatera spelare 2", Communication.UPDATE_PLAYER_2), new BoxOption("Återställ banor", Communication.RESET_COURSE), 
		new BoxOption("Pausa banor", Communication.PAUSE_COURSE), new BoxOption("Återuppta banor", Communication.RESUME_COURSE), 
		new BoxOption("Uppdatera spelare", Communication.UPDATE_PLAYERS)};
	
	private AdminGUI GUI;
	private AdminConsole console;
	
	public AdminServer() {
		super();
		this.GUI = new AdminGUI();
		this.console = new AdminConsole();
		JFrame frame_GUI = new JFrame("Server Admin");
		frame_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame_GUI.add(GUI);
		frame_GUI.pack();
		frame_GUI.setVisible(true);
		console.setLocation(GUI.getBounds().x, GUI.getBounds().y + GUI.getBounds().height);
		update.doClick();
	}
	
	private class AdminGUI extends JPanel implements ActionListener {
		private JLabel label_connections = new JLabel("Anslutning");
		private JLabel label_comTypes = new JLabel("ComType");
		private JLabel label_data = new JLabel("Data");
		private JLabel label_send = new JLabel("Skicka");
		private JLabel label_drop = new JLabel("Koppla bort");

		private JLabel label_player1 = new JLabel("Spelare 1: ");
		private JLabel label_player2 = new JLabel("Spelare 2: ");
		private JLabel label_players = new JLabel("Båda spelare: ");
		private JLabel label_embSys = new JLabel("Arduino (offline): ");
		private JLabel label_queuedPlayer = new JLabel("Köad spelare: ");
		private JComboBox<BoxOption> com_player1 = new JComboBox<BoxOption>(comTypes_client);
		private JComboBox<BoxOption> com_player2 = new JComboBox<BoxOption>(comTypes_client);
		private JComboBox<BoxOption> com_players = new JComboBox<BoxOption>(comTypes_client);
		private JComboBox<BoxOption> com_embSys = new JComboBox<BoxOption>(comTypes_embSys);
		private JComboBox<BoxOption> com_queuedPlayer = new JComboBox<BoxOption>(comTypes_client);
		private JTextField data_player1 = new JTextField();
		private JTextField data_player2 = new JTextField();
		private JTextField data_players = new JTextField();
		private JTextField data_embSys = new JTextField();
		private JTextField data_queuedPlayer = new JTextField();
		private JButton send_player1 = new JButton("Skicka");
		private JButton send_player2 = new JButton("Skicka");
		private JButton send_players = new JButton("Skicka");
		private JButton send_embSys = new JButton("Skicka");
		private JButton send_queuedPlayer = new JButton("Skicka");
		private JButton drop_player1 = new JButton("Koppla bort");
		private JButton drop_player2 = new JButton("Koppla bort");
		private JButton drop_players = new JButton("Koppla bort");
		private JButton drop_embSys = new JButton("Koppla bort");
		private JButton drop_queuedPlayer = new JButton("Koppla bort");
		
		private JComboBox<Object> queueBox = new JComboBox<Object>(playerQueue.getArray());
		private JButton btn_setPlayer1 = new JButton("Sätt till spelare 1");
		private JButton btn_setPlayer2 = new JButton("Sätt till spelare 2");
		
		private JLabel label_state = new JLabel("Nuvarande tillstånd: ");
		private JLabel label_currentState = new JLabel(state.toString());
		private JButton btn_startGame = new JButton("Försök starta spel");
		private JLabel label_states = new JLabel("Tillstånds-väljare: ");
		private JComboBox<State> statesBox = new JComboBox<State>(Server.State.values());
		private JButton changeState = new JButton("Byt server-tillstånd");
		
		private JLabel label_logics = new JLabel("Server-logik: ");
		private JButton toggle_logics = new JButton("Automatisk");
		
		private JLabel label_console = new JLabel("Konsoll-fönster: ");
		private JButton toggle_console = new JButton("Visa");
		
		private JLabel label_gameMode = new JLabel("Spelläge: ");
		private JLabel label_currentGameMode = new JLabel("Väntar på spel");

		private JButton btn_openDatabase = new JButton("Redigera databas");

		private Dimension size_col1 = new Dimension(100, 30);
		private Dimension size_col2 = new Dimension(150, 30);
		private Dimension size_col3 = new Dimension(150, 30);
		private Dimension size_col4 = new Dimension(100, 30);
		private Dimension size_col5 = new Dimension(100, 30);
		
		AdminGUI() {
			setPreferredSize(new Dimension(700, 500));

			label_connections.setPreferredSize(size_col1);
			add(label_connections);
			label_comTypes.setPreferredSize(size_col2);
			add(label_comTypes);
			label_data.setPreferredSize(size_col3);
			add(label_data);
			label_send.setPreferredSize(size_col4);
			add(label_send);
			label_drop.setPreferredSize(size_col5);
			add(label_drop);

			label_player1.setPreferredSize(size_col1);
			add(label_player1);
			com_player1.setPreferredSize(size_col2);
			add(com_player1);
			data_player1.setPreferredSize(size_col3);
			add(data_player1);
			send_player1.setPreferredSize(size_col4);
			send_player1.addActionListener(this);
			add(send_player1);
			drop_player1.setPreferredSize(size_col5);
			drop_player1.addActionListener(this);
			add(drop_player1);

			label_player2.setPreferredSize(size_col1);
			add(label_player2);
			com_player2.setPreferredSize(size_col2);
			add(com_player2);
			data_player2.setPreferredSize(size_col3);
			add(data_player2);
			send_player2.setPreferredSize(size_col4);
			send_player2.addActionListener(this);
			add(send_player2);
			drop_player2.setPreferredSize(size_col5);
			drop_player2.addActionListener(this);
			add(drop_player2);

			label_players.setPreferredSize(size_col1);
			add(label_players);
			com_players.setPreferredSize(size_col2);
			add(com_players);
			data_players.setPreferredSize(size_col3);
			add(data_players);
			send_players.setPreferredSize(size_col4);
			send_players.addActionListener(this);
			add(send_players);
			drop_players.setPreferredSize(size_col5);
			drop_players.addActionListener(this);
			add(drop_players);

			label_embSys.setPreferredSize(size_col1);
			add(label_embSys);
			com_embSys.setPreferredSize(size_col2);
			add(com_embSys);
			data_embSys.setPreferredSize(size_col3);
			add(data_embSys);
			send_embSys.setPreferredSize(size_col4);
			send_embSys.addActionListener(this);
			add(send_embSys);
			drop_embSys.setPreferredSize(size_col5);
			drop_embSys.addActionListener(this);
			add(drop_embSys);
			
			JComponent padding10 = new JLabel();
			padding10.setPreferredSize(new Dimension(650, 20));
			padding10.requestFocusInWindow();
			add(padding10);

			JComponent padding11 = new JLabel();
			padding11.setPreferredSize(new Dimension(60, 30));
			add(padding11);
			queueBox.setPreferredSize(new Dimension(200, 30));
			add(queueBox);
			btn_setPlayer1.setPreferredSize(new Dimension(150, 30));
			btn_setPlayer1.addActionListener(this);
			add(btn_setPlayer1);
			btn_setPlayer2.setPreferredSize(new Dimension(150, 30));
			btn_setPlayer2.addActionListener(this);
			add(btn_setPlayer2);
			JComponent padding12 = new JLabel();
			padding12.setPreferredSize(new Dimension(60, 30));
			add(padding12);
			
			label_queuedPlayer.setPreferredSize(size_col1);
			add(label_queuedPlayer);
			com_queuedPlayer.setPreferredSize(size_col2);
			add(com_queuedPlayer);
			data_queuedPlayer.setPreferredSize(size_col3);
			add(data_queuedPlayer);
			send_queuedPlayer.setPreferredSize(size_col4);
			send_queuedPlayer.addActionListener(this);
			add(send_queuedPlayer);
			drop_queuedPlayer.setPreferredSize(size_col5);
			drop_queuedPlayer.addActionListener(this);
			add(drop_queuedPlayer);
			
			JComponent padding1 = new JLabel();
			padding1.setPreferredSize(new Dimension(650, 20));
			padding1.requestFocusInWindow();
			add(padding1);
			
			JComponent padding2 = new JLabel();
			padding2.setPreferredSize(new Dimension(60, 30));
			add(padding2);
			label_state.setPreferredSize(new Dimension(130, 30));
			add(label_state);
			label_currentState.setPreferredSize(new Dimension(140, 30));
			add(label_currentState);
			JComponent padding9 = new JLabel();
			padding9.setPreferredSize(new Dimension(25, 30));
			add(padding9);
			btn_startGame.setPreferredSize(new Dimension(150, 30));
			add(btn_startGame);
			JComponent padding3 = new JLabel();
			padding3.setPreferredSize(new Dimension(60, 30));
			add(padding3);
			
			label_states.setPreferredSize(new Dimension(120, 30));
			add(label_states);
			statesBox.setPreferredSize(new Dimension(180, 30));
			add(statesBox);
			changeState.setPreferredSize(new Dimension(150, 30));
			changeState.addActionListener(this);
			add(changeState);
			
			JComponent padding4 = new JLabel();
			padding4.setPreferredSize(new Dimension(650, 20));
			add(padding4);
			
			label_logics.setPreferredSize(new Dimension(90, 30));
			add(label_logics);
			toggle_logics.setPreferredSize(new Dimension(100, 30));
			toggle_logics.addActionListener(this);
			add(toggle_logics);
			JComponent padding5 = new JLabel();
			padding5.setPreferredSize(new Dimension(40, 30));
			add(padding5);
			label_console.setPreferredSize(new Dimension(110, 30));
			add(label_console);
			toggle_console.setPreferredSize(new Dimension(100, 30));
			toggle_console.addActionListener(this);
			add(toggle_console);
			
			JComponent padding6 = new JLabel();
			padding6.setPreferredSize(new Dimension(650, 20));
			add(padding6);
			
			label_gameMode.setPreferredSize(new Dimension(70, 30));
			add(label_gameMode);
			label_currentGameMode.setPreferredSize(new Dimension(100, 30));
			add(label_currentGameMode);
			JComponent padding7 = new JLabel();
			padding7.setPreferredSize(new Dimension(40, 30));
			add(padding7);
			progress_player1.setPreferredSize(new Dimension(100, 30));
			progress_player1.setHorizontalAlignment(SwingConstants.CENTER);
			progress_player1.setForeground(new Color(255, 0, 0));
			add(progress_player1);
			progress_player2.setPreferredSize(new Dimension(100, 30));
			progress_player2.setHorizontalAlignment(SwingConstants.CENTER);
			progress_player2.setForeground(new Color(0, 0, 255));
			add(progress_player2);
			
			JComponent padding8 = new JLabel();
			padding8.setPreferredSize(new Dimension(650, 20));
			add(padding8);
			
			btn_openDatabase.setPreferredSize(new Dimension(150, 30));
			btn_openDatabase.addActionListener(this);
			add(btn_openDatabase);
			
			update.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == send_player1) {
				if (isOnline(player1)) {
					BoxOption option = (BoxOption)com_player1.getSelectedItem();
					String[] dataStrings = data_player1.getText().split(", ");
					byte[] data = new byte[dataStrings.length];
					if (dataStrings[0].isEmpty())
						data = null;
					else
						for (int index = 0; index < dataStrings.length; index++)
							data[index] = (byte) Integer.parseInt(dataStrings[index]);
					player1.sendMessage(option.comType, data);
				}
				else {
					update.doClick();
					System.out.println("player1 is not connected, but button is active! ");
				}
			}
			if (e.getSource() == send_player2) {
				if (isOnline(player2)) {
					BoxOption option = (BoxOption)com_player2.getSelectedItem();
					String[] dataStrings = data_player2.getText().split(", ");
					byte[] data = new byte[dataStrings.length];
					if (dataStrings[0].isEmpty())
						data = null;
					else
						for (int index = 0; index < dataStrings.length; index++)
							data[index] = (byte) Integer.parseInt(dataStrings[index]);
					player2.sendMessage(option.comType, data);
				}
				else {
					update.doClick();
					System.out.println("player2 is not connected, but button is active! ");
				}
			}
			if (e.getSource() == send_players) {
				if (isOnline(player1) && isOnline(player2)) {
					BoxOption option = (BoxOption)com_players.getSelectedItem();
					String[] dataStrings = data_players.getText().split(", ");
					byte[] data = new byte[dataStrings.length];
					if (dataStrings[0].isEmpty())
						data = null;
					else
						for (int index = 0; index < dataStrings.length; index++)
							data[index] = (byte) Integer.parseInt(dataStrings[index]);
					player1.sendMessage(option.comType, data);
					player2.sendMessage(option.comType, data);
				}
				else {
					update.doClick();
					System.out.println("Both players are not connected, but button is active! ");
				}
			}
			if (e.getSource() == send_embSys) {
				if (embSys != null) {
					BoxOption option = (BoxOption)com_embSys.getSelectedItem();
					String[] dataStrings = data_embSys.getText().split(", ");
					byte[] data = new byte[dataStrings.length];
					if (dataStrings[0].isEmpty())
						data = null;
					else
						for (int index = 0; index < dataStrings.length; index++)
							data[index] = (byte) Integer.parseInt(dataStrings[index]);
					embSys.sendMessage(option.comType, ID_embSys, data);
				}
				else {
					update.doClick();
					System.out.println("embSys is null, but button is active! ");
				}
			}
			if (e.getSource() == send_queuedPlayer) {
				if (isOnline(playerQueue.get(queueBox.getSelectedIndex()))) {
					BoxOption option = (BoxOption)com_queuedPlayer.getSelectedItem();
					String[] dataStrings = data_queuedPlayer.getText().split(", ");
					byte[] data = new byte[dataStrings.length];
					if (dataStrings[0].isEmpty())
						data = null;
					else
						for (int index = 0; index < dataStrings.length; index++)
							data[index] = (byte) Integer.parseInt(dataStrings[index]);
					playerQueue.get(queueBox.getSelectedIndex()).sendMessage(option.comType, data);
				}
				else {
					update.doClick();
					System.out.println("Queued player is not connected, is still in queue! ");
				}
			}
			if (e.getSource() == drop_player1) {
				lostConnection(player1.connection);			//Don't use this method here!
			}
			if (e.getSource() == drop_player2) {
				lostConnection(player2.connection);
			}
			if (e.getSource() == drop_players) {
				lostConnection(player1.connection);
				lostConnection(player2.connection);
			}
			if (e.getSource() == drop_embSys) {
				lostConnection(embSys);
			}
			if (e.getSource() == drop_queuedPlayer) {
				lostConnection(playerQueue.get(queueBox.getSelectedIndex()).connection);
			}
			if (e.getSource() == btn_setPlayer1) {
				player1 = playerQueue.remove(queueBox.getSelectedIndex());
			}
			if (e.getSource() == btn_setPlayer2) {
				player2 = playerQueue.remove(queueBox.getSelectedIndex());
			}
			if (e.getSource() == btn_startGame) {
				startGame();
			}
			if (e.getSource() == toggle_logics) {
				f_automatic = !f_automatic;
				toggle_logics.setText(f_automatic ? "Automatisk" : "Manuell");
				System.out.println(f_automatic ? "Automatisk logik" : "Manuell logik");
			}
			if (e.getSource() == changeState) {
				state = (State)statesBox.getSelectedItem();
				update.doClick();
			}
			if (e.getSource() == update) {
				label_player1.setText((player1 != null) ? player1.username + ": " : "Spelare 1: ");
				send_player1.setEnabled(isOnline(player1));
				drop_player1.setEnabled(isOnline(player1));
				label_player2.setText((player2 != null) ? player2.username + ": " : "Spelare 2: ");
				send_player2.setEnabled(isOnline(player2));
				drop_player2.setEnabled(isOnline(player2));
				send_players.setEnabled(isOnline(player1) && isOnline(player2));
				drop_players.setEnabled(isOnline(player1) && isOnline(player2));
				label_embSys.setText((embSys != null) ? "Arduino: " : "Arduino (offline): ");
				send_embSys.setEnabled(embSys != null);
				drop_embSys.setEnabled(embSys != null);
				btn_setPlayer1.setEnabled(playerQueue.size() > 0);
				btn_setPlayer2.setEnabled(playerQueue.size() > 0);
				label_queuedPlayer.setText((playerQueue.size() > 0) ? playerQueue.get(queueBox.getSelectedIndex()).username + ": " : "Köad spelare: ");
				send_queuedPlayer.setEnabled(isOnline(playerQueue.get(queueBox.getSelectedIndex())));
				drop_queuedPlayer.setEnabled(isOnline(playerQueue.get(queueBox.getSelectedIndex())));
				queueBox = new JComboBox<Object>(playerQueue.getArray());
				label_currentState.setText(state.toString());
				label_currentGameMode.setText((gameMode != null) ? gameMode.toString() : "Väntar på spel");
			}
			if (e.getSource() == toggle_console) {
				toggle_console.setText(console.toggleConsole() ? "Dölj" : "Visa");
			}
			if (e.getSource() == btn_openDatabase) {
				new AdminFrame();
			}
		}
	}
	
	public static void main(String[] args) {
		AdminServer adminServer = new AdminServer();
	}
}
