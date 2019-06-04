import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Tuan Nam Davaux, Laetitia Courgey and Samuel Cohen
 * @since 2019-05-26
 *        <p>
 *        <b>Pendu Game Panel with all features</b>
 *        </p>
 */
public class Panel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionHandler connectionHandler;

	// The components of the panel
	public JTextField input = new JTextField(14);
	public JTextField ip = new JTextField(9);
	public JTextField port = new JTextField(4);
	public JLabel ipLabel = new JLabel("<html><font color = black size = 3>IP: </font></html>");
	public JLabel portLabel = new JLabel("<html><font color = black size = 3>Port: </font></html>");
	public JLabel score = new JLabel("Score: 0");
	public JLabel allowedAttempts = new JLabel("Attempts left: 0");
	public JLabel guessedLetters = new JLabel("<html><font size = 5></font></html>");
	public JButton connect = new JButton("Connect");
	public JButton disconnect = new JButton("Disconnect");
	public JButton guess = new JButton("Guess");
	public JLabel player = new JLabel();
	private ImageLabel imageLabel;
	private JPanel rightContent = new JPanel();
	private JLabel invalidLetters = new JLabel("Lettres propos�es: ");
	private ArrayList<String> letters = new ArrayList<String>();

	private int evolution_pendu = 0;

	private Dimension dimension = new Dimension();

	Panel() {
		this.setBackground(Color.darkGray);
		this.imageLabel = new ImageLabel();
		this.imageLabel.setPreferredSize(new Dimension(500, 400));
		this.imageLabel.setVerticalAlignment(JLabel.CENTER);
		this.imageLabel.setImagePath("images/accueil.jpg");

		this.dimension = new Dimension(400, 530);

		JPanel leftContent = new JPanel();
		leftContent.setPreferredSize(this.dimension);

		// Show info
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(0, 50, 15));
		p.add(player);
		p.add(allowedAttempts);
		p.add(score);
		p.setPreferredSize(new Dimension(400, 50));
		leftContent.add(p, BorderLayout.CENTER);

		p = new JPanel();
		invalidLetters.setPreferredSize(new Dimension(200, 40));
		p.add(invalidLetters);
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
		leftContent.setBackground(Color.darkGray);

		// Show connection info
		p = new JPanel();
		p.add(ipLabel);
		p.add(ip);
		p.add(portLabel);
		p.add(port);
		p.setPreferredSize(new Dimension(400, 100));
		leftContent.add(p, BorderLayout.CENTER);

		// Show new/end game buttons
		p = new JPanel();
		p.add(connect);
		p.add(disconnect);
		p.setBackground(Color.darkGray);
		leftContent.add(p);
		this.add(leftContent);

		rightContent.setPreferredSize(this.dimension);
		rightContent.add(this.imageLabel, BorderLayout.CENTER);
		rightContent.setBackground(Color.darkGray);
		this.add(rightContent);

		// Add listeners
		connect.addActionListener(this);
		disconnect.addActionListener(this);
		guess.addActionListener(this);

		// Hide/Disable input
		disconnect.setEnabled(false);
		disconnect.setVisible(false);
		guess.setEnabled(false);
		input.setEnabled(false);

		// Set default values
		ip.setText("127.0.0.1");
		port.setText("81");
	}

	/**
	 * Notifies the user that it has won/lost
	 */
	public void winOrLose(boolean win, Message msg) {

		score.setText("Score: " + msg.score);
		allowedAttempts.setText("Attempts left: 0");
		letters = new ArrayList<String>();
		if (win) {
			guessedLetters.setText("<html><font size = 5>" + msg.word + "</font></html>");
			JOptionPane.showMessageDialog(this,
					"Congratulations! " + msg.name + " has won! \nScores: \n" + msg.resultats);
		} else {
			guessedLetters.setText("");
			JOptionPane.showMessageDialog(this, "Game Over! " + msg.name + " has lost! \nScores: \n" + msg.resultats);
		}
		input.setText("");
		invalidLetters.setText("Lettres propos�es: ");
	}

	public ArrayList<String> addInvalidLetter(String l) {
		if (!letters.contains(l))
			letters.add(l);
		return letters;
	}

	public void updateInfo(Message msg) {
		if (msg.letters.size() != 0)
			letters = msg.letters;
		else
			invalidLetters.setText("Lettres propos�es: " + letters.toString());
		allowedAttempts.setText("Attempts left: " + msg.allowedAttempts);
		invalidLetters.setText("Lettres propos�es: " + letters.toString());
		guessedLetters.setText("<html><font size = 5>" + msg.guessedLetters + "</font></html>");
		if (msg.flag == Message.NEW_GAME) {
			this.player.setText(msg.name);
			if (msg.allowedAttempts != 8) {
				this.imageLabel.setImagePath("images/pendu" + (7 - msg.allowedAttempts) + ".jpg");
				evolution_pendu = 8 - msg.allowedAttempts;
			} else {
				this.imageLabel.setImagePath("images/accueil.jpg");
			}
		}
	}

	public void changeImage(Message msg) {
		this.imageLabel.setImagePath("images/pendu" + evolution_pendu + ".jpg");
		evolution_pendu++;
		if (evolution_pendu == 8) {
			evolution_pendu = 0;
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
		} else if (e.getSource() == guess) // Client takes a guess
		{
			if (!input.getText().isEmpty()) {
				connectionHandler.sendMessage(new Message(0, input.getText()));
			} else
				JOptionPane.showMessageDialog(this, "Please enter at least one letter!", "Invalid Input",
						JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Enables/Disables input
	 */
	public void enablePlaying(boolean enable) {

		ip.setEnabled(!enable);
		port.setEnabled(!enable);
		connect.setVisible(!enable);
		connect.setEnabled(!enable);
		disconnect.setVisible(enable);
		disconnect.setEnabled(enable);
		guess.setEnabled(enable);
		input.setEnabled(enable);
	}

}