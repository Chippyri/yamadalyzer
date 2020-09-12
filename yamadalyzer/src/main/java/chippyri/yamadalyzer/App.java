package chippyri.yamadalyzer;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;


// TODO: Refactor
// TODO: Export "map" to file
// TODO: Notify when a solution could not be found

public class App extends Application {
	
	@FXML
	private Button solveButton;
	
	@FXML
	private Button clearSolutionButton;
	
	@FXML
	private Button clearMapButton;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private TextField filenameTextField;
	
	final static String DEFAULT_LEVEL_FILENAME = "level/Level01.txt";
	final static int SCREEN_SIZE = 500;
	
	private static MapData mapData;
	
	@FXML
	void clearPath() {
		mapData.clearPath();
	}
	
	@FXML
	void clearMap() {
		mapData.clearMap();
	}

	@FXML
	void solvePath() {
		mapData.solvePath();
	}
	
	
	// TODO: Unimplemented, use MapManager
	@FXML 
	void saveMap(){
		String filename = filenameTextField.getText();
		System.out.println(filename);
		
	}
	
	// TODO: Unimplemented, use MapManager
	@FXML 
	void loadMap(){
		String filename = filenameTextField.getText();
		//readFile(filename);	// TODO: MapManager
		throw new UnsupportedOperationException();
	}

	@Override
	public void init() {
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
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("interface.fxml"));
    		loader.load();
    		BorderPane root = (BorderPane)loader.getRoot();
    		root.setCenter(mapData.getMapPane());
    		Scene scene = new Scene(root, SCREEN_SIZE, SCREEN_SIZE);
    		stage.setTitle("Yamadalyzer");
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
	

}