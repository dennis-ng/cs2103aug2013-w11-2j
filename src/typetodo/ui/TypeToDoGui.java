// @author A0097968Y

package typetodo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import typetodo.logic.MainController;
import typetodo.logic.Schedule;

public class TypeToDoGui extends JFrame implements View, NativeKeyListener,
		WindowListener {

	public static final String IMAGE_DIRECTORY = "images/";
	public static final String FILENAME_LOGO = "logo.png";
	private static final String MESSAGE_ERROR_INITIALIZING = "There is an error initializing the scheduler. Please make sure to download the latest version.";
	private static final String MESSAGE_ERROR_NATIVE_HOOK = "There was a problem registering the global hotkey.\n"
			+ "For mavericks OSX users:\n"
			+ "Please go to Preferences>Security & Privacy>Privacy>Acessibility to allow Jar Launcher.";

	private static TypeToDoGui mainGui;
	private static CommandPanel cmdPanel;
	private static JTextField txtCmd;
	private static FeedbackDialog feedbackDialog;
	private Image imgLogo;

	private static MainController sc;
	private final LinkedList<String> inputHistory;
	private int historyIndex;

	private TypeToDoGui() {

		inputHistory = new LinkedList<String>();

		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(0, 0, 0, 0));
		this.setAlwaysOnTop(true);
		addWindowListener(this);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		URL imgLogoUrl = TypeToDoGui.class.getResource(IMAGE_DIRECTORY
				+ FILENAME_LOGO);
		if (imgLogoUrl != null) {
			imgLogo = Toolkit.getDefaultToolkit().getImage(imgLogoUrl);
			this.setIconImage(imgLogo);
		}

		cmdPanel = new CommandPanel();
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

		// TypeToDoGui handles the components that involves knowledge of
		// ScheduleController to ensure only TypeToDoGui has the knowledge of
		// ScheduleController
		txtCmd = cmdPanel.getTxtCmd();
		txtCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = txtCmd.getText();
				if (!input.isEmpty()) {
					sc.parseAndExecute(input);
					if (historyIndex != 0) {
						// The user had navigated into the history, thus we need to clear
						// history.
						inputHistory.pop();
						historyIndex = 0; // Reset
					}
					addToHistory(input);
					txtCmd.setText("");
				}
			}

		});
		txtCmd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"traverseBackInHistory");
		txtCmd.getActionMap().put("traverseBackInHistory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String history;
				if (historyIndex == 0) {
					addToHistory(txtCmd.getText());
				}
				if (historyIndex < inputHistory.size() - 1) {
					historyIndex++;
					history = inputHistory.get(historyIndex);
					txtCmd.setText(history);
					if (history.contains(" ")) {
						txtCmd.select(history.indexOf(' ') + 1, history.length());
					}
				}
			}
		});
		txtCmd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"traverseFrontInHistory");
		txtCmd.getActionMap().put("traverseFrontInHistory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String history;
				if (historyIndex > 0) {
					historyIndex--;
					if (historyIndex == 0) {
						// Remove the last input that the user has not execute
						history = inputHistory.pop();
					} else {
						history = inputHistory.get(historyIndex);
					}
					txtCmd.setText(history);
					if (history.contains(" ")) {
						txtCmd.select(history.indexOf(' ') + 1, history.length());
					}
				}
			}
		});
		txtCmd.getInputMap()
				.put(
						KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
								KeyEvent.CTRL_DOWN_MASK), "undo");
		txtCmd.getActionMap().put("undo", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// Hotkey represents user typing undo
				sc.parseAndExecute("undo");
			}
		});
		txtCmd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"moveToDialog");
		txtCmd.getActionMap().put("moveToDialog", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				feedbackDialog.toFront();
				feedbackDialog.requestFocusInWindow();
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
			this.setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null, MESSAGE_ERROR_NATIVE_HOOK);
			this.setAlwaysOnTop(true);
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

	public void windowDeiconified(WindowEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				toFront();
				txtCmd.requestFocusInWindow();
			}
		});
	}

	public void windowActivated(WindowEvent e) { /* Unimplemented */
	}

	public void windowDeactivated(WindowEvent e) { /* Unimplemented */
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VK_SPACE
				&& e.getModifiers() == NativeKeyEvent.CTRL_MASK) {
			if (getExtendedState() == JFrame.ICONIFIED) {
				setExtendedState(JFrame.NORMAL);
			} else {
				setExtendedState(JFrame.ICONIFIED);
			}
		} else if (e.getKeyCode() == NativeKeyEvent.VK_F4
				&& e.getModifiers() == NativeKeyEvent.ALT_MASK) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (getExtendedState() == JFrame.NORMAL) {
			if (!txtCmd.hasFocus()
					&& (e.getKeyCode() == NativeKeyEvent.VK_I || e.getKeyCode() == NativeKeyEvent.VK_ENTER)) {
				toFront();
				txtCmd.requestFocusInWindow();
			}
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) { /* Unimplemented */
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) { /* Unimplemented */
		if (e.getKeyCode() == NativeKeyEvent.VK_I) {
			if (getExtendedState() == JFrame.NORMAL && !txtCmd.hasFocus()) {
				txtCmd.requestFocusInWindow();
				System.out.println(e.getKeyChar());
			}
		}
	}

	/**
	 * @param input
	 *          We want to record a maximum of 50 histories so as not to waste
	 *          memory.
	 */
	private void addToHistory(String input) {
		if (inputHistory.size() > 50) {
			inputHistory.pollLast();
		}
		inputHistory.push(input);
	}

	@Override
	public boolean disableInput() {
		txtCmd.setEnabled(false);
		return !txtCmd.isEnabled();
	}

	@Override
	public boolean enableInput() {
		txtCmd.setEnabled(true);
		return txtCmd.isEnabled();
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

	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			f = new File("RingOnRequest.lock");
			// Check if the lock exist
			if (f.exists()) {
				// if exist try to delete it
				f.delete();
			}
			// Try to get the lock
			channel = new RandomAccessFile(f, "rw").getChannel();
			lock = channel.tryLock();
			if (lock == null) {
				// File is lock by other application
				channel.close();

				throw new RuntimeException("Only 1 instance of TypeToDo can run.");
			}
			// Add shutdown hook to release lock when application shutdown
			ShutdownHook shutdownHook = new ShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownHook);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TypeToDoGui cmdFrame = new TypeToDoGui();
					try {
						sc = new MainController(cmdFrame, new Schedule());
					} catch (IOException e) {
						cmdFrame.setAlwaysOnTop(false);
						JOptionPane.showMessageDialog(null, MESSAGE_ERROR_INITIALIZING);
						e.printStackTrace();
						System.exit(0);
					}
				}
			});
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			throw new RuntimeException("Could not start process.", e);
		}
	}

	public static void unlockFile() {
		// release and delete file lock
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class ShutdownHook extends Thread {

		public void run() {
			unlockFile();
		}
	}
}
