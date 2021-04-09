package run;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.shape.Circle;

public class Data {
	
	private ArrayList<Integer> listOfCircles_Data = new ArrayList<Integer>();
	private String fileLocation = null;
	
	
	/**
	 * Constructor without file path
	 */
	public Data(){
		this.fileLocation = null;
		
	}
	
	/**
	 * Constructor with file path
	 * @param fileLocation file path
	 */
	public Data(String fileLocation){
		this.fileLocation = fileLocation;
		
	}
	
	/**
	 * reads file and returns data as a list of integer values
	 * @return list of integer values
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Integer> readOutFile() throws FileNotFoundException{  
		ArrayList<Integer> circles_Data = new ArrayList<Integer>();
		Scanner sc = new Scanner(new File(this.fileLocation)); 
		
		sc.useDelimiter(",");  
		
		while (sc.hasNext()){  
			int i = sc.nextInt(); 
			listOfCircles_Data.add(i);
		}  
		sc.close();  
		return circles_Data;
	}  
	
	/**
	 * setter for the file location
	 * @param fileLocation filepath
	 */
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	
	/**
	 * getter for list data
	 * @return list of data
	 */
	public ArrayList<Integer> getListOfCircles_Data() {
		try {
			readOutFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return listOfCircles_Data;
	}
}
