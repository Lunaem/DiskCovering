package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import run.DrawingTools;

import javafx.scene.shape.Circle;
import run.Data;

public class CircleCoverage {
		
	private int global_iteration_count; 
	private Data data;

	//user parameters
	private int stop_iteration; //var used to stop the algorithm after x iterations
	private boolean ccw; 
	private boolean poh;
	private double tolerance; //tolerance for placing circles
	
	//list for debug info
	private ArrayList<Circle> listOfIntersectionPoints;
		
	//List to store radii of circles we want to place
	private ArrayList<Integer> listOfCircles_Data;
	
	
	//List to store all circles placed
	private ArrayList<Circle> listOfPlacedCircles;
	private ArrayList<Circle> listOfAlignementCircles;
	private int circlesLeftToPlace;	//number of circles that are left after circle has been covered
	
	/**
	 * Constructor
	 * @param data data that is being used
	 */
	public CircleCoverage(Data data) {
		this.data = data;
		ccw = false;
		poh = false;
		tolerance = 0.30; //TODO blend in window
		init();
	}
	
	//loads data of circles
	private void loadData() {

		for(int radius: data.getListOfCircles_Data()) {
			listOfCircles_Data.add(radius);
		}
		if(listOfCircles_Data.isEmpty()) {
			System.out.print("List with data is empty!");
		}

		//sort list of circles
		Collections.sort(listOfCircles_Data);
		Collections.reverse(listOfCircles_Data);
	}
	
	public void init() {
		global_iteration_count = 0;
		listOfIntersectionPoints = new ArrayList<Circle>();
		listOfCircles_Data = new ArrayList<Integer>();
		listOfPlacedCircles = new ArrayList<Circle>();
		listOfAlignementCircles = new ArrayList<Circle>();
		loadData();
	}

	/**
	 * Find biggest circle that is completely covered by given circles
	 */
	public void calculateBiggestCircle() {
		boolean isCovered = false;
		global_iteration_count = 1;
		CC_Iteration cci = null;

		double minSpace = CC_Functions.getSpace(new Circle(listOfCircles_Data.get(0)));
		double maxSpace = 0;

		for(int radius:listOfCircles_Data){
			maxSpace += (Math.pow(radius,2) * Math.PI);
		} 

		//try to cover circles of decreasing size
		while(!(isCovered && ((maxSpace - minSpace) <=  1e-10))) {
			
			isCovered = false;
			double currentSpace = (minSpace + maxSpace) / 2;
			
			System.out.println("____________________");
			System.out.println(" iteration: " + global_iteration_count + " ");
			System.out.println("space: " + currentSpace + " ");
			System.out.println("min: " + minSpace + " ");
			System.out.println("max:  " + maxSpace+ " ");
			
			Circle outerAlignementCircle = createOuterAlignementCircle(currentSpace);
			cci = new CC_Iteration(outerAlignementCircle, listOfCircles_Data, tolerance, ccw, poh);
			isCovered = cci.fillOuterAlignementCircle();
			
			System.out.println(" circles left: " + cci.getListOfCirclesToPlace().size() + " ");
			System.out.println(isCovered);
			System.out.println("____________________");

			if(isCovered) {
				minSpace = currentSpace;
			}
			if(!isCovered) {
				maxSpace = currentSpace;
			}
			
			if(global_iteration_count == stop_iteration) {
				break;
			}
			global_iteration_count ++;

		}
		circlesLeftToPlace = cci.getListOfCirclesToPlace().size();
		listOfPlacedCircles = cci.getListOfPlacedCircles();
		listOfAlignementCircles = cci.getListOfAlignmentCircles();
		listOfIntersectionPoints = cci.getListOfIntersectionPoints();
		
		DrawingTools.colorElements(true, listOfPlacedCircles, listOfAlignementCircles, listOfIntersectionPoints);
	}
	
	/**
	 * Creates an initial outer alignemt Circle at the beginning of the iteration
	 * @param spaceOfCircle space of the circle we create
	 * @return circle with given space
	 */
	public Circle createOuterAlignementCircle(double spaceOfCircle){
		
		Circle outerCircle = new Circle(0,0, Math.sqrt(spaceOfCircle/Math.PI));
		
		return outerCircle;
	}
	
	//---------Getter & Setter-----------
	
	public double getTolerance() {
		return tolerance;
	}	
	
	public void setCCW(boolean value) {
		this.ccw = value;
	}
	
	public void setPOH(boolean value) {
		this.poh = value;
	}
	
	public void setStopIteration(int value) {
		this.stop_iteration = value;
	}

	public void setTolerance(double value) {
		this.tolerance = value;
	}
	
	public ArrayList<Circle> getListOfIntersectionPoints(){
		return this.listOfIntersectionPoints;
	}

	public int getCirclesLeftToPlace() {
		return circlesLeftToPlace;
	}

	public ArrayList<Circle> getListOfPlacedCircles() {
		return listOfPlacedCircles;
	}

	public ArrayList<Circle> getListOfAlginementCircles() {
		return listOfAlignementCircles;
	}

}
