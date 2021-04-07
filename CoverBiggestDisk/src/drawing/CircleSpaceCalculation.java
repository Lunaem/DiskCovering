package drawing;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import run.Data;
import geometricShapes.Point;
//TODO delete TRO
/**
 * Class used to create a drawing of the algorithm 
 * 
 * 
 * 
 */

public class CircleSpaceCalculation {
	
	//-------------ATTRIBUTES-----------------

	//List to store all circles 
	public ArrayList<Circle> listOfCircles_Data = new ArrayList<Circle>();
	public boolean finished;
	
	//-----------Only for one iteration----------------
	//List to store all circles that need to be placed in current iteration 
	public ArrayList<Circle> listOfCirclesToPlace = new ArrayList<Circle>();
	//List to store all circles placed
	public ArrayList<Circle> listOfPlacedCircles = new ArrayList<Circle>();
	public ArrayList<Circle> listOfAlignementCircles = new ArrayList<Circle>();
	//Shape that is not yet covered
	public Shape uncoveredSpace;
	//------end-------
	
	
	//threshold used to allow minimal errors in calculations
	final double threshold = 1e-6;
	//var used to stop the algorithm after 
	int stop_iteration = 1800; //TODO DELETE , add ad debug
	int global_iteration_count;
	boolean ccw = false;
	boolean poh = false;
	//----Only for one alignement circle-----
	//last Point of Intersection
	private Point lastIntersectPoint_OuterCircleToCircle;
	//-------end--------
	
	
	//list for tests
	//TODO DELETE , add to debug
	public ArrayList<Circle> listXYZ = new ArrayList<Circle>();
	public ArrayList<Circle> listOfIntersectionPoints = new ArrayList<Circle>();
	
	
	//tolerance for placing circles
	public double tolerance = 0.30; 
	
	//Comparator, used to sort Circle lists
		public static final Comparator<Circle> circleComparator = new Comparator<Circle>() {

		    public int compare(Circle c1, Circle c2) {
		    	if(c1.getRadius() < c2.getRadius()) {
		    		return 1;
		    	}
		    	else if(c1.getRadius() > c2.getRadius()) {
		    		return -1;
		    	}
		    	else {
		    		return 0;
		    	}
		    }
		 };
	
