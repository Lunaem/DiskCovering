package drawing;

import geometricShapes.Point;
import javafx.scene.shape.Circle;

public class IntersectionPoint {

		public Point pointOfIntersection;
		public Circle circle1;
		public Circle circle2;
		
		
		public IntersectionPoint(Point pointOfIntersection, Circle circle1, Circle circle2) {
			this.pointOfIntersection = pointOfIntersection;
			this.circle1 = circle1;
			this.circle2 = circle2;
		}


		public Point getPointOfIntersection() {
			return pointOfIntersection;
		}


		public void setPointOfIntersection(Point pointOfIntersection) {
			this.pointOfIntersection = pointOfIntersection;
		}


		public Circle getCircle1() {
			return circle1;
		}


		public void setCircle1(Circle circle1) {
			this.circle1 = circle1;
		}


		public Circle getCircle2() {
			return circle2;
		}


		public void setCircle2(Circle circle2) {
			this.circle2 = circle2;
		}
		
		
}
