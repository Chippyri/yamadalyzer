package chippyri.yamadalyzer;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;


// TODO: Refactor
// TODO: Export "map" to file
// TODO: Notify when a solution could not be found

public class App extends Application {
	
	final static String DEFAULT_LEVEL_FILENAME = "level/Level01.txt";
	final static int SCREEN_SIZE = 500;
	
	private static MapData mapData;
	
	// --------------------------
	// Buttons
	// --------------------------
	
	@FXML
	private Button solveButton;
	
	@FXML
	private Button clearSolutionButton;
	
	@FXML
	private Button clearMapButton;
	
	@FXML
	private Button saveButton;
	
	// --------------------------
	// Button methods
	// --------------------------
	
	@FXML
	void solvePath() {
		mapData.solvePath();
	}
	
	@FXML
	void clearPath() {
		mapData.clearPath();
	}
	
	@FXML
	void clearMap() {
		mapData.clearMap();
	}
	
	// TODO: Unimplemented, use MapManager
	@FXML 
	void saveMap(){
		String filename = filenameTextField.getText();
		System.out.println(filename);
		throw new UnsupportedOperationException();
	}
	
	// TODO: Unimplemented, use MapManager
	@FXML 
	void loadMap() throws FileNotFoundException, IOException{
		String filename = filenameTextField.getText();
		MapManager.readMapData(filename);
		System.out.println(filename);

	}
	
	// --------------------------
	// Fields
	// --------------------------
	
	@FXML
	private TextField filenameTextField;
	

	// --------------------------
	// App initialization
	// --------------------------
	
	@Override
	public void init() {
		// Load a default file for testing purposes
		if (DEFAULT_LEVEL_FILENAME != null) {	// TODO: Remove DEFAULT_LEVEL_FILENAME
			try {
				mapData = MapManager.readMapData(DEFAULT_LEVEL_FILENAME);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			mapData = new MapData();	// Empty map
		}
	}
	
    @Override
    public void start(Stage stage) throws IOException {

		// Load the FXML to display and populate it
		FXMLLoader loader = new FXMLLoader(getClass().getResource("interface.fxml"));
		loader.load();
		
		// Set the map pane to the center of the pane
		BorderPane root = (BorderPane)loader.getRoot();
		root.setCenter(mapData.getMapPane());
		
		// Create the scene and display it
		Scene scene = new Scene(root, SCREEN_SIZE, SCREEN_SIZE);
		stage.setTitle("Yamadalyzer");
		stage.setScene(scene);
		stage.show();
				
    }
	

}