package chippyri.yamadalyzer;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

// Both the visual (JavaFX Line) and logical implementation of a wall

// A wall can have two states:
// * blocking and visible in pane
// * nonblocking and invisible in pane

public class Wall implements MapElement {
	
	final static double BLOCKING_WALL_OPACITY = 1.0;
	final static double NONBLOCKING_WALL_OPACITY = 0.01;
	
	private Line line;
	private boolean blocking;
	
	// The wall needs to be given a line and whether or
	// not it is supposed to be a visible and blocking wall
	public Wall(Line pLine, boolean pBlocking) {
		line = pLine;
		if (pBlocking) {
			show();
		} else {
			hide();
		}
	}
	
	// Set wall to blocking and visible
	public void show() {
		blocking = true;
		line.setOpacity(BLOCKING_WALL_OPACITY);
	}
	
	// Set wall to nonblocking and "invisible"
	public void hide() {
		blocking = false;
		line.setOpacity(NONBLOCKING_WALL_OPACITY);
	}
	
	public void toggle() {
		if (blocking) {
			hide();
		} else {
			show();
		}
	}
	
	public boolean isBlocking() {
		return blocking;
	}

	@Override
	public Shape getShape() {
		return line;
	}
	
}
