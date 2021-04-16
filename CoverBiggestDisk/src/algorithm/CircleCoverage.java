package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import run.DrawingTools;

import javafx.scene.shape.Circle;
import run.Data;

public class CircleCoverage {
	//threshold used to allow minimal errors in calculations
		//final double threshold = 1e-6;
		//var used to stop the algorithm after 
		int stop_iteration = 1800; //TODO DELETE , add ad debug
		int global_iteration_count;
		boolean ccw = false;
		boolean poh = false;
		
		//list for tests
		//TODO DELETE , add to debug
		public ArrayList<Circle> listXYZ = new ArrayList<Circle>();
		public ArrayList<Circle> listOfIntersectionPoints = new ArrayList<Circle>();
		
		
		//tolerance for placing circles
		public double tolerance = 0.30; 
	
	//List to store all circles 
		public ArrayList<Integer> listOfCircles_Data = new ArrayList<Integer>();
		public boolean finished;

	//List to store all circles that need to be placed in current iteration 
	private int circlesLeftToPlace;
	//List to store all circles placed
	private ArrayList<Circle> listOfPlacedCircles = new ArrayList<Circle>();
	private ArrayList<Circle> listOfAlginementCircles = new ArrayList<Circle>();
	
	/**
	 * Constructor
	 * @param data data that is being used
	 */
	public CircleCoverage(Data data) {
		loadData(data);
	}
	
	//loads data of circles
	public void loadData(Data data) {

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

	/**
	 * Find biggest circle that is completely covered by given circles
	 */
	public void calculateBiggestCircle() {
		finished = false;
		int iteration = 0;
		CC_Iteration cci = null;

		double minSpace = CC_Functions.getSpace(new Circle(listOfCircles_Data.get(0)));
		double maxSpace = 0;

		for(int radius:listOfCircles_Data){
			maxSpace += (Math.pow(radius,2) * Math.PI);
		} 

		//try to cover circles of decreasing size
		while(!(finished && ((maxSpace - minSpace) <=  1e-10))) {
			finished = false;

			double currentSpace = (minSpace + maxSpace) / 2;
			System.out.println("____________________");
			System.out.println(" iteration: " + iteration + " ");
			Circle outerAlignementCircle = createOuterAlignementCircle(currentSpace);
			
			cci = new CC_Iteration(outerAlignementCircle, listOfCircles_Data, tolerance, ccw, poh);
			
			finished = cci.fillOuterAlignementCircle();

			System.out.println("space: " + CC_Functions.getSpace(outerAlignementCircle)+ " ");
			System.out.println("min: " + minSpace + " ");
			System.out.println("max:  " + maxSpace+ " ");

			System.out.println(" circles left: " + cci.getListOfCirclesToPlace().size() + " ");


			System.out.println(finished);

			System.out.println("____________________");

			iteration++;
			global_iteration_count = iteration;

			if(finished) {
				minSpace = currentSpace;
			}
			if(!finished) {
				maxSpace = currentSpace;
			}
			if(iteration == stop_iteration) {
				break;
			}


		}
		circlesLeftToPlace = cci.getListOfCirclesToPlace().size();
		listOfPlacedCircles = cci.getListOfPlacedCircles();
		listOfAlginementCircles = cci.getListOfAlignmentCircles();
		
		DrawingTools.returnElementsToDraw(true, cci.getListOfPlacedCircles(), cci.getListOfAlignmentCircles());
	}
	
	/**
	 * Creates an initial outer alignemt Circle at the beginning of the iteration
	 * @param spaceOfCircle space of the circle we create
	 */
	public Circle createOuterAlignementCircle(double spaceOfCircle){
		
		Circle outerCircle = new Circle(0,0, Math.sqrt(spaceOfCircle/Math.PI));
		
		return outerCircle;
	}
	
	//---------Getter & Setter-----------
	
	public double getTolerance() {
		return tolerance;
	}	
	
	public boolean getFinished() {
		return finished;
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
	
	public ArrayList<Circle> getListOfABC(){
		return this.listOfIntersectionPoints;
	}

	public int getCirclesLeftToPlace() {
		return circlesLeftToPlace;
	}

	public ArrayList<Circle> getListOfPlacedCircles() {
		return listOfPlacedCircles;
	}


	public ArrayList<Circle> getListOfAlginementCircles() {
		return listOfAlginementCircles;
	}

}
