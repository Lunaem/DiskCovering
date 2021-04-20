package run;

import java.util.ArrayList;

import algorithm.Point;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class DrawingTools {

	//TODO DELETE TEST ONLY
	public static ArrayList<Circle> listOfPoints = new ArrayList<Circle>();
	
	
	/**
	 * Gives back all the elements that will be drawn
	 * @param debug indicates if elements that are used for developing are to be shown
	 * @return Group group which holds all elements that are to be drawned
	 */
	public static void colorElements(Boolean debug, ArrayList<Circle> listOfPlacedCircles, ArrayList<Circle> listOfAlignementCircles, ArrayList<Circle> listOfPoints) {
		for(Circle c: listOfPlacedCircles) {
			c.setFill(Color.TRANSPARENT);
			if(c.getStroke() == null) {
				c.setStroke(Color.BLACK);
			}
		}
		//AlginmentCircles -> RED
			for(Circle c: listOfAlignementCircles) {
			c.setFill(Color.TRANSPARENT);
			c.setStroke(Color.RED);
			}
		
		//add debug elements
		if(debug) {
			double radius = listOfAlignementCircles.get(0).getRadius()*0.01;
			//TODO DELETE TEST ONLY-> GREEN
			for(Circle i : listOfPoints){
				i.setRadius(radius);
				i.setStroke(Color.GREEN);
				i.setFill(Color.GREEN);
			} 
		}
		
	}
	
	/**
	 * Adds a coordinate system to a group
	 * @param root group which the cordinates are to be added
	 */
	private static void drawCoordinateSystem(Group root) {
		//draw axis
		Line x_axis = new Line(500,0, -500, 0); 
		Line y_axis = new Line(0,500, 0, -500);
		
		root.getChildren().add(x_axis);
		root.getChildren().add(y_axis);
		
		//draw labels
		for(int x = -500; x <=500; x += 50 ) {
			Text t = new Text(x+"");
			t.relocate(x, 0);
			root.getChildren().add(t);
		}
		
		for(int y = -500; y <=500; y += 50 ) {
			Text t = new Text(y+"");
			t.relocate(0, y);
			root.getChildren().add(t);
		}
		
	}
	
	/**
	 * Draws a circle at a given point
	 * @param p point at which the circle is to be drawn
	 */
	public void drawCircle(Point p) {
		Circle c = new Circle(p.getX(), p.getY(), 10);
		listOfPoints.add(c);
	}
}
