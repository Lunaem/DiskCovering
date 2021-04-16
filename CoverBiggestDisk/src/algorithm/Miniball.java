package algorithm;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.scene.shape.Circle;

//Source: https://www.nayuki.io/page/smallest-enclosing-circle

public class Miniball {
		/* 
		 * 
		 * Returns the smallest circle that encloses all the given points. Runs in expected O(n) time, randomized.
		 * Note: If 0 points are given, null is returned. If 1 point is given, a circle of radius 0 is returned.
		 */
	
		// Initially: No boundary points known
		public static Circle makeCircle(List<Point> points) {
			// Clone list to preserve the caller's data, randomize order
			List<Point> shuffled = new ArrayList<>(points);
			Collections.shuffle(shuffled, new Random());
			
			// Progressively add points to circle or recompute circle
			Circle c = null;
			for (int i = 0; i < shuffled.size(); i++) {
				Point p = shuffled.get(i);
				if (c == null || !CC_Functions.lookIfPointInCircle(c, p))
					c = makeCircleOnePoint(shuffled.subList(0, i + 1), p);
			}
			return c;
		}
		
		
		// One boundary point known
		private static Circle makeCircleOnePoint(List<Point> points, Point p) {
			Circle c = new Circle(p.getX(), p.getY(), 0);
			for (int i = 0; i < points.size(); i++) {
				Point q = points.get(i);
				if (!CC_Functions.lookIfPointInCircle(c, q)) {
					if (c.getRadius() == 0)
						c = makeDiameter(p, q);
					else
						c = makeCircleTwoPoints(points.subList(0, i + 1), p, q);
				}
			}
			return c;
		}
		
		
		// Two boundary points known
		private static Circle makeCircleTwoPoints(List<Point> points, Point p, Point q) {
			Circle circ = makeDiameter(p, q);
			Circle left  = null;
			Circle right = null;
			
			// For each point not in the two-point circle
			Point pq = subtract(q,p);
			for (Point r : points) {
				if (CC_Functions.lookIfPointInCircle(circ, r))
					continue;
				
				// Form a circumcircle and classify it on left or right side
				double cross = cross(pq,(subtract(r,p)));
				Circle c = CC_Functions.calculateCircleFromThreePoints(p, q, r);
				if (c == null)
					continue;
				else if (cross > 0 && (left == null || cross(pq,(subtract(CC_Functions.getCenterPoint(c),p))) > cross(pq,(subtract((CC_Functions.getCenterPoint(left)),p)))))
					left = c;
				else if (cross < 0 && (right == null || cross(pq,(subtract(CC_Functions.getCenterPoint(c),p))) < cross(pq,(subtract((CC_Functions.getCenterPoint(right)),p)))))
					right = c;
			}
			
			// Select which circle to return
			if (left == null && right == null)
				return circ;
			else if (left == null)
				return right;
			else if (right == null)
				return left;
			else
				return left.getRadius() <= right.getRadius() ? left : right;
		}
		
		// Three boundary points known
		private static Circle makeCircumcircle(Point a, Point b, Point c) {
			// Mathematical algorithm from Wikipedia: Circumscribed circle
			double ox = (Math.min(Math.min(a.getX(), b.getX()), c.getX()) + Math.max(Math.max(a.getX(), b.getX()), c.getX())) / 2;
			double oy = (Math.min(Math.min(a.getY(), b.getY()), c.getY()) + Math.max(Math.max(a.getY(), b.getY()), c.getY())) / 2;
			double ax = a.getX() - ox,  ay = a.getY() - oy;
			double bx = b.getX() - ox,  by = b.getY() - oy;
			double cx = c.getX() - ox,  cy = c.getY() - oy;
			double d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
			if (d == 0)
				return null;
			double x = ((ax*ax + ay*ay) * (by - cy) + (bx*bx + by*by) * (cy - ay) + (cx*cx + cy*cy) * (ay - by)) / d;
			double y = ((ax*ax + ay*ay) * (cx - bx) + (bx*bx + by*by) * (ax - cx) + (cx*cx + cy*cy) * (bx - ax)) / d;
			Point p = new Point(ox + x, oy + y);
			double r = Math.max(Math.max(p.calucateDistanceToPoint(a), p.calucateDistanceToPoint(b)), p.calucateDistanceToPoint(c));
			return new Circle(p.getX(),p.getY(), r);
		}
		
		
		private static Point subtract(Point p1, Point p2) {
			return new Point(p1.getX()- p2.getX(), p1.getY() - p2.getY());
		}

		// Signed area / determinant thing
		private static double cross(Point p1, Point p2) {
			return (p1.getX() * p2.getY()) - (p1.getY() * p2.getX());	
		}
		
		
		public static Circle makeDiameter(Point a, Point b) {
			Point c = new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
			double amax = c.calucateDistanceToPoint(a);
			double bmax = c.calucateDistanceToPoint(b);
			Circle circ = new Circle(c.getX(), c.getY(), Math.max(amax, bmax));
			return circ;
		}
	
	


}
