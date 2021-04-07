package drawing;


final class Point2 {
	
	public final double x;
	public final double y;
	
	
	public Point2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	
	public Point2 subtract(Point2 p) {
		return new Point2(x - p.x, y - p.y);
	}
	
	
	public double distance(Point2 p) {
		return Math.hypot(x - p.x, y - p.y);
	}
	
	
	// Signed area / determinant thing
	public double cross(Point2 p) {
		return x * p.y - y * p.x;
	}
	
	
	public String toString() {
		return String.format("Point(%g, %g)", x, y);
	}
	
}