	public CircleSpaceCalculation(Data data) {
		loadData(data);	
	}
	//loads data of circles
	public void loadData(Data data) {
		
		for(Circle c: data.getListOfCircles_Dat()) {
			listOfCircles_Data.add(c);
		}
		if(listOfCircles_Data.isEmpty()) {
			System.out.print("List with data is empty!");
		}
		
		//sort list of circles
		Collections.sort(listOfCircles_Data, CircleSpaceCalculation.circleComparator);
	}
	/**
	 * Find biggest circle that is completely covered by given circles
	 */
	public void calculateBiggestCircle() {
		finished = false;
		int iteration = 0;
		
		double minSpace = getSpace(listOfCircles_Data.get(0));
		double maxSpace = 0;
		
		for(Circle c:listOfCircles_Data){
			maxSpace += (Math.pow(c.getRadius(),2) * Math.PI);
	    } 
		
		//try to cover circles of decreasing size
		while(!(finished && ((maxSpace - minSpace) <=  1e-10))) {
			listOfPlacedCircles.clear();
			listOfAlignementCircles.clear();
			listOfIntersectionPoints.clear();
			finished = false;
			
			double currentSpace = (minSpace + maxSpace) / 2;
			System.out.println("____________________");
			System.out.println(" iteration: " + iteration + " ");
			Circle outerAlignementCircle = createOuterAlignementCircle(currentSpace);
			finished = fillOuterAlignementCircle(outerAlignementCircle);
			
			System.out.println("space: " + getSpace(outerAlignementCircle)+ " ");
			System.out.println("min: " + minSpace + " ");
			System.out.println("max:  " + maxSpace+ " ");
			
			System.out.println(" circles left: " + listOfCirclesToPlace.size() + " ");
			
			
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
		
		returnElementsToDraw(true);
		
		//TODO look at slightly bigger circles
	}
	
	/**
	 * Fills an outer alignment circle by creating and filling new inner alignment circles
	 * @param outerAlignementCircle circle to be filled by the given circles
	 * @return if circle is covered completely -> true, else -> false
	 */
	private boolean fillOuterAlignementCircle(Circle outerAlignementCircle) {
		listOfAlignementCircles.add(outerAlignementCircle);
		listOfCirclesToPlace = (ArrayList<Circle>) listOfCircles_Data.clone();
		
		uncoveredSpace = new Circle(outerAlignementCircle.getCenterX(), outerAlignementCircle.getCenterY(), outerAlignementCircle.getRadius());
		
		Circle currentAlignmentCircle = outerAlignementCircle;	
		Circle nextAlignmentCircle;
		while(!listOfCirclesToPlace.isEmpty()) {
			
			nextAlignmentCircle = fillAlignementCircle(currentAlignmentCircle, uncoveredSpace);	
			if(nextAlignmentCircle == null) {
				break;
			}
			//TODO duplicate in list
			if(!listOfAlignementCircles.contains(nextAlignmentCircle)) {
				listOfAlignementCircles.add(nextAlignmentCircle);
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
	private Circle fillAlignementCircle(Circle currentAlignmentCircle, Shape uncoveredSpace2) {
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
			
			currentCircle = placeCircle(currentCircle, currentAlignmentCircle);
			
			currentRow.add(currentCircle);
			
			listIterator.remove();
			
			//check if row is finished
			if(getCenterPoint(currentCircle).calucateDistanceToPoint(veryFirstIntersectionPoint) <= currentCircle.getRadius()) {
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
				
				if(checkIfCirclesIntersect(currentCircle,firstCircle)) {
					System.out.println(firstCircle.getRadius());
					ArrayList<Point> intersectionPointsOfCirclesOfRow = findIntersectionPoints(currentCircle, firstCircle);
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
			intersectionPointsOfCircleAndAlign = findIntersectionPoints(currentCircle , currentAlignmentCircle);
			
			if(intersectionPointsOfCircleAndAlign.get(0).calucateDistanceToPoint(veryFirstIntersectionPoint) > intersectionPointsOfCircleAndAlign.get(1).calucateDistanceToPoint(veryFirstIntersectionPoint)) {
				point2 = intersectionPointsOfCircleAndAlign.get(1);
			}
			else {
				point2 = intersectionPointsOfCircleAndAlign.get(0);
			}
				
			Circle c = calculateCircleFromThreePoints(veryFirstIntersectionPoint, point1 , point2);
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
				fittingCircle.setStroke(Color.BLUE);
				
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
	
	
	/**
	 * closes current row and places last circle
	 * @param currentRow all circles of the current row
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	private Circle closeRow(ArrayList<Circle> currentRow, Circle outerCircle) {
		//tests for conversion
		ArrayList<Point2> points2 = new ArrayList<Point2>();
		
		for(IntersectionPoint z :findInnerIntersectionPoints(currentRow, outerCircle)) {
			Point2 p  = new Point2(z.getPointOfIntersection().getX(), z.getPointOfIntersection().getY());
			points2.add(p);
		}
		for(IntersectionPoint z :findInnerIntersectionPoints(currentRow, outerCircle)) {
			Point p  = new Point(z.getPointOfIntersection().getX(), z.getPointOfIntersection().getY());
			listOfIntersectionPoints.add(new Circle(p.getX(),p.getY(), 10));
		}
		if(findInnerIntersectionPoints(currentRow, outerCircle).isEmpty()) System.out.print(" list of innerIntersectionpoints is empty ");
		Circle2 cir2 = Miniball2.makeCircle(points2); 
		Circle newOuterCircle = new Circle(cir2.c.x, cir2.c.y, cir2.r);
		
		
		if(newOuterCircle == null) System.out.print(" newOuterCircle is null ");
		
		listOfAlignementCircles.add(newOuterCircle);
		
		return newOuterCircle;
	}

	/**
	 * Places circle over the outer circle if circle to placed is bigger than outerCircle 
	 * @param currentCircle circle that is to be placed
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	public Circle placeCircleOverAlignmentCircle(Circle currentCircle, Circle outerCircle) {
		currentCircle.setCenterX(uncoveredSpace.getLayoutBounds().getCenterX());
		currentCircle.setCenterY(uncoveredSpace.getLayoutBounds().getCenterY());
		uncoveredSpace = Shape.subtract(uncoveredSpace, currentCircle);
		return currentCircle;
	}
	/**
	 * Places first circle of the row
	 * @param currentCircle circle that is to be placed
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	private Circle placeCircle(Circle currentCircle, Circle outerCircle) {
		
		placeNextCircle(currentCircle, outerCircle);
		listOfPlacedCircles.add(currentCircle);
		return currentCircle;
	}

	/**
	 * Places first circle of the row
	 * @param circleToBePlaced circle that is to be placed first on the alignement circle 
	 * @param currentAlignmentCircle circle that is to be covered
	 * @return placed first circle
	 */
	private boolean placeFirstCircle(Circle circleToBePlaced, Circle currentAlignmentCircle) {
		Circle firstCircle = circleToBePlaced;
		if(firstCircle.getRadius() >= currentAlignmentCircle.getRadius()) {
			firstCircle.setCenterX(currentAlignmentCircle.getCenterX());
			firstCircle.setCenterY(currentAlignmentCircle.getCenterY());
			
			listOfPlacedCircles.add(firstCircle);
			listOfCirclesToPlace.remove(0);
			finished = true;
			return true;
		}
		
		firstCircle.setCenterX(currentAlignmentCircle.getCenterX() + (currentAlignmentCircle.getRadius() + (getDiameter(firstCircle) * tolerance)) - firstCircle.getRadius());
		firstCircle.setCenterY(currentAlignmentCircle.getCenterY());
		listOfPlacedCircles.add(firstCircle);
		uncoveredSpace = Shape.subtract(uncoveredSpace, firstCircle);
		
		listOfCirclesToPlace.remove(0);
		return false;
	}
	/**
	 * Finds very First Intersection Point that must be covered to fill on alignment circle
	 * @param firstCircle circle that is placed first
	 * @param alignmentCircle current alignment circle
	 * @return
	 */
	public Point findVeryFirstIntersectionPoint(Circle firstCircle, Circle alignmentCircle) {
		
		Point veryFirstIntersectionPoint = null;
		ArrayList<Point> intersectPoints = findIntersectionPoints(firstCircle, alignmentCircle);
		Point alignmentPoint = new Point(alignmentCircle.getCenterX(), alignmentCircle.getRadius()); 
		Point intersectPoint = findNextClockwisePoint(getCenterPoint(alignmentCircle), alignmentPoint, intersectPoints);
		
		if(intersectPoint.equals(intersectPoints.get(0))){
			veryFirstIntersectionPoint = intersectPoints.get(1);
			lastIntersectPoint_OuterCircleToCircle = intersectPoints.get(1);
		}
		else if(intersectPoint.equals(intersectPoints.get(1))) {
			veryFirstIntersectionPoint = intersectPoints.get(0);
			lastIntersectPoint_OuterCircleToCircle = intersectPoints.get(0);
		}
		else{
			System.out.println(" Intersection Point not found!" );
		}
		return veryFirstIntersectionPoint;
	}
	
	/**
	 * looks if circle is covered 
	 * 
	 * @return if circle covered -> true, if circle not covered -> false
	 */
	public boolean lookifCircleIsCovered(ArrayList<Circle> allCirclesOfCurrentRow, Circle alginmentCircle) {
		
		lookIfAlignmentCircleIsCovered(allCirclesOfCurrentRow, alginmentCircle);
		return finished;
		//else return false;
	}


	/**
	 * Places one Circle
	 * @param circleToBePlaced circle that is to be placed
	 * @param outerCircle circle that is to be covered
	 */
	public Circle placeNextCircle(Circle circleToBePlaced, Circle outerCircle) {
		
		Circle currentCircle = checkIfCircleIsBiggerThanOuterCircle(circleToBePlaced,outerCircle);
		Circle previousCircle = getListOfPlacedCircles().get(getListOfPlacedCircles().size()-1);
		
		//find intersection points
		ArrayList<Point> intersectPoints = findIntersectionPoints(previousCircle,outerCircle);
		if(intersectPoints.size() <= 1) {
			System.out.println("Intersectionlist is empty");
		}
		
		Point intersectPoint = findRightIntersectionPoint(intersectPoints);
		lastIntersectPoint_OuterCircleToCircle = intersectPoint;	
		
		currentCircle = findCurrentCircleCenter(currentCircle, intersectPoint, outerCircle);
		
		return currentCircle;
	}
	
	/**
	 * Finds center for currentCircle
	 * @param currentCircle circle that is being placed
	 * @param intersectPoint list of intersection points between circle and outerCircle
	 * @param outerCircle circle that is to be covered
	 * @return intersection point of circle and outerCircle that is used to place the next circle	 
	 */
	private Circle findCurrentCircleCenter(Circle currentCircle, Point intersectPoint, Circle outerCircle) {
		
		Point currentCircle_center = calculateCenterOfCircle(currentCircle, intersectPoint, outerCircle);
		
		if(currentCircle_center == null) {
			currentCircle.setCenterX(outerCircle.getCenterX());
			currentCircle.setCenterY(outerCircle.getCenterY());
		}
		else {
		currentCircle.setCenterX(currentCircle_center.getX());
		currentCircle.setCenterY(currentCircle_center.getY());
		}
		
		uncoveredSpace = Shape.subtract(uncoveredSpace, currentCircle);
		
		return currentCircle;
	}

	/**
	 * Finds intersection point that is to be used for placing the next circle
	 * @param intersectPoints list of intersection points between circle and outerCircle
	 * @return intersection point of circle and outerCircle that is used to place the next circle	 
	 */
	private Point findRightIntersectionPoint(ArrayList<Point> intersectPoints) {
		Point intersectPoint = null;
		
		if(Math.abs(intersectPoints.get(0).getX()- lastIntersectPoint_OuterCircleToCircle.getX()) < threshold && Math.abs(intersectPoints.get(0).getY()- lastIntersectPoint_OuterCircleToCircle.getY()) < threshold){
			intersectPoint = intersectPoints.get(1);
		}
		else if(Math.abs(intersectPoints.get(1).getX()- lastIntersectPoint_OuterCircleToCircle.getX()) < threshold && Math.abs(intersectPoints.get(1).getY()- lastIntersectPoint_OuterCircleToCircle.getY()) < threshold) {
			intersectPoint = intersectPoints.get(0);
		}
		else{
			System.out.println(" Intersection Point not found!" );
		}
		
		return intersectPoint;
	}

	
	/**
	 * Looks if circle to be places is bigger than the OuterCircle
	 * @param currentCircle circle that is about be placed
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	private Circle checkIfCircleIsBiggerThanOuterCircle(Circle currentCircle, Circle outerCircle) {
		if(currentCircle.getRadius() > outerCircle.getRadius()) {
			currentCircle.setCenterX(outerCircle.getCenterX());
			currentCircle.setCenterY(outerCircle.getCenterY());
			uncoveredSpace = Shape.subtract(uncoveredSpace, currentCircle);
		}
		
		return currentCircle;
	}

	/**
	 * Looks where to place the next circle
	 * @param currentCircle circle that is about be placed
	 * @param firstIntersectionPoint point at which the circle is to placed
	 * @param outerCircle circle that is to be covered
	 */
	private Point calculateCenterOfCircle(Circle currentCircle, Point firstIntersectionPoint, Circle outerCircle) {
		
		//currenctCircle irgendwo, aber in richtiger distanz setzen
		currentCircle.setCenterX(outerCircle.getCenterX() + (outerCircle.getRadius() + (getDiameter(currentCircle) * tolerance)) - currentCircle.getRadius());
		currentCircle.setCenterY(outerCircle.getCenterY());
		
		//schnittpunkte von currentCircle mit outerCircle berechnen
		ArrayList<Point> arbitraryintersectPoints = findIntersectionPoints(currentCircle,outerCircle);
		
		Point p0 = new Point(0,0);
		Point p1 = arbitraryintersectPoints.get(0);
		Point p2 = arbitraryintersectPoints.get(1);
		
		//distanz zwischen schnittpunkten berechnen
		double p1_p2 = p1.calucateDistanceToPoint(p2);
		
		//den zweiten echten schnittpunkt bestimmen
		//schnittpunkte zwischen outercircle und circle der durch erstens schnittpunkt gezeichnet wird
		Circle tempCircle = new Circle(firstIntersectionPoint.getX(), firstIntersectionPoint.getY(), p1_p2);
		ArrayList<Point> possibleSecondIntersectPoints = findIntersectionPoints(outerCircle, tempCircle);
			//abhängig vom erstne echten schnittpunkt bestimmen, welcher der beiden der zweite echte ist
		Point secondIntersectionPoint = findNextClockwisePoint(p0, firstIntersectionPoint, possibleSecondIntersectPoints);
		
		//mit beiden schnittpunkten und radius, das center vom kreis bestimmen
		Circle firstIntersection = new Circle(firstIntersectionPoint.getX(),firstIntersectionPoint.getY(), currentCircle.getRadius());
		Circle secondIntersection = new Circle(secondIntersectionPoint.getX(),secondIntersectionPoint.getY(), currentCircle.getRadius());
		
		ArrayList<Point> possibleCircleCenter = findIntersectionPoints(firstIntersection, secondIntersection);
		
		double toleranceDistance = ((outerCircle.getRadius() + (getDiameter(currentCircle) * tolerance)) - currentCircle.getRadius());
		
		double distanceA = possibleCircleCenter.get(0).calucateDistanceToPoint(getCenterPoint(outerCircle));
		double distanceB = possibleCircleCenter.get(1).calucateDistanceToPoint(getCenterPoint(outerCircle));
		
		if(Math.abs(distanceA - toleranceDistance) < threshold) {
			return possibleCircleCenter.get(0);
		}
		else if(Math.abs(distanceB - toleranceDistance) < threshold) {
			return possibleCircleCenter.get(1);
		}
		else {
			System.out.print(" Center of next Circle not found! ");
			return null;
		}
	}
	
	private Circle calculateCircleFromThreePoints(Point p1, Point p2, Point p3) {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		
		double [][] matA = new double[3][3];
		double [][] matB = new double[3][3];
		double [][] matC = new double[3][3];
		double [][] matD = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			matA [i][0] = points.get(i).getX();
			matA [i][1] = points.get(i).getY();
			matA [i][2] = 1;
			
			matB [i][0] = Math.pow(points.get(i).getX(), 2) + Math.pow(points.get(i).getY(), 2);
			matB [i][1] = points.get(i).getY();
			matB [i][2] = 1;
			
			matC [i][0] = Math.pow(points.get(i).getX(), 2) + Math.pow(points.get(i).getY(), 2);
			matC [i][1] = points.get(i).getX();
			matC [i][2] = 1;
			
			matD [i][0] = Math.pow(points.get(i).getX(), 2) + Math.pow(points.get(i).getY(), 2);
			matD [i][1] = points.get(i).getX();
			matD [i][2] = points.get(i).getY();
		}
		
		double detA = findDeterminat(matA);
		double detB = -1 * findDeterminat(matB);
		double detC = findDeterminat(matC);
		double detD = -1 * findDeterminat(matD);
				
		double centerX = (-1 * detB) / (2 * detA);
		double centerY = (-1 * detC) / (2 * detA);
		
		double firstHalf = (Math.pow(detB, 2) + Math.pow(detC, 2)) / (4*Math.pow(detA,2));
		double secondHalf = (detD/detA);
		double radius = Math.sqrt(Math.abs(firstHalf - secondHalf));
		
		Circle circle = new Circle(centerX,centerY,radius);
		
		return circle;
		
	}
	
	private static double findDeterminat(double[][] mat) {
		double determinat = mat[0][0] * mat[1][1] * mat[2][2] +
					 	  mat[0][1] * mat[1][2] * mat[2][0] +
					 	  mat[0][2] * mat[1][0] * mat[2][1] -
					 	  mat[0][2] * mat[1][1] * mat[2][0] -
					 	  mat[1][2] * mat[2][1] * mat[0][0] -
					 	  mat[2][2] * mat[0][1] * mat[1][0];
		
		return determinat;
	}
	
	/**
	 * Finds next clockwise point
	 * @param p0 center of circle
	 * @param firstIntersectionPoint anchor point
	 * @param possibleSecondIntersectPoints points which are to be proved
	 */
	private Point findNextClockwisePoint(Point p0, Point firstIntersectionPoint, ArrayList<Point> possibleSecondIntersectPoints) {
				
				Point pI = firstIntersectionPoint;
				Point p1 = possibleSecondIntersectPoints.get(0);
				Point p2 = possibleSecondIntersectPoints.get(1);
				
				// The atan2 functions return arctan y/x in the interval [−π , +π] radians
				double p0_pI = Math.atan2(pI.getY() - p0.getY(), pI.getX() - p0.getX());
				double p0_p1 = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
				double p0_p2 = Math.atan2(p2.getY() - p0.getY(), p2.getX() - p0.getX());
				double angle_p0_pI_p1 = p0_pI - p0_p1;
				double angle_p0_pI_p2 = p0_pI - p0_p2;
				
				//place clockwise
				
					// Handle wrap around
					if (angle_p0_pI_p1 > Math.PI) angle_p0_pI_p1 -= 2*Math.PI;
					else if (angle_p0_pI_p1 < -Math.PI) angle_p0_pI_p1 += 2*Math.PI;
					// Handle wrap around
					if (angle_p0_pI_p2 > Math.PI) angle_p0_pI_p2 -= 2*Math.PI;
					else if (angle_p0_pI_p2 < -Math.PI) angle_p0_pI_p2 += 2*Math.PI;
					
				if(listOfAlignementCircles.size() % 2 == 0 || ccw == false) {	
					if(angle_p0_pI_p2 > angle_p0_pI_p1) return p1;
					else return p2;
				}
				//place counterclockwise
				else {
					if(angle_p0_pI_p2 > angle_p0_pI_p1) return p2;
					else return p1;
				}
				
	}
	
	/**
	 * Gives back all the elements that will be drawn
	 * @param debug indicates if elements that are used for developing are to be shown
	 * @return Group group which holds all elements that are to be drawned
	 */
	public Group returnElementsToDraw(Boolean debug) {
		for(Circle c: listOfPlacedCircles) {
			c.setFill(Color.TRANSPARENT);
			if(c.getStroke() == null) {
				c.setStroke(Color.BLACK);
			}
		}
		
		Group root = new Group();	
		root.getChildren().addAll(listOfPlacedCircles);
		drawCoordinateSystem(root);
		
		//add debug elements
		if(debug) {
			//OuterCircles -> RED
			for(Circle c: listOfAlignementCircles) {
			c.setFill(Color.TRANSPARENT);
			c.setStroke(Color.RED);
			}
			root.getChildren().addAll(listOfAlignementCircles);
			
			
			//listfortests -> GREEN
			for(Circle i : this.listOfIntersectionPoints){
				i.setStroke(Color.GREEN);
				i.setFill(Color.TRANSPARENT);
			} 
			root.getChildren().addAll(listOfIntersectionPoints);
		}
		
		return root;
	}
	
	/**
	 * Adds a coordinate system to a group
	 * @param root group which the cordinates are to be added
	 */
	private void drawCoordinateSystem(Group root) {
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
		listOfIntersectionPoints.add(c);
	}
	
	/**
	 * Creates an initial outer Circle at the beginning of the iteration
	 * @param iteration current iteration
	 */
	public Circle createOuterAlignementCircle(double spaceOfCircle){
		
		Circle outerCircle = new Circle(0,0, Math.sqrt(spaceOfCircle/Math.PI));
		
		return outerCircle;
	}
	  
	/**
	 * Finds the two Intersectionpoints between circles 
	 * based on http://paulbourke.net/geometry/circlesphere/
	 * @param circle0
	 * @param circle1
	 * @return
	 */
	public ArrayList<Point> findIntersectionPoints(Circle circle0, Circle circle1) {
		ArrayList<Point> points = new ArrayList<Point>();
		//Calculate distance from P0 to P1
		double dx = circle0.centerXProperty().get()-circle1.centerXProperty().get();
		double dy = circle0.centerYProperty().get()-circle1.centerYProperty().get();
		double d = Math.hypot(dx,dy);
		
		if((circle0.getRadius() + circle1.getRadius() + 1e8 )< d ) {
			System.out.println(circle0.getRadius());
			System.out.println(circle1.getRadius());
			System.out.println(d);
			return points;
			
		}
		
		//Calculate distance from P0 to P2
		double a = (Math.pow(circle1.getRadius(),2) - Math.pow(circle0.getRadius(),2) + Math.pow(d,2))/(2*d);
		
		//Calculate distance from P3 to P2
		double h = Math.sqrt(Math.pow(circle1.getRadius(),2) - Math.pow(a,2));
		
		//Calculate point P2
		double point2_x = circle1.centerXProperty().get() + (a*(dx))/d;   
		double point2_y = circle1.centerYProperty().get() + (a*(dy))/d;     
		
		double rx = ((-1) * dy) * (h/d);
		double ry = dx * (h/d);
		
		double firstIntersectionPoint_x = point2_x + rx;
		double firstIntersectionPoint_y = point2_y + ry;
		
		double secondIntersectionPoint_x = point2_x - rx;
		double secondIntersectionPoint_y = point2_y - ry;
		
		Point firstIntersectionPoint = new Point(firstIntersectionPoint_x,firstIntersectionPoint_y); 
		Point secondIntersectionPoint  = new Point(secondIntersectionPoint_x,secondIntersectionPoint_y); 
		
		if(d == circle0.getRadius() + circle1.getRadius()) {
			System.out.println("!" + firstIntersectionPoint_x + " " + firstIntersectionPoint_y);
			System.out.println("!" + secondIntersectionPoint_x + " " + secondIntersectionPoint_y);
		}
		points.add(firstIntersectionPoint);
		points.add(secondIntersectionPoint);
		
		return points;
	} 
	
	/**
	 * Checks if Circles Intersect
	 * @param c1 first circle
	 * @param c2 second circle
	 * @return boolean if circles intersect -> true, if circles do not intersect -> false
	 */
	private boolean checkIfCirclesIntersect(Circle c1, Circle c2) {
		double d = getCenterPoint(c1).calucateDistanceToPoint(getCenterPoint(c2));
		
		//case: No Intersection
		if (d > (c1.getRadius() + c2.getRadius())) {
			return false;
		}
		//case: Intersection
		else if(d <= Math.abs(c1.getRadius() + c2.getRadius())) {
			return true;
		} 
		//case: circles are overlaying
		else if(d==0 && c1.getRadius() == c2.getRadius()) {
			return true;
		}
		else {
			System.out.print("Interscetion check not possible");
			return false;
		}
	} 
	
	//TODO sonderfall in schleife rein
	/**
	 * Finds inner intersection points of one row
	 * @param allCirclesOfCurrentRow a list of all circles of the current row
	 * @param alignmentCircle current outerCircle
	 * @return list of the inner intersection points that are not in other circles of the row
	 */
	public ArrayList<IntersectionPoint> findInnerIntersectionPoints(ArrayList<Circle> allCirclesOfCurrentRow, Circle alignmentCircle){
		ArrayList<IntersectionPoint> points = new ArrayList<IntersectionPoint>();
		IntersectionPoint point = null;
		
		ArrayList<Point> possiblePoints = findIntersectionPoints(allCirclesOfCurrentRow.get(0), allCirclesOfCurrentRow.get(allCirclesOfCurrentRow.size()-1));
		double distance1 = possiblePoints.get(0).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
		double distance2 = possiblePoints.get(1).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
		
		if(distance1 <= distance2) {
			point = new IntersectionPoint(possiblePoints.get(0),allCirclesOfCurrentRow.get(0),allCirclesOfCurrentRow.get(allCirclesOfCurrentRow.size()-1));
		}
		else if(distance1 > distance2) {
			point = new IntersectionPoint(possiblePoints.get(1),allCirclesOfCurrentRow.get(0),allCirclesOfCurrentRow.get(allCirclesOfCurrentRow.size()-1));
		}
		else {
			System.out.print("Inner Intersectionpoint not found" + distance1 + " " + distance2);
		}
		
		if(!lookIfIntersectionPointInOtherCircle(point,allCirclesOfCurrentRow)) {
			points.add(point);
		}
		
		for (int i=0;i<allCirclesOfCurrentRow.size()-1;i++) {
			if(checkIfCirclesIntersect(allCirclesOfCurrentRow.get(i), allCirclesOfCurrentRow.get(i+1))) {
				possiblePoints = findIntersectionPoints(allCirclesOfCurrentRow.get(i), allCirclesOfCurrentRow.get(i+1));
				distance1 = possiblePoints.get(0).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
				distance2 = possiblePoints.get(1).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
				
				if(distance1 < distance2) {
					point = new IntersectionPoint(possiblePoints.get(0),allCirclesOfCurrentRow.get(i),allCirclesOfCurrentRow.get(i+1));
				}
				else if(distance1 > distance2) {
					point = new IntersectionPoint(possiblePoints.get(1),allCirclesOfCurrentRow.get(i),allCirclesOfCurrentRow.get(i+1));
				}
				else {
					System.out.print("Inner Intersectionpoint not found Loop" + " " + distance1 + " " + distance2);
				}
				
				if(!lookIfIntersectionPointInOtherCircle(point,allCirclesOfCurrentRow)) {
					points.add(point);
				}
			}
			
		}

		return points;
	}
	
	
	/**
	 * Looks if an intersection point is in another circle of the current row
	 * @param point point of intersection
	 * @param allCirclesOfCurrentRow list of all circles of the current row
	 * @return if in another circle -> true, else -> false
	 */
	public boolean lookIfIntersectionPointInOtherCircle(IntersectionPoint point, ArrayList<Circle> allCirclesOfCurrentRow) {
		ArrayList<Circle> circlesOfRow = (ArrayList<Circle>) allCirclesOfCurrentRow.clone();
		ArrayList<Circle> otherCirclesOfRow = new ArrayList<Circle>();
		boolean add = false;
		    
		for(int i = 0;i<=circlesOfRow.size()-1;i++) {
			
			if(circlesOfRow.get(i).equals(point.getCircle1())) {
				add = false;
			}
			else if(circlesOfRow.get(i).equals(point.getCircle2())) {
				add = false;
			}
			else {
				add = true;
			}
			if(add) {
				otherCirclesOfRow.add(circlesOfRow.get(i));
			}
		}
		boolean isIsInOtherCircle = false;
		for(int i = 0;i<=otherCirclesOfRow.size()-1;i++) {
			if(lookIfPointInCircle(otherCirclesOfRow.get(i), point.getPointOfIntersection())) {
				isIsInOtherCircle = true;
			}
		}
		
		return isIsInOtherCircle;
		
	}
	
	public void lookIfAlignmentCircleIsCovered (ArrayList<Circle> allCirclesOfCurrentRow, Circle alginmentCircle){
		ArrayList<IntersectionPoint> points = new ArrayList<IntersectionPoint>();
		points = findInnerIntersectionPoints(allCirclesOfCurrentRow, alginmentCircle);
		
		if(points.isEmpty()) {
			finished = true;
		}
		else {
			finished = false;
		}
		
	}

	public boolean lookIfPointInCircle(Circle circle, Point point) {
		boolean right = false; 
		if(getCenterPoint(circle).calucateDistanceToPoint(point) < circle.getRadius()) {
			right = true;
			//return true;
		}
		else {
			right = false;
			//return false;
		}
		return right;
		
	}
	
	// -------- Getter + Setter ------------
	
	/**
	 * Gets the diamater of a circle
	 * @param c circle
	 * @return the diameter of the circle
	 */
	public double getDiameter (Circle c) {
		
		return c.getRadius()*2;
		
	}
	
	/**
	 * Gets the center Point of a circle
	 * @param c circle
	 * @return Point center point
	 */
	public Point getCenterPoint(Circle c) {
		Point p = new Point(c.getCenterX(), c.getCenterY());
		return p;
	}

	/**
	 * Gets the space of a circle
	 * @param c circle
	 * @return the space of the circle
	 */
	public double getSpace (Circle c) {
		
		return (Math.pow(c.getRadius(),2)) * Math.PI;
		
	}
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
	
	/**
	 * Gets the tolerance factor
	 * @return the tolerance factor
	 */
	public double getTolerance() {
		return tolerance;
	}	
	/**
	 * Gets the boolean value finished
	 * @return finished
	 */
	public boolean getFinished() {
		return finished;
	}	
	/**
	 * Sets if the circles are placed ccw or cw
	 * @param value
	 */
	public void setCCW(boolean value) {
		this.ccw = value;
	}
	/**
	 * Sets if the circles are placed over holes or not
	 * @param value
	 */
	public void setPOH(boolean value) {
		this.poh = value;
	}
	/**
	 * Sets the value of stop iteration
	 * @param value 
	 */
	public void setStopIteration(int value) {
		this.stop_iteration = value;
	}	
	/**
	 * Sets the value of the tolerance
	 * @param value 
	 */
	public void setTolerance(double value) {
		this.tolerance = value;
	}
	public ArrayList<Circle> getListOfAlignmentCircles(){
		return listOfAlignementCircles;
	}
	
	
	public double getRadiusOfDisk() {
		return this.listOfAlignementCircles.get(0).getRadius();
	}
	public ArrayList<Circle> getListOfABC(){
		return this.listOfIntersectionPoints;
	}
}
	
	
	
	
	




