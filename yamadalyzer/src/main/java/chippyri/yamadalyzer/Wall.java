package chippyri.yamadalyzer;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

// Both the visual (JavaFX Line) and logical implementation of a wall
public class Wall implements MapElement{

	public static enum WALL_ORIENTATION {VERTICAL, HORIZONTAL};
	
	final static double BLOCKING_WALL_OPACITY = 1.0;
	final static double NONBLOCKING_WALL_OPACITY = 0.01;
	
	private Line line;
	private boolean blocking;
	
	public Wall(Line pLine, boolean pBlocking) {
		line = pLine;
		if (pBlocking) {
			show();
		} else {
			hide();
		}
	}
	
	public void show() {
		blocking = true;
		line.setOpacity(BLOCKING_WALL_OPACITY);
	}
	
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
