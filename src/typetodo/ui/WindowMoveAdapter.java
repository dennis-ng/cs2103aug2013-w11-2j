package typetodo.ui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

public class WindowMoveAdapter extends MouseAdapter {
	private final List<Component> WindowMoveComponents;
	private static WindowMoveAdapter instance;
	private boolean dragging = false;
	private int prevX = -1;
	private int prevY = -1;

	private WindowMoveAdapter() {
		super();
		WindowMoveComponents = new ArrayList<Component>();
	}

	public static WindowMoveAdapter getInstance() {
		if (instance == null) {
			instance = new WindowMoveAdapter();
		}
		return instance;
	}

	public void addComponentToMove(Component component) {
		WindowMoveComponents.add(component);
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dragging = true;
		}
		prevX = e.getXOnScreen();
		prevY = e.getYOnScreen();
	}

	public void mouseDragged(MouseEvent e) {
		if (prevX != -1 && prevY != -1 && dragging) {
			int xMoved = e.getXOnScreen() - prevX;
			int yMoved = e.getYOnScreen() - prevY;
			for (Component window : WindowMoveComponents) {
				if (window != null && window.isShowing()) {
					Rectangle rect = window.getBounds();
					window.setBounds(rect.x + (xMoved), rect.y + (yMoved), rect.width,
							rect.height);
				}
			}
		}
		prevX = e.getXOnScreen();
		prevY = e.getYOnScreen();
	}

	public void mouseReleased(MouseEvent e) {
		dragging = false;
	}
}