package algorithm;


public class Point {
	private double x;
	private double y;
	
	public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

	/**
	 * getter for x
	 * @return x
	 */
	public double getX() {
		return x;
	}

	/**
	 * setter for x
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * getter for y
	 * @return y
	 */
	public double getY() {
		return y;
	}

	/**
	 * setter for y
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * calculates the distance to another point p2
	 * @param p2 other point
	 * @return the distance 
	 */
	public double calucateDistanceToPoint(Point p2) {
			double dx = this.x - p2.getX();
			double dy = this.y - p2.getY();
			double distance = Math.hypot(dx,dy);
			
			return distance;
	}
	
	/**
	 * @param p1
	 * @param p2
	 * @return
	 */
	public Point subtract(Point p) {
		return new Point(this.x - p.getX(), this.y - p.getY());
	}

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public double cross(Point p) {
		return this.x * p.getY() - this.y * p.getX();	
	}
	
	//TODO nesesarry ?
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)) {
			return false;
		}
		Point p2 = (Point) obj;
		
		return (this.x == p2.getX() && this.y == p2.getY());
	}
	
	
}
