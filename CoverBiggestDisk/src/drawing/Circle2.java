package drawing;

import java.util.Collection;

final class Circle2 {
	
	private static final double MULTIPLICATIVE_EPSILON = 1 + 1e-14;
	
	
	public final Point2 c;   // Center
	public final double r;  // Radius
	
	
	public Circle2(Point2 c, double r) {
		this.c = c;
		this.r = r;
	}
	
	
	public boolean contains(Point2 p) {
		return c.distance(p) <= r * MULTIPLICATIVE_EPSILON;
	}
	
	
	public boolean contains(Collection<Point2> ps) {
		for (Point2 p : ps) {
			if (!contains(p))
				return false;
		}
		return true;
	}
	
	
	public String toString() {
		return String.format("Circle(x=%g, y=%g, r=%g)", c.x, c.y, r);
	}
	
	
}
