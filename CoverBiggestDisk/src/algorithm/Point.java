package algorithm;


public class Point {
	private double x;
	private double y;
	
	public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * calculates the (euclidean) distance to another point p2 
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
	 * @param p
	 * @return
	 */
	public Point subtract(Point p) {
		return new Point(this.x - p.getX(), this.y - p.getY());
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public double cross(Point p) {
		return this.x * p.getY() - this.y * p.getX();	
	}
	
	
	
	
}
