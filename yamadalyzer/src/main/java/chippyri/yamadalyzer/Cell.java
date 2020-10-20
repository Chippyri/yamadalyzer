package chippyri.yamadalyzer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Cell implements MapElement {
	
	public enum CELL_STATE { WALKABLE, START, END }
	public CELL_STATE currentState = CELL_STATE.WALKABLE;
	
	// Coordinates in map
	private int x;
	private int y;
	
	// The walked-state of the cell during backtracking search
	private boolean traversed = false;
	
	// The visual representation of a cell
	private Rectangle rectangle;
	
	public Cell(int pX, int pY, Rectangle pRectangle){
		x = pX;
		y = pY;
		rectangle = pRectangle;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean hasBeenTraversed() {
		return traversed;
	}
	
	// TODO: Should not be possible if it has already been traversed
	public void traverse() {
		traversed = true;
	}
	
	// TODO: Should not be possible if traversed is not true
	public void undoTraversal() {
		traversed = false;
	}
	
	@Override
	public String toString() {
		return "Cell[ " + x + ", " + y + " ]";
	}

	@Override
	public Shape getShape() {
		return rectangle;
	}
	
	public CELL_STATE getState() {
		return currentState;
	}
	
	public void setState(CELL_STATE newState) {
		switch(newState) {
			case WALKABLE:
				rectangle.setFill(Color.WHITE);
				break;
			case START:
				rectangle.setFill(Color.LIGHTGREEN);
				break;
			case END:
				rectangle.setFill(Color.PINK);
				break;
		}
		currentState = newState;
	}
	
}
