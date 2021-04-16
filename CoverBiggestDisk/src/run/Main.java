package run;

import java.util.ArrayList;

import algorithm.CC_Functions;
import algorithm.Point;
import javafx.application.Application;
import javafx.scene.shape.Circle;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//CsvGenerator csvg =  new CsvGenerator(10, 0, 100);
				//csvg.generateCSV("C:/Users/Lunaem/Desktop/data3.csv");
				
		        Application.launch(Window.class,args);
		
		Circle c1 = new Circle (0,0,50);
		Circle c2 = new Circle (100,0,50);
		
		
		ArrayList<Point> points =  CC_Functions.findIntersectionPoints(c1, c2);
		//System.out.println(points.get(0).getX() + " " + points.get(0).getY());
		//System.out.println(points.get(1).getX() + " " + points.get(1).getY());
		        
	}

}
