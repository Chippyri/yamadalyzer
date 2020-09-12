package chippyri.yamadalyzer;

import java.util.LinkedList;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import chippyri.yamadalyzer.Cell.CELL_STATE;

// Contains the logic and visual information required to create a map
public class MapData {

	public static enum ORIENTATION {VERTICAL, HORIZONTAL};
	public static enum DIRECTION {NORTH, EAST, SOUTH, WEST};
	
	final static int GRID_SIZE = 5;
	
	// TODO: Move appearance to CSS, attributes to XML/properties?
	final static int SHAPE_SIZE = 60;
	final static int STROKE_WIDTH = 10;
	final static int GRID_OFFSET = 30;
	
	private static Cell startCell = null;
	private static Cell endCell = null;
	private final static Wall[][] horizontalWalls = new Wall[GRID_SIZE][GRID_SIZE+1];	// 1 row extra
	private final static Wall[][] verticalWalls = new Wall[GRID_SIZE+1][GRID_SIZE];	// 1 column extra
	private final static Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];

	private static Pane mapPane;
	private static LinkedList<Cell> tmpLongestPath = null;
	
	private final static Line[] solutionPathLines = new Line[GRID_SIZE * GRID_SIZE];
	
	// ************************************
	// MAP SOLVING METHODS
	// ************************************
	
	public void solvePath() {
		if (startCell != null && endCell != null) {
			Platform.runLater(()->{
				clearPath();
				LinkedList<Cell> path = walkFromStartToEnd();
				if (path == null) {
					System.out.println("No full path");
					if (tmpLongestPath != null) {
						System.out.println("Tmp was not null");
						// TODO: Copied code
						// TODO: What if no path is found?
						for (int i = 0; i < tmpLongestPath.size(); i++) {
							if (i + 1 >= tmpLongestPath.size() ) { // Should not go past end
								break;
							}
							
							Cell cell = tmpLongestPath.get(i);
							Rectangle rect = (Rectangle) cell.getShape();
							Cell nextCell = tmpLongestPath.get(i+1);
							Rectangle nextRect = (Rectangle) nextCell.getShape();
			
							// Draw a line between the center of the rectangles
							Line line = generateLineBetweenRectangles(rect, nextRect);
							mapPane.getChildren().add(line);
							solutionPathLines[i] = line;
						}
					}
				} else {
					for (int i = 0; i < path.size(); i++) {
						if (i + 1 >= path.size() ) { // Should not go past end
							break;
						}
						
						Cell cell = path.get(i);
						Rectangle rect = (Rectangle) cell.getShape();
						Cell nextCell = path.get(i+1);
						Rectangle nextRect = (Rectangle) nextCell.getShape();
		
						// Draw a line between the center of the rectangles
						Line line = generateLineBetweenRectangles(rect, nextRect);
						mapPane.getChildren().add(line);
						solutionPathLines[i] = line;
					}
				}
				tmpLongestPath.clear();
				tmpLongestPath = null;
			});
		}
		// TODO: Error message to set start and end?
	}

	private LinkedList<Cell> walkFromStartToEnd() {
    	
    	LinkedList<Cell> longestPath = new LinkedList<Cell>();
    	startCell.traverse();
    	longestPath.add(startCell);
    	
    	Cell[] accessibleCells = getAccessibleCellsFromCell(startCell);
    	for (DIRECTION direction : DIRECTION.values()) {
    		Cell c = accessibleCells[direction.ordinal()];
    		if (c != null) {
    			longestPath.add(c);
    			c.traverse();
    			
    			LinkedList<Cell> result = traversePath(longestPath);
    			
    			if (result == null) {
    				longestPath.remove(c);
        			c.undoTraversal();
    			} else {
    				return result;
    			}
    		}
    	}
    	
    	return null;
    }
    
    private LinkedList<Cell> traversePath(LinkedList<Cell> longestPath){
    	if (longestPath.size() == GRID_SIZE * GRID_SIZE && longestPath.get(GRID_SIZE*GRID_SIZE-1).getState() == Cell.CELL_STATE.END) { 
    		return longestPath; 
    	} 
    	
    	if(longestPath.getLast().getState() == CELL_STATE.END) {
    		if (tmpLongestPath == null) {
    			tmpLongestPath = (LinkedList<Cell>) longestPath.clone();
    		} else {
    			if (tmpLongestPath.size() < longestPath.size()) {
    				tmpLongestPath = (LinkedList<Cell>) longestPath.clone();
    			}
    		}
    	}
    	
    	Cell cell = longestPath.get(longestPath.size()-1);
    	Cell[] accessibleCells = getAccessibleCellsFromCell(cell);
    	for (DIRECTION direction : DIRECTION.values()) {
    		Cell c = accessibleCells[direction.ordinal()];
    		if (c != null) {
    			longestPath.add(c);
    			c.traverse();
    			
    			LinkedList<Cell> result = traversePath(longestPath);
    			
    			if (result == null) {
    				longestPath.remove(c);
        			c.undoTraversal();
    			} else {
    				return result;
    			}
    		}
    	}
    	
    	return null;
    }

    private Cell getCellNeighbourByDirection(DIRECTION direction, Cell cell) {
    	
    	int x = cell.getX();
    	int y = cell.getY();
    	
    	switch(direction) {
	    	case NORTH:
	    		if (y <= 0) return null;
	    		return cells[x][y - 1];
	    	case EAST:
	    		if (x >= GRID_SIZE) return null;
	    		return cells[x + 1][y];
	    	case SOUTH:
	    		if (y >= GRID_SIZE) return null;
	    		return cells[x][y + 1];
	    	case WEST:
	    		if (x <= 0) return null;
	    		return cells[x - 1][y];
    		default:
    			return null;
    	}
    	
    }
    
    private boolean hasWallInDirection(DIRECTION direction, int cellX, int cellY) {
    	return getWallInDirection(direction, cellX, cellY).isBlocking();
    }

    private Wall getWallInDirection(DIRECTION direction, int cellX, int cellY) {
    	switch(direction) {
    		case NORTH:
    			return horizontalWalls[cellX][cellY];
    		case EAST:
    			return verticalWalls[cellX+1][cellY];
    		case SOUTH:
    			return horizontalWalls[cellX][cellY+1];
    		case WEST:
    			return verticalWalls[cellX][cellY];
    		default:
    			return null;
    	}
    }
    
    // Returns walls in an array in the order NORTH, EAST, SOUTH, WEST
    private Wall[] getWallsNeighboringCell(int cellX, int cellY) {
		Wall[] neighbours = new Wall[4];
		
		neighbours[0] = getWallInDirection(DIRECTION.NORTH, cellX, cellY);
		neighbours[1] = getWallInDirection(DIRECTION.EAST, cellX, cellY);
		neighbours[2] = getWallInDirection(DIRECTION.SOUTH, cellX, cellY);
		neighbours[3] = getWallInDirection(DIRECTION.WEST, cellX, cellY);
				
		return neighbours;
	}
    
    // Solution "frontier", null means no options and that you should roll back
    private Cell[] getAccessibleCellsFromCell(Cell cell) {
    	Cell[] accessibleCells = new Cell[4];
    	
    	for (DIRECTION direction : DIRECTION.values()) {
    		if (!hasWallInDirection(direction, cell.getX(), cell.getY())) {
    			Cell neighbour = getCellNeighbourByDirection(direction, cell);
    			if (neighbour != null && !neighbour.hasBeenTraversed()) {
    				accessibleCells[direction.ordinal()] = neighbour;
    			}
    		}
    	}
    	return accessibleCells;
    }
    
    // NOTE: Can return null
    private DIRECTION directionBetweenCells(Cell a, Cell b) {
    	int xDifference = a.getX() - b.getX();
    	int yDifference = a.getY() - b.getY();
    	
    	// TODO: Diagonal moves should not be possible
    	// TODO: Zeroes in both should not be possible
    	
    	switch(xDifference) {
    		case -1:
    			return DIRECTION.EAST;
    		case 1:
    			return DIRECTION.WEST;
    		default:
    			break;
    	}
    	
    	switch(yDifference) {
    		case 1:
    			return DIRECTION.NORTH;
    		case -1:
    			return DIRECTION.SOUTH;
    		default:
    			break;
    	}
    	
    	return null;
    }
	
	// ************************************
	// EVENT HANDLERS
	// ************************************
    
	// Toggles walls when clicked
	// TODO: Can these be handled with states in Wall?
	private final static EventHandler<MouseEvent> wallClickHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			// TODO: Has to be a better way... maybe make Wall able to react to MouseEvents?
			Line line = (Line)mouseEvent.getTarget();
			int x = (int)line.getStartX() / SHAPE_SIZE;
			int y = (int)line.getStartY() / SHAPE_SIZE;
			// Check the orientation of the line, to get the wall from the correct array
			Wall wall;
			if (line.getEndX() > line.getStartX()) {
				wall = horizontalWalls[x][y];
				System.out.println("Horizontal, " + x + "," + y);
			} else {
				wall = verticalWalls[x][y];
				System.out.println("Vertical, " + x + "," + y);
			}
			wall.toggle();
		}
	};
	
	// Toggles cell state when left or right clicked
	private final static EventHandler<MouseEvent> cellClickHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			
			Rectangle rect = (Rectangle)mouseEvent.getTarget();
			// TODO: Has to be a better way... maybe make Cell able to react to MouseEvents?
			int x = (int) rect.getX() / SHAPE_SIZE;
			int y = (int) rect.getY() / SHAPE_SIZE;
			Cell cell = cells[x][y];
			
			// TODO: Refactor this, too many paths
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {	// SET START
				if (cell.getState() == CELL_STATE.WALKABLE) {
					updateStartCell(cell);
				} else if (cell.getState() == CELL_STATE.END) {
					endCell = null;
					updateStartCell(cell);
				} else { 
					// It was already the start
					startCell = null;
					cell.setState(CELL_STATE.WALKABLE);
				}
			}
			
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {	// SET END
				if (cell.getState() == CELL_STATE.WALKABLE) {
					updateEndCell(cell);
				}
				else if (cell.getState() == Cell.CELL_STATE.START) {
					startCell = null;
					updateEndCell(cell);
				}
				else {	// It was already the end
					endCell = null;
					cell.setState(Cell.CELL_STATE.WALKABLE);
				}
			}
		}
	};
	
	public MapData() {
		createCells();
		createWalls();
		fillNewPaneWithElements();
	}
	
	// ************************************
	// MAP INITIALIZATION FUNCTIONS
	// ************************************
	
	private void createCells() {
		for (int x = 0; x < GRID_SIZE; x++) {
			for (int y = 0; y < GRID_SIZE; y++) {
				Rectangle rectangle = rectangleFactory(x, y);
				rectangle.setOnMouseClicked(cellClickHandler);
				cells[x][y] = new Cell(x, y, rectangle);
			}
		}
	}
	
	private void createWalls() {
		createHorizontalBorder();
		createVerticalBorder();
		createInnerHorizontalWalls();
		createInnerVerticalWalls();
	}
	
	private void createVerticalBorder() {
		for (int y = 0; y < GRID_SIZE; y++) {
			verticalWalls[0][y] = wallFactory(ORIENTATION.VERTICAL, true, false, 0, y); 
			verticalWalls[GRID_SIZE][y] = wallFactory(ORIENTATION.VERTICAL, true, false, GRID_SIZE, y);
		}
	}

	private void createHorizontalBorder() {
		for (int x = 0; x < GRID_SIZE; x++) {
			horizontalWalls[x][0] = wallFactory(ORIENTATION.HORIZONTAL, true, false, x, 0);
			horizontalWalls[x][GRID_SIZE] = wallFactory(ORIENTATION.HORIZONTAL, true, false, x, GRID_SIZE);
		}
	}

	private void createInnerVerticalWalls() {
		for (int y = 0; y < GRID_SIZE; y++) {
			for (int x = 1; x < GRID_SIZE; x++) {
				Line line = lineFactory(ORIENTATION.VERTICAL, x, y, true);
				verticalWalls[x][y] = new Wall(line, false);
			}
		}
	}

	private void createInnerHorizontalWalls() {
		for (int y = 1; y < GRID_SIZE; y++) {
			for (int x = 0; x < GRID_SIZE; x++) {
				Line line = lineFactory(ORIENTATION.HORIZONTAL, x, y, true);
				horizontalWalls[x][y] = new Wall(line, false);
			}
		}
	}
	
	private Line lineFactory(ORIENTATION orientation, int x, int y, boolean setHandler) {

		Line line = null;
		
		switch(orientation) {
		
			case VERTICAL:
				line = new Line(
						SHAPE_SIZE * x + GRID_OFFSET,
						SHAPE_SIZE * y + GRID_OFFSET,
						SHAPE_SIZE * x + GRID_OFFSET,
						SHAPE_SIZE * (y + 1) + GRID_OFFSET
				);
				break;
				
			case HORIZONTAL:
				line = new Line(
						SHAPE_SIZE * x + GRID_OFFSET,
						SHAPE_SIZE * y + GRID_OFFSET,
						SHAPE_SIZE * (x + 1) + GRID_OFFSET,
						SHAPE_SIZE * y + GRID_OFFSET
				);
				break;
				
			default:
				throw new RuntimeException();	// TODO: Explain cause
				
		}
		
		// TODO: Move to CSS
		line.setStrokeWidth(STROKE_WIDTH);
		
		if (setHandler) {
			line.setOnMouseClicked(wallClickHandler);
		}
		
		return line;
	}
	
	private Wall wallFactory(ORIENTATION orientation, boolean isBlocking, boolean setHandler, int x, int y) {
		return new Wall(lineFactory(orientation, x, y, setHandler), isBlocking);
	}

	private Rectangle rectangleFactory(int x, int y) {
		return new Rectangle(
				SHAPE_SIZE * x + GRID_OFFSET, 
				SHAPE_SIZE * y + GRID_OFFSET, 
				SHAPE_SIZE, 
				SHAPE_SIZE
		);
	}

	private void fillPaneWithElements(MapElement[][] pElements) {
		for (int x = 0; x < pElements.length; x++) {
			for (int y = 0; y < pElements[x].length; y++) {
				mapPane.getChildren().add(pElements[x][y].getShape());
			}
		}
	}
	
	private void fillNewPaneWithElements() {
		mapPane = new Pane();
		fillPaneWithElements(cells);
		fillPaneWithElements(horizontalWalls);
		fillPaneWithElements(verticalWalls);
	}
	
	// ************************************
	// UPDATE CELLS & WALLS
	// ************************************
	
	void setCell(int x, int y, int type) {
		switch(type) {
			// Walkable cell
			case 0:
				cells[x][y].setState(Cell.CELL_STATE.WALKABLE);
				break;
			// Start cell
			case 1:
				updateStartCell(cells[x][y]);
				break;
			// End cell
			case 2:
				updateEndCell(cells[x][y]);
				break;
			// TODO: Unwalkable cell
			default:
				throw new RuntimeException("Not a valid type for cell: " + type);
		}
	}
	
	void setHorizontalWall(int x, int y, int state) {
		switch(state) {
			case 0:
				horizontalWalls[x + 1][y + 1].hide();
				break;
			case 1:
				horizontalWalls[x + 1][y + 1].show();
				break;
		}
	}
	
	void setVerticalWall(int x, int y, int state) {
		switch(state) {
			case 0:
				verticalWalls[x + 1][y + 1].hide();
				break;
			case 1:
				verticalWalls[x + 1][y + 1].show();
				break;
		}
	}
	
	static void updateStartCell(Cell cell) {
		if (startCell != null) {
			startCell.setState(Cell.CELL_STATE.WALKABLE);
			System.out.println("Unset START: " + startCell);
		}
		cell.setState(Cell.CELL_STATE.START);
		startCell = cell;
		System.out.println("Set START: " + startCell);
	}
	
	static void updateEndCell(Cell cell) {
		if (endCell != null) {
			endCell.setState(Cell.CELL_STATE.WALKABLE);
			System.out.println("Unset END: " + endCell);
		}
		cell.setState(Cell.CELL_STATE.END);
		endCell = cell;
		System.out.println("Set END: " + endCell);
	}
	
	public void clearMap() {
		
		// Reset cells 5x5
		for (int y = 0; y < GRID_SIZE; y++) {
			for (int x = 0; x < GRID_SIZE; x++) {
				setCell(x, y, 0);	// TODO: Change last parameter to enum for readability
			}
		}
		
		// Reset horizontal walls 4x5
		for (int x = 0; x < GRID_SIZE; x++) {
			for (int y = 0; y < GRID_SIZE - 1; y++) {
				setHorizontalWall(x - 1, y, 0); // TODO: Change last parameter to enum for readability
			}
		}
		
		// Reset vertical walls 5x4
		for (int x = 0; x < GRID_SIZE - 1; x++) {
			for (int y = 0; y < GRID_SIZE; y++) {
				setVerticalWall(x, y - 1, 0); // TODO: Change last parameter to enum for readability
			}
		}
		
	}

	// ************************************
	// GETTERS
	// ************************************
	
	public Pane getMapPane() {
		return mapPane;
	}
	
	// ************************************
	// SOLUTION LINE METHODS
	// ************************************
	
	private Line generateLineBetweenRectangles(Rectangle rect, Rectangle nextRect) {
		Line pathLine = new Line(
				rect.getX() + (rect.getWidth()/2), 
				rect.getY() + (rect.getHeight()/2), 
				nextRect.getX() + (nextRect.getWidth()/2), 
				nextRect.getY() + (nextRect.getHeight()/2));
		return pathLine;
	}

	public void clearPath() {
		mapPane.getChildren().removeAll(solutionPathLines);
		for (int i = 0; i < GRID_SIZE*GRID_SIZE; i++) {
			solutionPathLines[i] = null;
		}
		
		for (int x = 0; x < GRID_SIZE; x++) {
			for (int y = 0; y < GRID_SIZE; y++) {
				cells[x][y].undoTraversal();
			}
		}
	}
	
}
