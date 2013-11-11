// @author A0097968Y
package typetodo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CommandPanel extends JPanel {
	private JLabel lblTitle;
	private JLabel lblMinimize;
	private JLabel lblClose;
	private JTextField txtCmd;

	/**
	 * 
	 */
	public CommandPanel() {
		super();
		initialize();
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
	}

	/**
	 * @param isDoubleBuffered
	 */
	public CommandPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initialize();
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public CommandPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initialize();
		this.setLayout(layout);
	}

	/**
	 * @param layout
	 */
	public CommandPanel(LayoutManager layout) {
		super(layout);
		initialize();
		this.setLayout(layout);
	}

	private void initialize() {
		lblTitle = new JLabel(" TypeToDo ");
		lblMinimize = new JLabel("-");
		lblMinimize.setToolTipText("Minimize");
		lblClose = new JLabel("X");
		lblClose.setToolTipText("Close");
		txtCmd = new JTextField(30);
		txtCmd.setToolTipText("Enter a command");

		this.add(lblTitle);
		this.add(txtCmd);
		this.add(lblMinimize);
		this.add(lblClose);

		this.setBackground(new Color(0, 0, 0, 0));
		this.setMinimumSize(getSize());
		this.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(new Color(230, 230, 230));
		g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
	}

	@Override
	public Component add(Component comp) {
		comp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,
				comp.getFont().getSize() + 5));
		return super.add(comp);
	}

	protected void setFrameToMinimize(final Frame parent) {
		lblMinimize.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				parent.setState(JFrame.ICONIFIED);
			}
		});
	}

	protected void setFrameToClose(final Frame parent) {
		lblClose.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				parent
						.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	public JTextField getTxtCmd() {
		return txtCmd;
	}

}
