import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * @author Tuan Nam Davaux, Laetitia Courgey and Samuel Cohen
 * @since 2019-05-26
 *        <p>
 *        <b>Handles all communication with the Server</b>
 *        </p>
 */
public class ConnectionHandler implements Runnable {
	int score = 0, allowedAttempts, port;
	String word, guessedLetters, ip;
	Message msg;
	Panel panel;
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	String name;

	ConnectionHandler(String ip, int port, Panel p, String s) {
		this.ip = ip;
		this.port = port;
		panel = p;
		name = s;
	}

	/**
	 * Sends a message to the server and flushes the stream
	 */
	public void sendMessage(Message msg) {

		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(panel, "Could not communicate with the server!", "Communication Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void run() {
		if (!connect()) // If a connection was not made then it's pointless to go on
			return;
		// Notify the server that the
		// client wants to start a new game
		sendMessage(new Message(Message.NEW_GAME, 0, 0, "", "", name));

		while (!socket.isClosed()) // Run while a connection is maintained
		{
			try {
				Message message = (Message) in.readObject();

				if (message != null) // The server sent a message
				{
					if (message.flag == Message.WIN) // The client won
					{
						panel.changeImage(message);
						panel.winOrLose(true, message);
						sendMessage(new Message(Message.NEW_GAME, 0, 0, "", "", name)); // Start a new game
					} else if (message.flag == Message.LOSE) // The client lost
					{
						panel.changeImage(message);
						panel.winOrLose(false, message);
						sendMessage(new Message(Message.NEW_GAME, 0, 0, "", "", name)); // Start a new game
					}
					// The client guessed right/wrong or requested to start a new game
					else if (message.flag == Message.RIGHT_GUESS || message.flag == Message.WRONG_GUESS
							|| message.flag == Message.NEW_GAME) {
						if (message.flag == Message.WRONG_GUESS && message.word.length() == 1) {
							panel.addInvalidLetter(message.word);
						}
						if (message.flag == Message.WRONG_GUESS && message.allowedAttempts == 1) {
							JOptionPane.showMessageDialog(panel, "There is only one attempt left !", "Last attempt",
									JOptionPane.INFORMATION_MESSAGE);
						}
						panel.updateInfo(message);
					}

					if (message.flag == Message.WRONG_GUESS) {
						panel.changeImage(message);
					} else if (message.flag == Message.CLOSE_CONNECTION) // The server has terminated the connection
						disconnect();
				} else
					Thread.yield(); // The thread gives up its timeslice if the client does not send anything
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Connects to a server
	 */
	public boolean connect() {

		try {
			socket = new Socket(ip, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			panel.enablePlaying(true);
			return true;
		} catch (Exception e) {
			panel.enablePlaying(false);
			JOptionPane.showMessageDialog(panel, "Could not establish a connection!", "Connection error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * Disconnects from the connected server
	 */
	public boolean disconnect() {

		try {
			sendMessage(new Message(Message.CLOSE_CONNECTION));
			out.close();
			in.close();
			socket.close();
			out = null;
			in = null;

			// Clear the client
			panel.updateInfo(new Message(0, 0, 0, "", ""));
			panel.score.setText("Score: 0");
			return true;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(panel, "Could not terminate connection!", "Disconnection error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}