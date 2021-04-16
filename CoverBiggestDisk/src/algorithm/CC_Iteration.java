package algorithm;

import java.util.ArrayList;
import java.util.ListIterator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CC_Iteration {
		
		//boolean value set to true if outer alignment circle is covered
		private boolean isCovered;
		//boolean value whether ccw is used
		private boolean ccw;
		//boolean value whether poh is used
		private boolean poh;
		//tolerance with which the circles are placed
		private double tolerance;
		
		
		//List to store all circles that need to be placed in current iteration 
		private ArrayList<Circle> listOfCirclesToPlace;
		//List to store all circles placed
		private ArrayList<Circle> listOfPlacedCircles;
		private ArrayList<Circle> listOfAlignmentCircles;
		
		//point used to place circles on alignment circle
		private Point alignmentPoint;
		//the outer alignmentcircle
		private Circle outerAlignmentCircle;
		
		
		/**
		 * Constructor
		 * @param outerAlignmentCircle space to be covered by circles
		 * @param listOfRadii list of the radii (sorted, descending) of circles we want to place
		 * @param tolerance tolerance with which the circles are placed on the alignment circles
		 * @param ccw true-> use counterclokwise
		 * @param poh true -> use place over hole
		 */
		public CC_Iteration (Circle outerAlignmentCircle, ArrayList<Integer> listOfRadii, double tolerance, boolean ccw, boolean poh) {
			this.isCovered = false;
			this.listOfCirclesToPlace = new ArrayList<Circle>();
			this.listOfPlacedCircles = new ArrayList<Circle>();
			this.listOfAlignmentCircles = new ArrayList<Circle>();
			
			this.outerAlignmentCircle = outerAlignmentCircle;
			for(int i = 0; i <= listOfRadii.size() -1; i++) {
				Circle c = new Circle (listOfRadii.get(i));
				listOfCirclesToPlace.add(c);
			}
			this.tolerance = tolerance;
			this.ccw = ccw;
			this.poh = poh;
			
		}
	
	//-------------FILL-------------	
	/**
	 * Fills the outer alignment circle by creating and filling new inner alignment circles
	 * @param outerAlignementCircle circle to be filled by the given circles
	 * @return if circle is covered completely -> true, else -> false
	 */
	public boolean fillOuterAlignementCircle() {
		listOfAlignmentCircles.add(this.outerAlignmentCircle);
		
		Circle currentAlignmentCircle = outerAlignmentCircle;	
		Circle nextAlignmentCircle;
		while(!listOfCirclesToPlace.isEmpty()) {
			
			nextAlignmentCircle = fillAlignmentCircle(currentAlignmentCircle);	//setzt iscovered ?
			
			if(nextAlignmentCircle == null) {
				break;
			}
			//TODO why ? tell my why ..... :(
			//Björn: warum ist das notwendig ?
			//eigentlich dürfte next a nicht in liste sein
			if(!listOfAlignmentCircles.contains(nextAlignmentCircle)) {
				listOfAlignmentCircles.add(nextAlignmentCircle);
			}
			
			currentAlignmentCircle = nextAlignmentCircle;	
		}
		
		return isCovered;
	}
	
	/**
	 * Fill one alignement circle
	 * @param currentAlignmentCircle alignment circle we want to cover
	 * @return next alginment circle if posssible else null
	 */
	private Circle fillAlignmentCircle(Circle currentAlignmentCircle) {
		boolean newRow = false;
		
		ArrayList<Circle> currentRow = new ArrayList<Circle>();
		Circle newAlignmentCircle = null;
		
		//place first circle
		Circle firstCircle = listOfCirclesToPlace.get(0);
		if(placeFirstCircle(firstCircle, currentAlignmentCircle)) {
			isCovered = true;
			return newAlignmentCircle;
		}
		currentRow.add(firstCircle);
		
		Point veryFirstIntersectionPoint = findVeryFirstIntersectionPoint(firstCircle, currentAlignmentCircle);
		
		//place other circles
		ListIterator<Circle> listIterator = listOfCirclesToPlace.listIterator(0); 
		
		while(listIterator.hasNext()) {
			Circle currentCircle = listIterator.next();
			
			currentCircle = placeNextCircle(currentCircle, currentAlignmentCircle);
			
			currentRow.add(currentCircle);
			
			listIterator.remove();
			
			//check if row is finished
			if(CC_Functions.getCenterPoint(currentCircle).calucateDistanceToPoint(veryFirstIntersectionPoint) <= currentCircle.getRadius()) {
				lookIfAlignmentCircleIsCovered(currentRow,currentAlignmentCircle);
				if(isCovered) {
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
						if(isCovered) {
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
	 * @param alignmentCircle circle that is to be covered
	 * @return placed first circle
	 */
	private boolean placeFirstCircle(Circle circleToBePlaced, Circle alignmentCircle) {
		Circle firstCircle = circleToBePlaced;
		if(firstCircle.getRadius() >= alignmentCircle.getRadius()) {
			firstCircle.setCenterX(alignmentCircle.getCenterX());
			firstCircle.setCenterY(alignmentCircle.getCenterY());
			
			listOfPlacedCircles.add(firstCircle);
			listOfCirclesToPlace.remove(0);
			isCovered = true;
			return true;
		}
		
		firstCircle.setCenterX(alignmentCircle.getCenterX() + (alignmentCircle.getRadius() + (CC_Functions.getDiameter(firstCircle) * tolerance)) - firstCircle.getRadius());
		firstCircle.setCenterY(alignmentCircle.getCenterY());
		
		listOfPlacedCircles.add(firstCircle);
		listOfCirclesToPlace.remove(0);
		
		return false;
	}
	
	/**
	 * Places one Circle on the alignment circle
	 * @param circleToBePlaced circle that is to be placed
	 * @param alignmentCircle circle that is to be covered
	 * @return placed circle
	 */
	private Circle placeNextCircle(Circle circleToBePlaced, Circle alignmentCircle) {
		
		Circle currentCircle = CC_Functions.checkIfCircleIsBiggerThanAlignmentCircle(circleToBePlaced,alignmentCircle);
		Circle previousCircle = getListOfPlacedCircles().get(getListOfPlacedCircles().size()-1);
		
		//find intersection points
		ArrayList<Point> intersectPoints = CC_Functions.findIntersectionPoints(previousCircle,alignmentCircle);
		if(intersectPoints.size() <= 1) {
			System.out.println("Intersectionlist is empty");
		}
		
		alignmentPoint = CC_Functions.findNextAlignmentPoint(intersectPoints, alignmentPoint);
		
		
		boolean enableCCW = false;
		if(ccw && listOfAlignmentCircles.size() % 2 == 0) {
			enableCCW = true;
		}
		currentCircle = CC_Functions.setCenterOfNextCircle(currentCircle, alignmentPoint, alignmentCircle, tolerance, enableCCW);
		
		listOfPlacedCircles.add(currentCircle);
		
		return currentCircle;
	}
	
	/**
	 * Finds very first Intersection Point that must be covered to fill on alignment circle
	 * @param firstCircle circle that is placed first
	 * @param alignmentCircle current alignment circle
	 * @return the very first intersection point
	 */
	private Point findVeryFirstIntersectionPoint(Circle firstCircle, Circle alignmentCircle) {
		
		Point veryFirstIntersectionPoint = null;
		ArrayList<Point> intersectPoints = CC_Functions.findIntersectionPoints(firstCircle, alignmentCircle);
		
		Point alignmentPointOfAlginmentCircle = new Point(alignmentCircle.getCenterX(), alignmentCircle.getRadius()); 
		boolean enableCCW = false;
		if(ccw && listOfAlignmentCircles.size() % 2 == 0) {
			enableCCW = true;
		}
		
		Point intersectPoint = CC_Functions.findNextClockwisePoint(CC_Functions.getCenterPoint(alignmentCircle), alignmentPointOfAlginmentCircle, intersectPoints,enableCCW);
		
		if(intersectPoint.equals(intersectPoints.get(0))){
			veryFirstIntersectionPoint = intersectPoints.get(1);
			alignmentPoint = intersectPoints.get(1);
		}
		else if(intersectPoint.equals(intersectPoints.get(1))) {
			veryFirstIntersectionPoint = intersectPoints.get(0);
			alignmentPoint = intersectPoints.get(0);
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
	 * @param alignmentCircle circle that is to be covered
	 * @return new alignment circle
	 */
	private Circle closeRow(ArrayList<Circle> currentRow, Circle alignmentCircle) {
		
		ArrayList<Point> points = new ArrayList<Point>();
		for(IntersectionPoint z :CC_Functions.findInnerIntersectionPoints(currentRow, alignmentCircle)) {
			Point p  = new Point(z.getPointOfIntersection().getX(), z.getPointOfIntersection().getY());
			points.add(p);
		}
		if(CC_Functions.findInnerIntersectionPoints(currentRow, alignmentCircle).isEmpty()) System.out.print(" list of innerIntersectionpoints is empty ");
		
		Circle c = Miniball.makeCircle(points); 
		Circle newOuterCircle = new Circle(c.getCenterX(),c.getCenterY(),c.getRadius());
		
		if(newOuterCircle == null) System.out.print(" newAlignmentCircle is null ");
		
		listOfAlignmentCircles.add(newOuterCircle);
		
		return newOuterCircle;
	}
	
	/**
	 * looks if the alginment circle is covered then sets finished to true if covered
	 * @param allCirclesOfCurrentRow list of all circles of current row
	 * @param alginmentCircle current alginment circle
	 */
	private void lookIfAlignmentCircleIsCovered (ArrayList<Circle> allCirclesOfCurrentRow, Circle alginmentCircle){
		ArrayList<IntersectionPoint> points = new ArrayList<IntersectionPoint>();
		points = CC_Functions.findInnerIntersectionPoints(allCirclesOfCurrentRow, alginmentCircle);
		
		if(points.isEmpty()) {
			isCovered = true;
		}
		else {
			isCovered = false;
		}
		
	}
	
	//--------------special cases-----------------
	/**
	 * Places circle over the alignment circle if circle to placed is bigger than alignmentcircle 
	 * @param currentCircle circle that is to be placed
	 * @param alignmentCircle circle that is to be covered
	 * @return placed circle
	 */
	private Circle placeCircleOverAlignmentCircle(Circle currentCircle, Circle alignmentCircle) {
		currentCircle.setCenterX(alignmentCircle.getCenterX());
		currentCircle.setCenterY(alignmentCircle.getCenterY());
		return currentCircle;
	}
	
	/**
	 * place a circle over the hole 
	 * @param veryFirstIntersectionPoint the point we need to cover to close a row
	 * @param currentCircle circle that was just placed
	 * @param currentAlignmentCircle alginment circle
	 * @param currentRow list of all circles of current row
	 * @param intersectionPointsOfCirclesOfRow all Intersection points of the current row
	 * @return the list of the current row addec with the circle just placed 
	 */
	private ArrayList<Circle> placeCircleOverHole(Point veryFirstIntersectionPoint, Circle currentCircle, Circle currentAlignmentCircle, ArrayList<Circle> currentRow, ArrayList<Point> intersectionPointsOfCirclesOfRow) {
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
	
	public ArrayList<Circle> getListOfPlacedCircles() {
		return listOfPlacedCircles;
	}

	
	public void setListOfPlacedCircles(ArrayList<Circle> listOfPlacedCircles) {
		this.listOfPlacedCircles = listOfPlacedCircles;
	}
	
	
	public ArrayList<Circle> getListOfCirclesToPlace() {
		return listOfCirclesToPlace;
	}

	
	public void setListOfCirclesToPlace(ArrayList<Circle> listOfAllCircles) {
		this.listOfCirclesToPlace = listOfAllCircles;
	}
	
	public Point getLastIntersectPoint_OuterCircleToCircle() {
		return alignmentPoint;
	}

	public void setListOfAlignementCircles(ArrayList<Circle> listOfAlignementCircles) {
		this.listOfAlignmentCircles = listOfAlignementCircles;
	}
	public ArrayList<Circle> getListOfAlignmentCircles(){
		return listOfAlignmentCircles;
	}

}
