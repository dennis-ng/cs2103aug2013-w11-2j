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
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

/**
 * @author DennZ
 * 
 */
public class FeedbackDialog extends JDialog {

	private JPanel feedbackPanel;
	private JPanel taskListPanel;
	private JTextArea txtFeedback;
	private JTextPane txtListOutput;
	private JScrollPane scrollableDisplay;
	private static ComponentAdapter DIALOG_RESIZE_ACTION;
	public final static Color dialogColor = new Color(230, 230, 230);

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

	public void setFeedbackText(String text) {
		this.txtFeedback.setText(text);
		this.pack();
	}

	public void setTableOfTasks(String htmlText) {
		this.txtListOutput.setText(htmlText);
		
		//lineCount = this.countLinesInJTextPane(this.txtListOutput);
		//if (lineCount > 10) {
			//txtListOutput.setRows(10);
		//} else {
			//txtListOutput.set.setRows(lineCount);
		//}

		try {
			String textWithoutHtmlTags = txtListOutput.getDocument().getText(0, txtListOutput.getDocument().getLength());
			int indexToScrollTo = getIndexToScrollTo(htmlText, textWithoutHtmlTags);
			txtListOutput.setCaretPosition(indexToScrollTo);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.pack();
	}	

	public int getIndexToScrollTo(String htmlText, String textWithOutHtmlTags) {
		int indexOfMarker = htmlText.indexOf("<marker>");
	
		if (indexOfMarker != -1) {
			String test = htmlText.substring(htmlText.indexOf("<marker>") + 8, htmlText.length());
			Scanner sc = new Scanner(test);
			sc.useDelimiter("]");
			String idOfTask = sc.next() + "]";
			sc.close();
			
			return textWithOutHtmlTags.indexOf(idOfTask);
		}
		
		return 0;
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
		DIALOG_RESIZE_ACTION = new ComponentAdapter() {
			// Resize the dialog as the textarea increase in size
			@Override
			public void componentResized(ComponentEvent e) {
				FeedbackDialog.this.pack();
			}
		};
		txtFeedback = new JTextArea();
		txtFeedback.setForeground(Color.WHITE);
		txtFeedback.setBackground(Color.DARK_GRAY);
		txtFeedback.setLineWrap(true);
		txtFeedback.setWrapStyleWord(true);
		txtFeedback.setSize(this.getMinimumSize());
		txtFeedback.setMinimumSize(new Dimension(txtFeedback.getWidth(), 0));
		txtFeedback.setMaximumSize(new Dimension(txtFeedback.getWidth(), 20));
		txtFeedback.setEditable(false);
		txtFeedback.addComponentListener(DIALOG_RESIZE_ACTION);
		feedbackPanel.add(txtFeedback);

		taskListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		txtListOutput = new JTextPane();
		txtListOutput.setContentType("text/html");
		txtListOutput.addComponentListener(DIALOG_RESIZE_ACTION);
		//txtListOutput.setLineWrap(true);
		//txtListOutput.setWrapStyleWord(true);
		txtListOutput.setBackground(dialogColor);
		txtListOutput.setEditable(false);
		txtListOutput.setSize(this.getMinimumSize());
		txtListOutput.setMinimumSize(new Dimension(txtListOutput.getWidth(), 0));
		txtListOutput.setMaximumSize(new Dimension(txtListOutput.getWidth(), 20));
		taskListPanel.setBackground(dialogColor);

		scrollableDisplay = new JScrollPane(txtListOutput);
		scrollableDisplay.setBorder(null);
		scrollableDisplay.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollableDisplay.setPreferredSize(new Dimension(txtListOutput.getWidth(), 200));
		taskListPanel.add(scrollableDisplay);
		// taskListPanel.add(txtListOutput);

		this.setBackground(dialogColor);
		this.setLayout(new BorderLayout());
		this.add(feedbackPanel, BorderLayout.NORTH);
		this.add(taskListPanel, BorderLayout.CENTER);
		this.pack();
	}

}
