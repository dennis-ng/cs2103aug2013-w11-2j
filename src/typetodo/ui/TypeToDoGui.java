/**
 * 
 */
package typetodo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author DennZ
 * 
 */
public class TypeToDoGui extends JFrame {

	private static TypeToDoGui mainGui;

	private TypeToDoGui() {

		// FeedbackDialog feedbackDialog = new FeedbackDialog(this);

		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(0, 0, 0, 0));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static TypeToDoGui getInstance() {
		if (mainGui == null) {
			mainGui = new TypeToDoGui();
		}
		return mainGui;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JTextField txtCmd;
		TypeToDoGui cmdFrame = new TypeToDoGui();

		CommandPanel cmdPanel = new CommandPanel();
		cmdPanel.setFrameToMinimize(cmdFrame);
		cmdPanel.setFrameToClose(cmdFrame);
		cmdFrame.add(cmdPanel);
		cmdFrame.pack();
		cmdFrame.setVisible(true);
		// This will center the JFrame in the middle of the screen
		cmdFrame.setLocationRelativeTo(null);

		FeedbackDialog feedbackDialog = new FeedbackDialog(cmdFrame);
		feedbackDialog.setLocationRelativeTo(cmdFrame);
		feedbackDialog.setLocation(feedbackDialog.getX(), cmdFrame.getY()
				+ cmdFrame.getHeight());
		feedbackDialog.pack();
		feedbackDialog.setVisible(true);

		WindowMoveAdapter wma = WindowMoveAdapter.getInstance();
		cmdFrame.addMouseListener(wma);
		cmdFrame.addMouseMotionListener(wma);
		wma.addComponentToMove(cmdFrame);
		feedbackDialog.addMouseListener(wma);
		feedbackDialog.addMouseMotionListener(wma);
		wma.addComponentToMove(feedbackDialog);

		txtCmd = cmdPanel.getTxtCmd();
		String cmd = "";
		txtCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField) e.getSource();
				// cmd = source.getText();
				source.setText("");
			}
		});
	}
}
