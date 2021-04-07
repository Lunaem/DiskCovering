package drawing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import geometricShapes.Point;
import javafx.scene.shape.Circle;
import run.Data;

public class CircleSpaceCalculationTest{
	

	private Data data = new Data(System.getProperty("user.dir") + "/src/resources/data.csv");
	private CircleSpaceCalculation csc;
	

    @BeforeEach                                         
    public void setUp(){
    	//CircleSpaceCalculationTest(data);
    	//Data data = new Data("resources/data.csv");
    	csc = new CircleSpaceCalculation(data);
    }

	@Disabled
	void testCircleSpaceCalculation() {
	}

	@Disabled
	void testLoadData() {
	}

	@Disabled
	void testCalculateBiggestCircle() {
	}

	@Disabled
	void testPlaceCircleOverAlignmentCircle() {
	}

	@Disabled
	void testFindVeryFirstIntersectionPoint() {
	}

	@Disabled
	void testLookifCircleIsCovered() {
	}

	@Disabled
	void testPlaceNextCircle() {
	}

	@Disabled
	void testReturnElementsToDraw() {
	}

	@Disabled
	void testDrawCircle() {
	}

	@Disabled
	void testCreateOuterAlignementCircle() {
	}

	@Disabled
	void testFindIntersectionPoints() {
	}

	@Disabled
	void testFindInnerIntersectionPoints() {
	}

	@Disabled
	void testLookIfIntersectionPointInOtherCircle() {
		Circle circle0 = new Circle(-50,0,100);
		Circle circle1 = new Circle(0,-50,100);
		Circle circle2 = new Circle(0,50,100);

		ArrayList<Circle> listOfCircles = new ArrayList<Circle>();
		listOfCircles.add(circle0);
		
		ArrayList<Point> listOfIntersectionPoints = csc.findIntersectionPoints(circle1, circle2);
		
		IntersectionPoint ip1 = new IntersectionPoint(listOfIntersectionPoints.get(0), circle1, circle2);
		IntersectionPoint ip2 = new IntersectionPoint(listOfIntersectionPoints.get(1), circle1, circle2);
		
		assertTrue(csc.lookIfIntersectionPointInOtherCircle(ip1, listOfCircles));
		assertFalse(csc.lookIfIntersectionPointInOtherCircle(ip2, listOfCircles));
	}

	@Test
	void testLookIfAlignmentCircleIsCovered() {
		Circle circle0 = new Circle(0,0,100);
		Circle circle1 = new Circle(50,0,100);
		Circle circle2 = new Circle(0,50,100);
		Circle circle3 = new Circle(0,-50,100);
		Circle circle4 = new Circle(-50,0,100);
		
		ArrayList<Circle> listOfCirclesCovered = new ArrayList<Circle>();
		listOfCirclesCovered.add(circle1);
		listOfCirclesCovered.add(circle2);
		listOfCirclesCovered.add(circle3);
		listOfCirclesCovered.add(circle4);
		
		ArrayList<Circle> listOfCirclesNotCovered = new ArrayList<Circle>();
		listOfCirclesNotCovered.add(circle1);
		listOfCirclesNotCovered.add(circle2);
		
		
		csc.lookIfAlignmentCircleIsCovered(listOfCirclesCovered, circle0);
		assertTrue(csc.getFinished());
		csc.lookIfAlignmentCircleIsCovered(listOfCirclesNotCovered, circle0);
		assertFalse(csc.getFinished());
	}

	@Test
	void testLookIfPointInCircle() {
		Circle circle = new Circle(0,0,100);
    	Point pointInCircle = new Point(1,1);
    	Point pointNotInCircle = new Point(200,200);
    	
    	assertTrue(csc.lookIfPointInCircle(circle, pointInCircle));
    	assertFalse(csc.lookIfPointInCircle(circle, pointNotInCircle));
	}

	@Test
	void testGetDiameter() {
		Circle circle = new Circle(0,0,1);
		assertEquals(2, csc.getDiameter(circle));
	}

	@Test
	void testGetSpace() {
		Circle circle = new Circle(0,0,1);
		assertEquals(Math.PI, csc.getSpace(circle));
	}

	

}
