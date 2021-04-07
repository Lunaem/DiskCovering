package geometricShapes;

public class Point {

	private double x;
	private double y;
	
	public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)) {
			return false;
		}
		Point p2 = (Point) obj;
		
		return (this.x == p2.getX() && this.y == p2.getY());
	}
	//TODO move to class point
		public double calucateDistanceToPoint(Point p2) {
			double dx = x - p2.getX();
			double dy = y - p2.getY();
			double distance = Math.hypot(dx,dy);
			return distance;
		}
		
}
