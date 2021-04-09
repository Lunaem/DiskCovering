package algorithm;

import java.util.ArrayList;


import javafx.scene.shape.Circle;

public class CC_Functions {
	//threshold used to allow minimal errors in calculations
		final static double threshold = 1e-6;
	
	
	//------------Point-------------
	
	/**
	 * Finds intersection point that is to be used for placing the next circle
	 * @param intersectPoints list of intersection points between circle and outerCircle
	 * @return intersection point of circle and outerCircle that is used to place the next circle	 
	 */
	public static Point findAlginmentIntersectionPoint(ArrayList<Point> intersectPoints, Point lastIntersectionPoint) {
		Point intersectPoint = null;
		
		if(Math.abs(intersectPoints.get(0).getX()- lastIntersectionPoint.getX()) < threshold && Math.abs(intersectPoints.get(0).getY()- lastIntersectionPoint.getY()) < threshold){
			intersectPoint = intersectPoints.get(1);
		}
		else if(Math.abs(intersectPoints.get(1).getX()- lastIntersectionPoint.getX()) < threshold && Math.abs(intersectPoints.get(1).getY()- lastIntersectionPoint.getY()) < threshold) {
			intersectPoint = intersectPoints.get(0);
		}
		else{
			System.out.println(" Intersection Point not found!" );
		}
		
		return intersectPoint;
	}
	/**
	 * Finds next clockwise point
	 * @param p0 center of circle
	 * @param firstIntersectionPoint anchor point
	 * @param possibleSecondIntersectPoints points which are to be proved
	 */
	public static Point findNextClockwisePoint(Point p0, Point firstIntersectionPoint, ArrayList<Point> possibleSecondIntersectPoints, int rows, boolean ccw) {
				
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
					
				if(rows % 2 == 0 || ccw == false) {	
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
	 * Looks where to place the next circle
	 * @param currentCircle circle that is about be placed
	 * @param firstIntersectionPoint point at which the circle is to placed
	 * @param outerCircle circle that is to be covered
	 */
	public static Point calculateCenterPointOfCircle(Circle currentCircle, Point firstIntersectionPoint, Circle outerCircle, double tolerance) {
		
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
		Point secondIntersectionPoint = findNextClockwisePoint(p0, firstIntersectionPoint, possibleSecondIntersectPoints, 0, false);
		
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
	
	//------------IntersectionPoints--------------
	/**
	 * Finds the two Intersectionpoints between circles 
	 * based on http://paulbourke.net/geometry/circlesphere/
	 * @param circle0
	 * @param circle1
	 * @return
	 */
	public static ArrayList<Point> findIntersectionPoints(Circle circle0, Circle circle1) {
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
	//TODO sonderfall in schleife rein
		/**
		 * Finds inner intersection points of one row
		 * @param allCirclesOfCurrentRow a list of all circles of the current row
		 * @param alignmentCircle current outerCircle
		 * @return list of the inner intersection points that are not in other circles of the row
		 */
		public static ArrayList<IntersectionPoint> findInnerIntersectionPoints(ArrayList<Circle> allCirclesOfCurrentRow, Circle alignmentCircle){
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
	
	//------------Circle-------------
	/**
	 * Finds center for currentCircle
	 * @param currentCircle circle that is being placed
	 * @param intersectPoint list of intersection points between circle and outerCircle
	 * @param outerCircle circle that is to be covered
	 * @return intersection point of circle and outerCircle that is used to place the next circle	 
	 */
	public static Circle findCircleCenterWithIntersectionPoint(Circle currentCircle, Point intersectPoint, Circle outerCircle, double tolerance) {
		
		Point currentCircle_center = calculateCenterPointOfCircle(currentCircle, intersectPoint, outerCircle, tolerance);
		
		if(currentCircle_center == null) {
			currentCircle.setCenterX(outerCircle.getCenterX());
			currentCircle.setCenterY(outerCircle.getCenterY());
		}
		else {
		currentCircle.setCenterX(currentCircle_center.getX());
		currentCircle.setCenterY(currentCircle_center.getY());
		}
		
		
		return currentCircle;
	}
	
	
	
	/**
	 * Calculates circle through three points
	 * @param p1 first point 
	 * @param p2 second point 
	 * @param p3 third point
	 * @return circle
	 */
	public static Circle calculateCircleFromThreePoints(Point p1, Point p2, Point p3) {
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
				
		double centerX = (-1 * detB) / (2 * detA);
		double centerY = (-1 * detC) / (2 * detA);
		
		double firstHalf = (Math.pow(detB, 2) + Math.pow(detC, 2)) / (4*Math.pow(detA,2));
		double secondHalf = (detD/detA);
		double radius = Math.sqrt(Math.abs(firstHalf - secondHalf));
		
		Circle circle = new Circle(centerX,centerY,radius);
		
		return circle;
		
	}
	
	/**
	 * Looks if circle to be places is bigger than the OuterCircle
	 * @param currentCircle circle that is about be placed
	 * @param outerCircle circle that is to be covered
	 * @return placed circle
	 */
	public static Circle checkIfCircleIsBiggerThanOuterCircle(Circle currentCircle, Circle outerCircle) {
		if(currentCircle.getRadius() > outerCircle.getRadius()) {
			currentCircle.setCenterX(outerCircle.getCenterX());
			currentCircle.setCenterY(outerCircle.getCenterY());
		}
		
		return currentCircle;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return
	 */
	public static Circle makeDiameter(Point a, Point b) {
		Point c = new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
		return new Circle(c.getX(), c.getY(), Math.max(c.calucateDistanceToPoint(a), c.calucateDistanceToPoint(b)));
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
	/**
	 * Looks if an intersection point is in another circle of the current row
	 * @param point point of intersection
	 * @param allCirclesOfCurrentRow list of all circles of the current row
	 * @return if in another circle -> true, else -> false
	 */
	public static boolean lookIfIntersectionPointInOtherCircle(IntersectionPoint point, ArrayList<Circle> allCirclesOfCurrentRow) {
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
	
	/**
	 * looks if point is in circle
	 * @param circle 
	 * @param p 
	 * @return if point in circle -> true, else -> false 
	 */
	public static boolean lookIfPointInCircle(Circle circle, Point p) {
		if(getCenterPoint(circle).calucateDistanceToPoint(p) < circle.getRadius()) {
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
