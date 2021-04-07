package run;
import java.util.ArrayList;
import java.util.Collections;
import drawing.CircleSpaceCalculation;
import geometricShapes.Disk;
import javafx.scene.shape.Circle;
import java.io.*;  
	import java.util.Scanner;  


//Test-class for providing data
public class Data {
	
	
	private ArrayList<Circle> listOfCircles_Dat = new ArrayList<Circle>();
	private String fileLocation = null;
	
	public Data(){
		this.fileLocation = null;
		
	}
	
	public Data(String fileLocation){
		this.fileLocation = fileLocation;
		
	}
	
	public ArrayList<Circle> getListOfCircles_Dat() {
		try {
			readOutFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return listOfCircles_Dat;
	}

	
	
	public ArrayList<Circle> readOutFile() throws FileNotFoundException{  
		ArrayList<Circle> circles_Data = new ArrayList<Circle>();
		Scanner sc = new Scanner(new File(this.fileLocation)); 
		
		sc.useDelimiter(",");  
		
		while (sc.hasNext()){  
			Circle c = new Circle(sc.nextInt()); 
			listOfCircles_Dat.add(c);
		}  
		sc.close();  
		return circles_Data;
	}  
	
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	
}



