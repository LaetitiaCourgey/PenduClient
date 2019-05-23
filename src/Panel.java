import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/***************************************************************************************
 * Written by: Simon Cicek * Last changed: 2012-04-13 *
 ***************************************************************************************/

public class Panel extends JPanel implements ActionListener {
	public ConnectionHandler connectionHandler;

	// The components of the panel
	public JTextField input = new JTextField(14);
	public JTextField ip = new JTextField(9);
	public JTextField port = new JTextField(4);
	public JLabel ipLabel = new JLabel("<html><font color = white size = 3>IP: </font></html>");
	public JLabel portLabel = new JLabel("<html><font color = white size = 3>Port: </font></html>");
	public JLabel score = new JLabel("Score: 0");
	public JLabel allowedAttempts = new JLabel("Attempts left: 0");
	public JLabel guessedLetters = new JLabel("<html><font size = 5></font></html>");
	public JButton connect = new JButton("Connect");
	public JButton disconnect = new JButton("Disconnect");
	// public JButton newGame = new JButton("New Game");
	public JButton guess = new JButton("Guess");
	public JLabel player = new JLabel();
	private ImageLabel imageLabel;
	private JPanel rightContent = new JPanel();

	private int evolution_pendu = 0;

	private Dimension dimension = new Dimension();

	Panel() {
		// this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.imageLabel = new ImageLabel();
		this.imageLabel.setPreferredSize(new Dimension(300, 300));
		this.imageLabel.setVerticalAlignment(JLabel.CENTER);
		this.imageLabel.setImagePath("images/accueil.jpg");

		this.dimension = new Dimension(400, 530);


		JPanel leftContent = new JPanel();
		leftContent.setPreferredSize(this.dimension);
		// leftContent.setBackground(Color.darkGray);

		// Show info
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(0, 50, 15));
		p.add(player);
		p.add(allowedAttempts);
		p.add(score);
		p.setPreferredSize(new Dimension(400, 50));
		leftContent.add(p, BorderLayout.CENTER);

		// Show output
		p = new JPanel();
		p.add(guessedLetters);
		p.setPreferredSize(new Dimension(400, 50));
		leftContent.add(p, BorderLayout.CENTER);

		// Show input
		p = new JPanel();
		p.add(input);
		p.add(guess);
		p.setPreferredSize(new Dimension(400, 50));
		leftContent.add(p, BorderLayout.CENTER);

		// Show "Connected to:"
		p = new JPanel();
		p.add(new JLabel("<html><font color = white size = 5>Connected to:</font></html>"));
		p.setBackground(Color.darkGray);
		p.setPreferredSize(new Dimension(400, 100));
		leftContent.add(p, BorderLayout.CENTER);

		// Show connection info
		p = new JPanel();
		p.add(ipLabel);
		p.add(ip);
		p.add(portLabel);
		p.add(port);
		p.setBackground(Color.darkGray);
		p.setPreferredSize(new Dimension(400, 100));
		leftContent.add(p, BorderLayout.CENTER);

		// Show new/end game buttons
		p = new JPanel();
		p.add(connect);
		p.add(disconnect);
		// p.add(newGame);
		p.setBackground(Color.darkGray);
		leftContent.add(p);
		this.add(leftContent);

		rightContent.setPreferredSize(this.dimension);
		rightContent.add(this.imageLabel, BorderLayout.CENTER);
		rightContent.setBackground(Color.white);
		this.add(rightContent);


		// Add listeners
		connect.addActionListener(this);
		disconnect.addActionListener(this);
		// newGame.addActionListener(this);
		guess.addActionListener(this);

		// Hide/Disable input
		disconnect.setEnabled(false);
		disconnect.setVisible(false);
		// newGame.setEnabled(false);
		guess.setEnabled(false);
		input.setEnabled(false);

		// Set default values
		ip.setText("127.0.0.1");
		port.setText("81");
	}

	// Notifies the user that it has won/lost
	public void winOrLose(boolean win, Message msg) {
		score.setText("Score: " + msg.score);
		allowedAttempts.setText("Attempts left: 0");
		if (win) {
			guessedLetters.setText("<html><font size = 5>" + msg.word + "</font></html>");
			JOptionPane.showMessageDialog(this,
					"Félicitations! " + msg.name + " a gagné! \nScores: \n" + msg.resultats);
		} else {
			guessedLetters.setText("");
			JOptionPane.showMessageDialog(this,
					"Game Over! " + msg.name + " a perdu! \nScores: \n" + msg.resultats);
		}
		input.setText("");
	}

	public void updateInfo(Message msg) {
		allowedAttempts.setText("Attempts left: " + msg.allowedAttempts);
		guessedLetters.setText("<html><font size = 5>" + msg.guessedLetters + "</font></html>");
		if (msg.flag == Message.NEW_GAME) {
			this.player.setText(msg.name);
			if (msg.allowedAttempts != 8) {
				this.imageLabel.setImagePath("images/pendu" + (7 - msg.allowedAttempts) + ".jpg");
				evolution_pendu = 8 - msg.allowedAttempts;
			}
			else {
				this.imageLabel.setImagePath("images/accueil.jpg");
			}
		}
	}

	public void changeImage(Message msg) {
		this.imageLabel.setImagePath("images/pendu" + evolution_pendu + ".jpg");
		System.out.println("hhh");
		evolution_pendu++;
		if (evolution_pendu == 8) {
			evolution_pendu = 0;
			// this.imageLabel.removeImagePath();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connect) // Client wants to connect to a server
		{
			int p = 81; // Default port
			try {
				p = Integer.parseInt(port.getText());
			} // Get an int from the port textfield
			catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Please enter a valid port!", "Invalid Port!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			connectionHandler = new ConnectionHandler(ip.getText(), p, this); // Initialize the handler
			new Thread(connectionHandler).start(); // Run the handler in a new thread
		} else if (e.getSource() == disconnect) // Client wants to disconnect from the server
		{
			try {
				connectionHandler.disconnect();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Could not terminate connection!", "Disconnection error",
						JOptionPane.ERROR_MESSAGE);
			}
			enablePlaying(false); // When the client is not connected to a server, disable all input
		}
//		else if (e.getSource() == newGame) // Client wants to start a new game
//			connectionHandler.sendMessage(new Message(Message.NEW_GAME));
		else if (e.getSource() == guess) // Client takes a guess
		{
			if (!input.getText().isEmpty()) {
				System.out.println("a");
				connectionHandler.sendMessage(new Message(0, input.getText()));
			} else
				JOptionPane.showMessageDialog(this, "Please enter at least one letter!", "Invalid Input",
						JOptionPane.ERROR_MESSAGE);
		}
	}

	// Enables/Disables input
	public void enablePlaying(boolean enable) {
		ip.setEnabled(!enable);
		port.setEnabled(!enable);
		connect.setVisible(!enable);
		connect.setEnabled(!enable);
		disconnect.setVisible(enable);
		disconnect.setEnabled(enable);
		// newGame.setEnabled(enable);
		guess.setEnabled(enable);
		input.setEnabled(enable);
	}


}