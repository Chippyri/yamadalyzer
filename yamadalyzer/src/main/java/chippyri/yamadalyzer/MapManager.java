package chippyri.yamadalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

// A manager that turns file data into map data and vice versa
public class MapManager {

	final static int GRID_SIZE = 5;
	
	final static int CELL_DATA_START = 0;
	final static int VERTICAL_WALL_DATA_START = 5;
	final static int HORIZONTAL_WALL_DATA_START = 10;
	
	public static MapData readMapData(String filename) throws FileNotFoundException, IOException {
		List<String> loadedData = FileManager.loadFile(filename);
		String line;
		MapData mapData = new MapData();
		
		// 5*5 cells
		for (int y = CELL_DATA_START; y < VERTICAL_WALL_DATA_START; y++) {
			line = loadedData.get(y);
			for(int x = 0; x < GRID_SIZE; x++) {
				int cellType = Character.getNumericValue(line.charAt(x));
				mapData.setCell(x, y, cellType);
			}
		}
		
		// 5*4 vertical walls
		for (int y = VERTICAL_WALL_DATA_START; y < HORIZONTAL_WALL_DATA_START; y++) {
			line = loadedData.get(y);
			for(int x = 0; x < GRID_SIZE - 1; x++) {
				int wallType = Character.getNumericValue(line.charAt(x));
				mapData.setVerticalWall(x, y - VERTICAL_WALL_DATA_START - 1, wallType);
			}
		}
		
		// 4*5 horizontal walls
		for (int y = HORIZONTAL_WALL_DATA_START; y < HORIZONTAL_WALL_DATA_START + 4; y++) {
			line = loadedData.get(y);
			for(int x = 0; x < GRID_SIZE; x++) {
				int wallType = Character.getNumericValue(line.charAt(x));
				mapData.setHorizontalWall(x - 1, y - HORIZONTAL_WALL_DATA_START, wallType);
			}
		}
		
		return mapData;
	}
	
	public static void saveMapData() throws Exception {
		throw new Exception("Not implemented");
	}
}
