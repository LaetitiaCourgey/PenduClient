import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author Tuan Nam Davaux, Laetitia Courgey and Samuel Cohen
 * @since 2019-05-26
 *        <p>
 *        <b>Client Launcher and UI</b>
 *        </p>
 */
public class Main extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Panel panel = new Panel();

	Main() {
		System.out.println("Client launched");
		this.add(panel);
		this.setTitle("Client ");
		this.setVisible(true);
		this.setResizable(false);
		// this.setLocationRelativeTo(null);
		this.setSize(new Dimension(900, 600));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		this.addWindowListener(new Adapter());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		Main m = new Main();
	}

	private class Adapter extends WindowAdapter {
		// Makes sure to clean everything up (close all connections) when the client is
		// closing
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("windowClosing");
			// Client is closed after at least one connection has been made
			if (panel.connectionHandler != null) {
				// Client is closed while a connection is maintained
				if (panel.connectionHandler.socket != null && panel.connectionHandler.out != null)
					try {
						panel.connectionHandler.disconnect();
					} catch (Exception ex) {
					}
			}
			System.exit(0);
		}
	}
}
