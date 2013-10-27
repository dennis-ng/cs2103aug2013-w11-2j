/**
 * 
 */
package typetodo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author DennZ
 * 
 */
public class FeedbackDialog extends JDialog {

	private JPanel feedbackPanel;
	private JPanel taskListPanel;
	private JLabel lblFeedback;
	private JTextArea txtListOutput;

	public FeedbackDialog() {
		super();
		initialize();
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public FeedbackDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setMinimumSize(new Dimension(owner.getWidth() - 40, 0));
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public FeedbackDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setMinimumSize(new Dimension(owner.getWidth() - 40, 50));
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public FeedbackDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setMinimumSize(new Dimension(owner.getWidth() - 40, 50));
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 */
	public FeedbackDialog(Frame owner, String title) {
		super(owner, title);
		setMinimumSize(new Dimension(owner.getWidth() - 40, 50));
		initialize();
	}

	/**
	 * @param owner
	 */
	public FeedbackDialog(Frame owner) {
		super(owner);
		setMinimumSize(new Dimension(owner.getWidth() - 40, 50));
		initialize();
	}

	public JLabel getFeedbackLabel() {
		return lblFeedback;
	}

	public JTextArea getOutputBox() {
		return txtListOutput;
	}

	/**
	 * 
	 */
	public void initialize() {
		this.setUndecorated(true);
		feedbackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setPaint(Color.DARK_GRAY);
				g2d.fill(new RoundRectangle2D.Double(0, -10, getWidth(),
						getHeight() + 10, 20, 20));
			}
		};
		lblFeedback = new JLabel("Fail to initialise feedback");
		lblFeedback.setForeground(Color.WHITE);
		feedbackPanel.add(lblFeedback);

		taskListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		txtListOutput = new JTextArea("Add some words");
		txtListOutput.addComponentListener(new ComponentAdapter() {
			// Resize the dialog as the textarea increase in size
			@Override
			public void componentResized(ComponentEvent e) {
				FeedbackDialog.this.pack();
			}
		});
		txtListOutput.setLineWrap(true);
		txtListOutput.setWrapStyleWord(true);
		txtListOutput.setBackground(new Color(230, 230, 230));
		txtListOutput.setEditable(false);
		txtListOutput.setSize(this.getMinimumSize());
		txtListOutput.setMinimumSize(new Dimension(txtListOutput.getWidth(), 0));
		txtListOutput.setMaximumSize(new Dimension(txtListOutput.getWidth(), 20));
		taskListPanel.add(txtListOutput);

		this.setOpacity(0.75f);
		this.setLayout(new BorderLayout());
		this.add(feedbackPanel, BorderLayout.NORTH);
		this.add(taskListPanel, BorderLayout.CENTER);
		this.pack();
	}
}
