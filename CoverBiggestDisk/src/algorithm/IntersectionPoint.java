package algorithm;

import javafx.scene.shape.Circle;

public class IntersectionPoint {
	public Point pointOfIntersection;
	public Circle circle1;
	public Circle circle2;
	
	
	/**
	 * Constructor
	 * @param pointOfIntersection point of intersection
	 * @param circle1 first circle of intersection point 
	 * @param circle2 second circle of intersection point 
	 */
	public IntersectionPoint(Point pointOfIntersection, Circle circle1, Circle circle2) {
		this.pointOfIntersection = pointOfIntersection;
		this.circle1 = circle1;
		this.circle2 = circle2;
	}


	/**
	 * getter for the point of intersection
	 * @return point of intersection
	 */
	public Point getPointOfIntersection() {
		return pointOfIntersection;
	}


	/**
	 * setter for the point of intersection
	 * @param pointOfIntersection point of intersection
	 */
	public void setPointOfIntersection(Point pointOfIntersection) {
		this.pointOfIntersection = pointOfIntersection;
	}

	/**
	 * getter for the first circle
	 * @return first circle
	 */
	public Circle getCircle1() {
		return circle1;
	}

	/**
	 * setter for the first circle
	 * @param circle1 first circle
	 */
	public void setCircle1(Circle circle1) {
		this.circle1 = circle1;
	}


	/**
	 * getter for the second circle
	 * @return second circle
	 */
	public Circle getCircle2() {
		return circle2;
	}


	/**
	 * setter for the second circle
	 * @param circle1 second circle
	 */
	public void setCircle2(Circle circle2) {
		this.circle2 = circle2;
	}
}
