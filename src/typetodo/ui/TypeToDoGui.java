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
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import typetodo.logic.Scheduler;
import typetodo.model.Task;

/**
 * @author DennZ
 * 
 */
public class TypeToDoGui extends JFrame implements View {

	private static TypeToDoGui mainGui;
	private static String cmd = null;
	private static JLabel lblFeedback;
	private static JTextArea txtListOutput;
	private static FeedbackDialog feedbackDialog;
	private static Scheduler sc;

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
				sc.listenForCommands(source.getText());
				source.setText("");
			}
		});
		lblFeedback = feedbackDialog.getFeedbackLabel();
		txtListOutput = feedbackDialog.getOutputBox();

		sc = new Scheduler(cmdFrame);
	}

	public static void setCmd(String input) {
		cmd = input;
		System.out.println(cmd + "first");
	}

	@Override
	public String getUserInput() {
		return null;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void displayFeedBack(String feedBack) {
		lblFeedback.setText(feedBack);
	}

	@Override
	public void displayErrorMessage(String errorMessage) {
		lblFeedback.setText(errorMessage);
	}

	@Override
	public void displayTasks(ArrayList<Task> tasks) {
		String output = "";
		int index = 1;
		for (Task task : tasks) {
			output += index + ". " + task + "\n";
			index++;
		}
		txtListOutput.setText(output);
		feedbackDialog.pack();
	}

	@Override
	public void displayHelp(String helpMessage) {
		System.out.println(helpMessage);
		txtListOutput.setText(helpMessage);
		feedbackDialog.pack();
	}

}
