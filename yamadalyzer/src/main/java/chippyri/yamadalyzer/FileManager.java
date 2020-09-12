package chippyri.yamadalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// A utility class to read and save files
public class FileManager {

    public static boolean fileExists(String filename) {
    	File tmpFile = new File(filename);
    	return tmpFile.exists(); 
    }
	
    public static boolean saveFile(String filename, String[] fileContents, boolean allowOverwrite) throws IOException {

    	if (fileExists(filename) && !allowOverwrite) {
    		return false;    		
    	} 
    	
		FileWriter fw = new FileWriter(filename);
		
		for (String str : fileContents) {
			fw.write(str);
		}
		
		fw.close();
		return true;
    		
    }

    public static List<String> loadFile(String filename) throws FileNotFoundException, IOException {
		List<String> contents = new ArrayList<String>();
		
    	if (fileExists(filename)) {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			
			while((line = reader.readLine()) != null) {
				contents.add(line);
			}
			
			reader.close();
    	}
    	
    	return contents;
    }
}
