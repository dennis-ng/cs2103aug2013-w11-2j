/**
 * 
 */
package typetodo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import typetodo.logic.Schedule;
import typetodo.logic.ScheduleController;

/**
 * @author DennZ
 * 
 */
public class TypeToDoGui extends JFrame implements View, NativeKeyListener,
		WindowListener {

	private static TypeToDoGui mainGui;
	private static String cmd = null;
	private static FeedbackDialog feedbackDialog;
	private static ScheduleController sc;
	private static JTextField txtCmd;

	private TypeToDoGui() {

		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(0, 0, 0, 0));
		this.setAlwaysOnTop(true);
		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		CommandPanel cmdPanel = new CommandPanel();
		cmdPanel.setFrameToMinimize(this);
		cmdPanel.setFrameToClose(this);
		cmdPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		this.add(cmdPanel);
		this.pack();
		// This will center the JFrame in the middle of the screen
		this.setLocationRelativeTo(null);

		feedbackDialog = new FeedbackDialog(this);
		feedbackDialog.setLocationRelativeTo(this);
		feedbackDialog.setLocation(feedbackDialog.getX(),
				this.getY() + this.getHeight());
		feedbackDialog.pack();
		feedbackDialog.setVisible(true);

		WindowMoveAdapter wma = WindowMoveAdapter.getInstance();
		this.addMouseListener(wma);
		this.addMouseMotionListener(wma);
		wma.addComponentToMove(this);
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
		this.setVisible(true);
		txtCmd.requestFocusInWindow();
	}

	public static TypeToDoGui getInstance() {
		if (mainGui == null) {
			mainGui = new TypeToDoGui();
		}
		return mainGui;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// Initialze native hook.
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			JOptionPane.showMessageDialog(null,
					"There was a problem registering the native hook.");
			ex.printStackTrace();
		}

		GlobalScreen.getInstance().addNativeKeyListener(this);
	}

	public void windowClosed(WindowEvent e) {
		// Clean up the native hook.
		GlobalScreen.unregisterNativeHook();
		System.runFinalization();
		System.exit(0);
	}

	public void windowClosing(WindowEvent e) { /* Unimplemented */
	}

	public void windowIconified(WindowEvent e) { /* Unimplemented */
	}

	public void windowDeiconified(WindowEvent e) { /* Unimplemented */
	}

	public void windowActivated(WindowEvent e) { /* Unimplemented */
	}

	public void windowDeactivated(WindowEvent e) { /* Unimplemented */
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VK_SPACE
				&& e.getModifiers() == NativeKeyEvent.CTRL_MASK) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (getState() == JFrame.ICONIFIED) {
						setExtendedState(JFrame.NORMAL);
						toFront();
						txtCmd.requestFocusInWindow();
					} else {
						setExtendedState(JFrame.ICONIFIED);
					}
				}
			});
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) { /* Unimplemented */
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) { /* Unimplemented */
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
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TypeToDoGui cmdFrame = new TypeToDoGui();
				try {
					sc = new ScheduleController(cmdFrame, new Schedule());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
