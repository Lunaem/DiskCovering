package algorithm;

import java.util.ArrayList;


import javafx.scene.shape.Circle;

public class CC_Functions {
	
	//threshold used to allow minimal errors in calculations
	final static double epsilon = 1e-6;
	
	
	//------------Calculation of Points-------------
	
	/**
	 * Finds intersection point that is to be used for placing the next circle
	 * @param intersectionPoints list of intersection points between circle and alignment circle
	 * @param prevUsedIntersectionPoint intersection point previously used for placing a circle
	 * @return intersection point of circle and alignment circle that is used to place the next circle	 
	 */
	public static Point findNextAlignmentPoint(ArrayList<Point> intersectionPoints, Point prevUsedIntersectionPoint) {
		Point alignnmentPoint = null;
		try {
			if(intersectionPoints.size() != 2) {
				throw new Exception("findNextAlignmentPoint: Wrong Intersection Point count " + intersectionPoints.size() + " instead of 2");
			}
			if(intersectionPoints.get(0) == null || intersectionPoints.get(1) == null) {
				throw new Exception("findNextAlignmentPoint: atleast one intersection point is null");
			}
			
			if(Math.abs(intersectionPoints.get(0).getX()- prevUsedIntersectionPoint.getX()) < epsilon && Math.abs(intersectionPoints.get(0).getY()- prevUsedIntersectionPoint.getY()) < epsilon){
				alignnmentPoint = intersectionPoints.get(1);
			}
			else if(Math.abs(intersectionPoints.get(1).getX()- prevUsedIntersectionPoint.getX()) < epsilon && Math.abs(intersectionPoints.get(1).getY()- prevUsedIntersectionPoint.getY()) < epsilon) {
				alignnmentPoint = intersectionPoints.get(0);
			}
			else{
				throw new Exception("findNextAlignmentPoint: Alignment Point not found!" );
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return alignnmentPoint;
	}
	/**
	 * Finds next clockwise point out of two 
	 * determines the smallest angle to the anchor point, based on the point pCenter
	 * @param pCenter center of alignment circle
	 * @param anchorPoint anchor point for angle calculation
	 * @param points points which are to be tested (expects two points)
	 * @param ccw if true->searches counter clockwise
	 */
	public static Point findNextClockwisePoint(Point pCenter, Point anchorPoint, ArrayList<Point> points, boolean ccw) {
		try {
			if(points.size() != 2) {
				throw new Exception("findNextClockwisePoint: Wrong Point count " + points.size() + " instead of 2");
			}
			if(points.get(0) == null || points.get(1) == null || pCenter == null || anchorPoint == null) {
				throw new Exception("findNextClockwisePoint: atleast one point is null");
			}
		}
		catch (Exception e){
				System.out.println(e.getMessage());
				e.printStackTrace();
		}
				Point pAnchor = anchorPoint;
				Point p1 = points.get(0);
				Point p2 = points.get(1);
				
				// The atan2 functions return arctan y/x in the interval [−π , +π] radians
				double pCenter_pAnchor = Math.atan2(pAnchor.getY() - pCenter.getY(), pAnchor.getX() - pCenter.getX());
				double pCenter_p1 = Math.atan2(p1.getY() - pCenter.getY(), p1.getX() - pCenter.getX());
				double pCenter_p2 = Math.atan2(p2.getY() - pCenter.getY(), p2.getX() - pCenter.getX());
				double angle_pCenter_pAnchor_p1 = pCenter_pAnchor - pCenter_p1;
				double angle_pCenter_pAnchor_p2 = pCenter_pAnchor - pCenter_p2;
				
				// Handle wrap around angle_pCenter_pAnchor_p1
				if (angle_pCenter_pAnchor_p1 > Math.PI) angle_pCenter_pAnchor_p1 -= 2*Math.PI;
				else if (angle_pCenter_pAnchor_p1 < -Math.PI) angle_pCenter_pAnchor_p1 += 2*Math.PI;
				
				// Handle wrap around angle_pCenter_pAnchor_p2
				if (angle_pCenter_pAnchor_p2 > Math.PI) angle_pCenter_pAnchor_p2 -= 2*Math.PI;
				else if (angle_pCenter_pAnchor_p2 < -Math.PI) angle_pCenter_pAnchor_p2 += 2*Math.PI;
					
				
				//counterclockwise option
				if (ccw == true){
					if(angle_pCenter_pAnchor_p2 > angle_pCenter_pAnchor_p1) return p2;
					else return p1;
				}
				//default: next clockwise point
				else{	
					if(angle_pCenter_pAnchor_p2 > angle_pCenter_pAnchor_p1) return p1;
					else return p2;
				}
				
	}
	
	/**
	 * Calculates the center Point of the next circle to be placed in the alignment circle
	 * @param nextCircle circle that is about be placed
	 * @param firstAlignmentPoint point at which the circle is to be placed on the alignment circle (first intersection point of alignment- and next-circle)
	 * @param alignmentCircle alignment circle for placing next circle
	 * @param tolerance tolerance for placing circles
	 * @param ccw determines if next circle is clock- or counterclockwise
	 * @return Center Point of next circle
	 */
	public static Point calculateCenterPointOfNextCircle(Circle nextCircle, Point firstAlignmentPoint, Circle alignmentCircle, double tolerance, boolean ccw) {
		
		//places nextCircle with tolerance at arbitrary position -> to calculate distance between intersection points with alignment circle
		nextCircle.setCenterX(alignmentCircle.getCenterX() + (alignmentCircle.getRadius() + (getDiameter(nextCircle) * tolerance)) - nextCircle.getRadius());
		nextCircle.setCenterY(alignmentCircle.getCenterY());
		
		ArrayList<Point> arbitraryintersectPoints = findIntersectionPoints(nextCircle,alignmentCircle);
		Point p_AlignCenter = getCenterPoint(alignmentCircle);
		Point p1 = arbitraryintersectPoints.get(0);
		Point p2 = arbitraryintersectPoints.get(1);
		
		//distance between arbitrary intersection points
		double p1_p2 = p1.calucateDistanceToPoint(p2);
		
		// --- Calculate second alignment Point ---
			//temp circle with center at first alignment point -> second alignment point is one of temp circles intersection points
			Circle tempCircle = new Circle(firstAlignmentPoint.getX(), firstAlignmentPoint.getY(), p1_p2);
			ArrayList<Point> possibleSecondAlignmentPoints = findIntersectionPoints(alignmentCircle, tempCircle);
			//determine second alignment point based on current placing direction
			Point secondAlignmentPoint = findNextClockwisePoint(p_AlignCenter, firstAlignmentPoint, possibleSecondAlignmentPoints, ccw);		
		
		//using two circles (center at both alignment points) to determine center of nextCircle
		Circle circle_firstAP = new Circle(firstAlignmentPoint.getX(),firstAlignmentPoint.getY(), nextCircle.getRadius());
		Circle circle_secondAP = new Circle(secondAlignmentPoint.getX(),secondAlignmentPoint.getY(), nextCircle.getRadius());
		ArrayList<Point> possibleCircleCenters = findIntersectionPoints(circle_firstAP, circle_secondAP);
		
		//determine correct center point, based on distance to center of alignment circle (depends on tolerance)
		double expected_distance = ((alignmentCircle.getRadius() + (getDiameter(nextCircle) * tolerance)) - nextCircle.getRadius());
		double distanceA = possibleCircleCenters.get(0).calucateDistanceToPoint(p_AlignCenter);
		double distanceB = possibleCircleCenters.get(1).calucateDistanceToPoint(p_AlignCenter);
		
		if(Math.abs(distanceA - expected_distance) < epsilon) {
			return possibleCircleCenters.get(0);
		}
		else if(Math.abs(distanceB - expected_distance) < epsilon) {
			return possibleCircleCenters.get(1);
		}
		else {
			System.out.print("calculateCenterPointOfNextCircle: Center of next Circle not found! ");
			return null;
		}
	}
	
	/**
	 * Returns the intersection points between 2 circles 
	 * based on http://paulbourke.net/geometry/circlesphere/
	 * @param circle0
	 * @param circle1
	 * @return list of intersection points
	 */
	public static ArrayList<Point> findIntersectionPoints(Circle circle0, Circle circle1) {
		ArrayList<Point> points = new ArrayList<Point>();
		//Calculate distance from P0 to P1
		double dx = circle0.centerXProperty().get()-circle1.centerXProperty().get();
		double dy = circle0.centerYProperty().get()-circle1.centerYProperty().get();
		double d = Math.hypot(dx,dy);
		
		
		//catch unique cases
		if(d > (circle0.getRadius() + circle1.getRadius()) ) {
			System.out.println("findIntersectionPoints: circles too far away -> intersection points between circles dont exist!");
			return points;
		}
		else if(d < Math.abs(circle0.getRadius() - circle1.getRadius())) {
			System.out.println("findIntersectionPoints: one circle contained in other -> intersection points between circles dont exist!");
			return points;
		}
		else if(d == 0 && (circle0.getRadius() == circle1.getRadius()) ) {
			System.out.println("findIntersectionPoints: circles are identical -> intersection points between circles dont exist!");
			return points;
		}
		
		//Calculate distance from P0 to P2
		double a = (Math.pow(circle1.getRadius(),2) - Math.pow(circle0.getRadius(),2) + Math.pow(d,2))/(2*d);
		
		//Calculate distance from P3 to P2
		double h = Math.sqrt(Math.pow(circle1.getRadius(),2) - Math.pow(a,2));
		
		//Calculate point P2
		double point2_x = circle1.centerXProperty().get() + (a*(dx))/d;   
		double point2_y = circle1.centerYProperty().get() + (a*(dy))/d;     
		
		//Calculating the intersection points based on P2
		double rx = ((-1) * dy) * (h/d);
		double ry = dx * (h/d);
		
		double firstIntersectionPoint_x = point2_x + rx;
		double firstIntersectionPoint_y = point2_y + ry;
		
		double secondIntersectionPoint_x = point2_x - rx;
		double secondIntersectionPoint_y = point2_y - ry;
		
		if(firstIntersectionPoint_x == secondIntersectionPoint_x && firstIntersectionPoint_y == secondIntersectionPoint_y) {
			System.out.println("findIntersectionPoints: Only one Intersection Point!");
			points.add(new Point(firstIntersectionPoint_x,firstIntersectionPoint_y));
			return points;
		}
		
		Point firstIntersectionPoint = new Point(firstIntersectionPoint_x,firstIntersectionPoint_y); 
		Point secondIntersectionPoint  = new Point(secondIntersectionPoint_x,secondIntersectionPoint_y); 
		points.add(firstIntersectionPoint);
		points.add(secondIntersectionPoint);
		
		
		return points;
	} 

		/**
		 * Finds inner intersection points between circles of one row (considers the points closest to the alignment center point) which touch uncovered space 
		 * @param allCirclesOfCurrentRow a list of all circles of the current row
		 * @param alignmentCircle alignment circle of current row
		 * @return list of the inner intersection points that are not in other circles of the row -> points that touch uncovered space
		 */
		public static ArrayList<IntersectionPoint> findInnerIntersectionPoints(ArrayList<Circle> allCirclesOfCurrentRow, Circle alignmentCircle){
			ArrayList<IntersectionPoint> result = new ArrayList<IntersectionPoint>();
			IntersectionPoint point = null;
			ArrayList<Point> intersectionPoints = null;
			double distance1, distance2;
			Circle c1, c2;
			
			for (int i=0;i<allCirclesOfCurrentRow.size();i++) {
				c1 = allCirclesOfCurrentRow.get(i);
				if (i == allCirclesOfCurrentRow.size()-1) c2 = allCirclesOfCurrentRow.get(0);
				else c2 = allCirclesOfCurrentRow.get(i+1);
				
				if(checkIfCirclesIntersect(c1, c2)) {
					intersectionPoints = findIntersectionPoints(c1, c2);
					distance1 = intersectionPoints.get(0).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
					distance2 = intersectionPoints.get(1).calucateDistanceToPoint(getCenterPoint(alignmentCircle));
					
					if(distance1 < distance2) {
						point = new IntersectionPoint(intersectionPoints.get(0),c1,c2);
					}
					else if(distance1 > distance2) {
						point = new IntersectionPoint(intersectionPoints.get(1),c1,c2);
					}
					else {
						System.out.print("findInnerIntersectionPoints: Inner Intersectionpoint not found Loop" + " " + distance1 + " " + distance2);
					}
					
					if(!checkIfIntersectionPointInOtherCircle(point,allCirclesOfCurrentRow)) {
						result.add(point);
					}
				}
				
			}
			return result;
		}
	
	//------------Calculations of Circles-------------
	
	/**
	 * Sets center for the next Circle on the alignment circle
	 * @param nextCircle circle that is being placed
	 * @param firstAlignmentPoint first intersection point of nextCircle and alignment circle
	 * @param alignmentCircle current alignment circle
	 * @param ccw counter clockwise option
	 * @return nextCircle reference	 
	 */
	public static Circle setCenterOfNextCircle(Circle nextCircle, Point firstAlignmentPoint, Circle alignmentCircle, double tolerance, boolean ccw) {
		
		Point nextCircleCenter = calculateCenterPointOfNextCircle(nextCircle, firstAlignmentPoint, alignmentCircle, tolerance, ccw);
		
		if(nextCircleCenter == null) {
			nextCircle.setCenterX(alignmentCircle.getCenterX());
			nextCircle.setCenterY(alignmentCircle.getCenterY());
		}
		else {
		nextCircle.setCenterX(nextCircleCenter.getX());
		nextCircle.setCenterY(nextCircleCenter.getY());
		}
		
		
		return nextCircle;
	}
	
	/**
	 * Calculates circle using three points
	 * based on: http://web.archive.org/web/20161011113446/http://www.abecedarical.com/zenosamples/zs_circle3pts.html
	 * @param p1 first point 
	 * @param p2 second point 
	 * @param p3 third point
	 * @return circle
	 */
	public static Circle calculateCircleFromThreePoints(Point p1, Point p2, Point p3) {
		try {
			if(p1.calucateDistanceToPoint(p2) == 0 || p1.calucateDistanceToPoint(p3) == 0 || p2.calucateDistanceToPoint(p3) == 0) {
				throw new Exception("calculateCircleFromThreePoints: Points are the same");
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
	}
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
		
		double detA = findDeterminant(matA);
		double detB = -1 * findDeterminant(matB);
		double detC = findDeterminant(matC);
		double detD = -1 * findDeterminant(matD);
		
		if(detA == 0) {
			System.out.println("calculateCircleFromThreePoints: Circle cannot be drawn over points");
		}
				
		double centerX = (-1 * detB) / (2 * detA);
		double centerY = (-1 * detC) / (2 * detA);
		
		double firstHalf = (Math.pow(detB, 2) + Math.pow(detC, 2)) / (4*Math.pow(detA,2));
		double secondHalf = (detD/detA);
		double radius = Math.sqrt(Math.abs(firstHalf - secondHalf));
		
		Circle circle = new Circle(centerX,centerY,radius);
		
		return circle;
	}
	
	/**
	 * Looks if next circle to be placed is bigger than the alignment Circle -> if true sets next circle on alignment circle center
	 * @param nextCircle circle that is about be placed
	 * @param alignmentCircle
	 * @return nextCircle reference
	 */
	public static Circle checkIfCircleIsBiggerThanAlignmentCircle(Circle nextCircle, Circle alignmentCircle) {
		if(nextCircle.getRadius() > alignmentCircle.getRadius()) {
			nextCircle.setCenterX(alignmentCircle.getCenterX());
			nextCircle.setCenterY(alignmentCircle.getCenterY());
		}
		
		return nextCircle;
	}
	

	
	//-------------Checks--------------
	
   /**
	 * Checks if Circles Intersect
	 * @param c1 first circle
	 * @param c2 second circle
	 * @return boolean if circles intersect -> true, if circles do not intersect -> false
	 */
	public static boolean checkIfCirclesIntersect(Circle c1, Circle c2) {
		double d = getCenterPoint(c1).calucateDistanceToPoint(getCenterPoint(c2));
		
		//case: No Intersection
		if (d > (c1.getRadius() + c2.getRadius())) {
			return false;
		}
		else if (d < Math.abs(c1.getRadius() - c2.getRadius())) {
			return false;
		} 
		//case: circles are identical
		else if(d==0 && c1.getRadius() == c2.getRadius()) {
			System.out.println("checkIfCirclesIntersect: inifinte # of intersection points!");
			return true;
		}
		
		return true;
	} 
	/**
	 * checks if an intersection point is in another circle of the current row
	 * @param point point of intersection
	 * @param circles list of all circles of the current row
	 * @return if in another circle -> true, else -> false
	 */
	public static boolean checkIfIntersectionPointInOtherCircle(IntersectionPoint point, ArrayList<Circle> circles) {
		ArrayList<Circle> remainingCircles = new ArrayList<Circle>();
		    
		for(int i = 0;i<=circles.size()-1;i++) {
			if(! (circles.get(i).equals(point.getCircle1()) || circles.get(i).equals(point.getCircle2())) ){
				remainingCircles.add(circles.get(i));
			}
		}
		
		boolean isIsInOtherCircle = false;
		for(int i = 0;i<=remainingCircles.size()-1;i++) {
			if(lookIfPointInCircle(remainingCircles.get(i), point.getPointOfIntersection())) {
				isIsInOtherCircle = true;
				break;
			}
		}
		
		return isIsInOtherCircle;
	}
	
	/**
	 * looks if point is in circle
	 * @param circle 
	 * @param p 
	 * @return if point in circle -> true, else -> false 
	 */
	public static boolean lookIfPointInCircle(Circle circle, Point p) {
		if(circle.getRadius() != 0 && getCenterPoint(circle).calucateDistanceToPoint(p) <= circle.getRadius() + epsilon) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	//------------Other----------------
		/**
		 * calculates the determinant of a 3x3 matrix using the sarus rule
		 * @param mat the matrix
		 * @return determinant
		 */
		public static double findDeterminant(double[][] mat) {
			double determinat = mat[0][0] * mat[1][1] * mat[2][2] +
						 	  mat[0][1] * mat[1][2] * mat[2][0] +
						 	  mat[0][2] * mat[1][0] * mat[2][1] -
						 	  mat[0][2] * mat[1][1] * mat[2][0] -
						 	  mat[1][2] * mat[2][1] * mat[0][0] -
						 	  mat[2][2] * mat[0][1] * mat[1][0];
			
			return determinat;
		}

	// -------- Getter + Setter ------------
	
		/**
		 * Gets the diamater of a circle
		 * @param c circle
		 * @return the diameter of the circle
		 */
		public static double getDiameter (Circle c) {
			return c.getRadius()*2;
		}
		
		/**
		 * Gets the center Point of a circle
		 * @param c circle
		 * @return Point center point
		 */
		public static Point getCenterPoint(Circle c) {
			Point p = new Point(c.getCenterX(), c.getCenterY());
			return p;
		}

		/**
		 * Gets the space of a circle
		 * @param c circle
		 * @return the space of the circle
		 */
		public static double getSpace (Circle c) {
			return (Math.pow(c.getRadius(),2)) * Math.PI;
		}
		
		
		
}
