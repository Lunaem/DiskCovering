package algorithm;

import java.util.ArrayList;
import java.util.ListIterator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CC_Iteration {
	//threshold used to allow minimal errors in calculations
		//final double threshold = 1e-6;
		
		//boolean value set to true if algorithm is finished
		private boolean finished;
		//boolean value whether ccw is used
		private boolean ccw = false;
		//boolean value whether poh is used
		private boolean poh = false;
		
		private double tolerance;
		
		private Circle outerAlignmentCircle;
		
	//List to store all circles that need to be placed in current iteration 
		private ArrayList<Circle> listOfCirclesToPlace = new ArrayList<Circle>();
		//List to store all circles placed
		private ArrayList<Circle> listOfPlacedCircles = new ArrayList<Circle>();
		private ArrayList<Circle> listOfAlginementCircles = new ArrayList<Circle>();
		
		

		//last Point of Intersection
		private Point lastIntersectionPoint;
		
		
		public CC_Iteration (Circle outerAlignmentCircle, ArrayList<Integer> listOfRadii, double tolerance, boolean ccw, boolean poh) {
			this.outerAlignmentCircle = outerAlignmentCircle;
			for(int i = 0; i <= listOfRadii.size() -1; i++) {
				Circle c = new Circle (listOfRadii.get(i));
				listOfCirclesToPlace.add(c);
			}
			this.tolerance = tolerance;
			this.finished = false;
			this.ccw = ccw;
			this.poh = poh;
			
		}
	
	//-------------FILL-------------	
	/**
	 * Fills an outer alignment circle by creating and filling new inner alignment circles
	 * @param outerAlignementCircle circle to be filled by the given circles
	 * @return if circle is covered completely -> true, else -> false
	 */
	public boolean fillOuterAlignementCircle() {
		listOfAlginementCircles.add(this.outerAlignmentCircle);
		
		Circle currentAlignmentCircle = outerAlignmentCircle;	
		Circle nextAlignmentCircle;
		while(!listOfCirclesToPlace.isEmpty()) {
			
			nextAlignmentCircle = fillAlignementCircle(currentAlignmentCircle);	
			if(nextAlignmentCircle == null) {
				break;
			}
			//TODO duplicate in list
			if(!listOfAlginementCircles.contains(nextAlignmentCircle)) {
				listOfAlginementCircles.add(nextAlignmentCircle);
			}
			
			currentAlignmentCircle = nextAlignmentCircle;	
		}
		
		return finished;
	}
	
	/**
	 * Fill one alignement circle
	 * @param currentAlignmentCircle
	 * @param uncoveredSpace2
	 * @return
	 */
	public Circle fillAlignementCircle(Circle currentAlignmentCircle) {
		boolean newRow = false;
		
		ArrayList<Circle> currentRow = new ArrayList<Circle>();
		Circle newAlignmentCircle = null;
		
		Circle firstCircle = listOfCirclesToPlace.get(0);
		if(placeFirstCircle(firstCircle, currentAlignmentCircle)) {
			finished = true;
			return null;
		}
		
		currentRow.add(firstCircle);
		//look if circle is covered
		Point veryFirstIntersectionPoint = findVeryFirstIntersectionPoint(firstCircle, currentAlignmentCircle);
		
		ListIterator<Circle> listIterator = listOfCirclesToPlace.listIterator(0); 
		
		while(listIterator.hasNext()) {
			Circle currentCircle = listIterator.next();
			
			currentCircle = placeNextCircle(currentCircle, currentAlignmentCircle);
			
			currentRow.add(currentCircle);
			
			listIterator.remove();
			
			//check if row is finished
			if(CC_Functions.getCenterPoint(currentCircle).calucateDistanceToPoint(veryFirstIntersectionPoint) <= currentCircle.getRadius()) {
				lookIfAlignmentCircleIsCovered(currentRow,currentAlignmentCircle);
				if(finished) {
					newRow = false;
					break;
				}
				else {
					newRow = true;
					break;
				}
			}
			//place circle over hole if nessesary
			if(currentRow.size() > 2 && poh) {
				
				if(CC_Functions.checkIfCirclesIntersect(currentCircle,firstCircle)) {
					System.out.println(firstCircle.getRadius());
					ArrayList<Point> intersectionPointsOfCirclesOfRow = CC_Functions.findIntersectionPoints(currentCircle, firstCircle);
					int i = currentRow.size();
					currentRow = placeCircleOverHole(veryFirstIntersectionPoint, currentCircle, currentAlignmentCircle, currentRow, intersectionPointsOfCirclesOfRow);
					//if poh was used
					if(i < currentRow.size()) {
						newRow = true;
						lookIfAlignmentCircleIsCovered(currentRow,currentAlignmentCircle);
						if(finished) {
							newRow = false;
						}
						break;
					}
					
				}
				
			}
				
		}
		if(newRow) {
			newAlignmentCircle = closeRow(currentRow,currentAlignmentCircle);
		}
		return newAlignmentCircle;
	}
	
	

	//--------------Place circles---------------
	/**
	 * Places first circle of the row
	 * @param circleToBePlaced circle that is to be placed first on the alignement circle 
	 * @param currentAlignmentCircle circle that is to be covered
	 * @return placed first circle
	 */
	public boolean placeFirstCircle(Circle circleToBePlaced, Circle currentAlignmentCircle) {
		Circle firstCircle = circleToBePlaced;
		if(firstCircle.getRadius() >= currentAlignmentCircle.getRadius()) {
			firstCircle.setCenterX(currentAlignmentCircle.getCenterX());
			firstCircle.setCenterY(currentAlignmentCircle.getCenterY());
			
			listOfPlacedCircles.add(firstCircle);
			listOfCirclesToPlace.remove(0);
			finished = true;
			return true;
		}
		
		firstCircle.setCenterX(currentAlignmentCircle.getCenterX() + (currentAlignmentCircle.getRadius() + (CC_Functions.getDiameter(firstCircle) * tolerance)) - firstCircle.getRadius());
		firstCircle.setCenterY(currentAlignmentCircle.getCenterY());
		listOfPlacedCircles.add(firstCircle);
		
		listOfCirclesToPlace.remove(0);
		return false;
	}
	
	/**
	 * Places one Circle
	 * @param circleToBePlaced circle that is to be placed
	 * @param outerCircle circle that is to be covered
	 */
	public Circle placeNextCircle(Circle circleToBePlaced, Circle outerCircle) {
		
		Circle currentCircle = CC_Functions.checkIfCircleIsBiggerThanAlignmentCircle(circleToBePlaced,outerCircle);
		Circle previousCircle = getListOfPlacedCircles().get(getListOfPlacedCircles().size()-1);
		
		//find intersection points
		ArrayList<Point> intersectPoints = CC_Functions.findIntersectionPoints(previousCircle,outerCircle);
		if(intersectPoints.size() <= 1) {
			System.out.println("Intersectionlist is empty");
		}
		
		Point intersectPoint = CC_Functions.findNextAlginmentPoint(intersectPoints, lastIntersectionPoint);
		lastIntersectionPoint = intersectPoint;	
		
		currentCircle = CC_Functions.setCenterOfNextCircle(currentCircle, intersectPoint, outerCircle, tolerance);
		
		listOfPlacedCircles.add(currentCircle);
		
		return currentCircle;
	}
	
	/**
	 * Finds very First Intersection Point that must be covered to fill on alignment circle
	 * @param firstCircle circle that is placed first
	 * @param alignmentCircle current alignment circle
	 * @return
	 */
	public Point findVeryFirstIntersectionPoint(Circle firstCircle, Circle alignmentCircle) {
		
		Point veryFirstIntersectionPoint = null;
		ArrayList<Point> intersectPoints = CC_Functions.findIntersectionPoints(firstCircle, alignmentCircle);
		Point alignmentPoint = new Point(alignmentCircle.getCenterX(), alignmentCircle.getRadius()); 
		Point intersectPoint = CC_Functions.findNextClockwisePoint(CC_Functions.getCenterPoint(alignmentCircle), alignmentPoint, intersectPoints, listOfAlginementCircles.size(), ccw);
		
		if(intersectPoint.equals(intersectPoints.get(0))){
			veryFirstIntersectionPoint = intersectPoints.get(1);
			lastIntersectionPoint = intersectPoints.get(1);
		}
		else if(intersectPoint.equals(intersectPoints.get(1))) {
			veryFirstIntersectionPoint = intersectPoints.get(0);
			lastIntersectionPoint = intersectPoints.get(0);
		}
		else{
			System.out.println(" Intersection Point not found!" );
		}
		return veryFirstIntersectionPoint;
	}
	
	//---------------end of row-----------------
	
	/**
	 * closes current row and places last circle
	 * @param currentRow all circles of the current row
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	public Circle closeRow(ArrayList<Circle> currentRow, Circle outerCircle) {
		ArrayList<Point> points = new ArrayList<Point>();
		for(IntersectionPoint z :CC_Functions.findInnerIntersectionPoints(currentRow, outerCircle)) {
			Point p  = new Point(z.getPointOfIntersection().getX(), z.getPointOfIntersection().getY());
			points.add(p);
		}
		if(CC_Functions.findInnerIntersectionPoints(currentRow, outerCircle).isEmpty()) System.out.print(" list of innerIntersectionpoints is empty ");
		Circle c = Miniball.makeCircle(points); 
		Circle newOuterCircle = new Circle(c.getCenterX(),c.getCenterY(),c.getRadius());
		
		
		if(newOuterCircle == null) System.out.print(" newOuterCircle is null ");
		
		listOfAlginementCircles.add(newOuterCircle);
		
		return newOuterCircle;
	}
	
	/**
	 * looks if the alginment circle is covered then sets finished to true if covered
	 * @param allCirclesOfCurrentRow list of all circles of current row
	 * @param alginmentCircle alginment circle
	 */
	public void lookIfAlignmentCircleIsCovered (ArrayList<Circle> allCirclesOfCurrentRow, Circle alginmentCircle){
		ArrayList<IntersectionPoint> points = new ArrayList<IntersectionPoint>();
		points = CC_Functions.findInnerIntersectionPoints(allCirclesOfCurrentRow, alginmentCircle);
		
		if(points.isEmpty()) {
			finished = true;
		}
		else {
			finished = false;
		}
		
	}
	
	//--------------special cases-----------------
	/**
	 * Places circle over the outer circle if circle to placed is bigger than outerCircle 
	 * @param currentCircle circle that is to be placed
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	public Circle placeCircleOverAlignmentCircle(Circle currentCircle, Circle outerCircle) {
		currentCircle.setCenterX(outerCircle.getCenterX());
		currentCircle.setCenterY(outerCircle.getCenterY());
		return currentCircle;
	}
	
	/**
	 * place a circle over the hole 
	 * @param veryFirstIntersectionPoint
	 * @param currentCircle
	 * @param currentAlignmentCircle
	 * @param currentRow
	 * @param intersectionPointsOfCirclesOfRow
	 * @return
	 */
	public ArrayList<Circle> placeCircleOverHole(Point veryFirstIntersectionPoint, Circle currentCircle, Circle currentAlignmentCircle, ArrayList<Circle> currentRow, ArrayList<Point> intersectionPointsOfCirclesOfRow) {
		//ArrayList<Circle> currentRow; = new ArrayList<Circle>();
		if(intersectionPointsOfCirclesOfRow.size() == 2) {
			
			Point point1;
			Point point2;
			
			if(intersectionPointsOfCirclesOfRow.get(0).calucateDistanceToPoint(veryFirstIntersectionPoint) > intersectionPointsOfCirclesOfRow.get(1).calucateDistanceToPoint(veryFirstIntersectionPoint)) {
				point1 = intersectionPointsOfCirclesOfRow.get(1);
			}
			else {
				point1 = intersectionPointsOfCirclesOfRow.get(0);
			}
			
			ArrayList<Point> intersectionPointsOfCircleAndAlign = new ArrayList<Point>(); 
			intersectionPointsOfCircleAndAlign = CC_Functions.findIntersectionPoints(currentCircle , currentAlignmentCircle);
			
			if(intersectionPointsOfCircleAndAlign.get(0).calucateDistanceToPoint(veryFirstIntersectionPoint) > intersectionPointsOfCircleAndAlign.get(1).calucateDistanceToPoint(veryFirstIntersectionPoint)) {
				point2 = intersectionPointsOfCircleAndAlign.get(1);
			}
			else {
				point2 = intersectionPointsOfCircleAndAlign.get(0);
			}
				
			Circle c = CC_Functions.calculateCircleFromThreePoints(veryFirstIntersectionPoint, point1 , point2);
			Circle fittingCircle = null;
			ListIterator<Circle> listIterator = listOfCirclesToPlace.listIterator(listOfCirclesToPlace.size()-1); 
			while(listIterator.hasPrevious()) {
			//for(int i = listOfCirclesToPlace.size()-1;i >= 0; i--){
				Circle c2 = listIterator.previous();
				//if(listOfCirclesToPlace.get(i).getRadius() >= c.getRadius()) {
				if(c2.getRadius() >= c.getRadius()) {	
					
					//fittingCircle = listOfCirclesToPlace.get(i);
					fittingCircle = c2;
					//listIterator.remove();
				}
			}
			if(fittingCircle != null ) {
				System.out.println("WTF");
				fittingCircle.setCenterX(c.getCenterX());
				fittingCircle.setCenterY(c.getCenterY());
				
				if(fittingCircle.getStroke() == null) fittingCircle.setStroke(Color.BLUE);
				
				listOfPlacedCircles.add(fittingCircle);
				currentRow.add(fittingCircle);
				listIterator.remove();
				
			}
			else {
				return currentRow;
			}
		} 
		return currentRow;
	}
	//-----------Getter & Setter-------------
	/**
	 * Gets the list of placed circles
	 * @return list of placed circles
	 */
	public ArrayList<Circle> getListOfPlacedCircles() {
		return listOfPlacedCircles;
	}

	/**
	 * Sets the list of placed circles
	 * @param list of placed circles which is to be set
	 */
	public void setListOfPlacedCircles(ArrayList<Circle> listOfPlacedCircles) {
		this.listOfPlacedCircles = listOfPlacedCircles;
	}
	
	/**
	 * Gets the list of circles to be placed
	 * @return list of circles to be placed
	 */
	public ArrayList<Circle> getListOfCirclesToPlace() {
		return listOfCirclesToPlace;
	}

	/**
	 * Sets the list of circles to be placed
	 * @param list of placed circles which is to be set
	 */
	public void setListOfCirclesToPlace(ArrayList<Circle> listOfAllCircles) {
		this.listOfCirclesToPlace = listOfAllCircles;
	}
	public Point getLastIntersectPoint_OuterCircleToCircle() {
		return lastIntersectionPoint;
	}

	

	public void setListOfAlignementCircles(ArrayList<Circle> listOfAlignementCircles) {
		this.listOfAlginementCircles = listOfAlignementCircles;
	}
	public ArrayList<Circle> getListOfAlignmentCircles(){
		return listOfAlginementCircles;
	}

}
