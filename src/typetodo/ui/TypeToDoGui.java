/**
 * 
 */
package typetodo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextField;

import typetodo.logic.Schedule;
import typetodo.logic.ScheduleController;
import typetodo.model.Task;

/**
 * @author DennZ
 * 
 */
public class TypeToDoGui extends JFrame implements View {

	private static TypeToDoGui mainGui;
	private static String cmd = null;
	private static FeedbackDialog feedbackDialog;
	private static ScheduleController sc;

	private TypeToDoGui() {

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

	public static void setCmd(String input) {
		cmd = input;
		System.out.println(cmd + "first");
	}

	@Override
	public void displayFeedBack(String feedBack) {
		feedbackDialog.setFeedbackText(feedBack);
	}

	@Override
	public void displayErrorMessage(String errorMessage) {
		feedbackDialog.setFeedbackText(errorMessage);
	}

	@Override
	public void displayTasks(String tasks) {
		feedbackDialog.setTableOfTasks(tasks.trim());
	}

	@Override
	public void displayHelp(String helpMessage) {
		System.out.println(helpMessage);
		feedbackDialog.setTableOfTasks(helpMessage);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
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

		feedbackDialog = new FeedbackDialog(cmdFrame);
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
		txtCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField) e.getSource();
				sc.parseAndExecute(source.getText());
				source.setText("");
			}
		});

		sc = new ScheduleController(cmdFrame, new Schedule());
	}
}
