package drawing;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import geometricShapes.Point;

public class Miniball {
	final static double threshold = 1e-6;
	
	
	/*
	public static Circle minidisk(ArrayList<Point> points, ArrayList<Point> borderPoints) {
		Circle d = new Circle();
		if(points.isEmpty() || borderPoints.size() == 3) {
			d = computeCircle(borderPoints);
		}
	    else {
	    	int randomNum = (int) Math.round(Math.random()*(points.size() -1));
			Point selectedPoint = points.get(randomNum);
			
			ArrayList<Point> remainingPoints = (ArrayList<Point>) points.clone();
			remainingPoints.remove(selectedPoint);
			
			d = minidisk(remainingPoints, borderPoints);

		        if(!checkIfCircleContainsPoint(d,selectedPoint)) {
					ArrayList<Point> newBorderPoints = (ArrayList<Point>) borderPoints.clone();
					newBorderPoints.add(selectedPoint);
					d = minidisk(remainingPoints, newBorderPoints);
				}
	    }
	    return d;
	}
	*/
	public static Circle minidisk(ArrayList<Point> points) {
		Circle d;
		if(points.isEmpty()) {
			d = new Circle();
		}
		else {
			int randomNum = (int) Math.round(Math.random()*(points.size() -1));
			Point selectedPoint = points.get(randomNum);
			
			ArrayList<Point> remainingPoints = (ArrayList<Point>) points.clone();
			remainingPoints.remove(selectedPoint);
			d = minidisk(remainingPoints);
			
			if(!checkIfCircleContainsPoint(d, selectedPoint)) {
				ArrayList<Point> borderPoints = new ArrayList<Point>();
				borderPoints.add(selectedPoint);
				d = b_minidisk(remainingPoints, borderPoints);
			}
		}
		return d;
	}
	
	private static Circle b_minidisk(ArrayList<Point> points, ArrayList<Point> borderPoints) {
		Circle d;
		if(points.isEmpty() || borderPoints.size() == 3) {
			d = computeCircle(borderPoints);
		}
		else {
			int randomNum = (int) Math.round(Math.random()*(points.size() -1));
			Point selectedPoint = points.get(randomNum);
			
			ArrayList<Point> remainingPoints = (ArrayList<Point>) points.clone();
			remainingPoints.remove(selectedPoint);
			
			ArrayList<Point> borderPoints2 = (ArrayList<Point>) borderPoints.clone();
			d = b_minidisk(remainingPoints, borderPoints2);
			
			if(!checkIfCircleContainsPoint(d,selectedPoint) && d != null) {
				
				ArrayList<Point> newBorderPoints = (ArrayList<Point>) borderPoints.clone();
				newBorderPoints.add(selectedPoint);
				d = b_minidisk(remainingPoints, newBorderPoints);
			}
		}
		return d;
	}
	
	private static Circle computeCircle(ArrayList<Point> borderPoints) {
		Circle c = new Circle();
	
		 switch(borderPoints.size()){
	        case 0:
	            System.out.println("BorderPoint List is empty");
	            break;
	        case 1:
	        	Point p = borderPoints.get(0);
	        	c = new Circle (p.getX(),p.getY(),0);
	        	break;
	        	
	        case 2:
	        	c = makeCircleWithTwoBorderPoints(borderPoints.get(0), borderPoints.get(1));
	        	break;
	        	
	        case 3:
	        	c = makeCircleWithThreeBorderPoints(borderPoints);
	        	break;
		 }
		 return c;
	}
	
	//Math after https://de.wikibooks.org/wiki/Analytische_Geometrie/_Matrizen/_Rechnen_mit_Matrizen/_Determinante_einer_Matrix
	private static Circle makeCircleWithThreeBorderPoints(ArrayList<Point> points) {
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
		
		double detA = findDeterminat(matA);
		double detB = -1 * findDeterminat(matB);
		double detC = findDeterminat(matC);
		double detD = -1 * findDeterminat(matD);
				
		double centerX = (-1 * detB) / (2 * detA);
		double centerY = (-1 * detC) / (2 * detA);
		
		double firstHalf = (Math.pow(detB, 2) + Math.pow(detC, 2)) / (4*Math.pow(detA,2));
		double secondHalf = (detD/detA);
		double radius = Math.sqrt(Math.abs(firstHalf - secondHalf));
		
		Circle circle = new Circle(centerX,centerY,radius);
		
		return circle;
	}


	private static double findDeterminat(double[][] mat) {
		double determinat = mat[0][0] * mat[1][1] * mat[2][2] +
					 	  mat[0][1] * mat[1][2] * mat[2][0] +
					 	  mat[0][2] * mat[1][0] * mat[2][1] -
					 	  mat[0][2] * mat[1][1] * mat[2][0] -
					 	  mat[1][2] * mat[2][1] * mat[0][0] -
					 	  mat[2][2] * mat[0][1] * mat[1][0];
		
		return determinat;
	}


	private static Circle makeCircleWithTwoBorderPoints(Point a, Point b) {
		Point center = new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
		
		Circle circle = new Circle(center.getX(), center.getY(), center.calucateDistanceToPoint(a));
		
		return circle;
	}

	
	
	public static boolean checkIfCircleContainsPoint(Circle c, Point p) {
		if(c == null) {
			return false;
		}
		if(getCenterPoint(c).calucateDistanceToPoint(p) <= c.getRadius() * threshold) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static Point subtract(Point p1, Point p2) {
		return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
	public static Point getCenterPoint(Circle c) {
		Point p = new Point(c.getCenterX(), c.getCenterY());
		return p;
	}
	public static double cross(Point p1, Point p2) {
		return p1.getX() * p2.getY() - p1.getY() * p2.getX();
		
		
	}


	public static boolean checkIfPointInCircle(Circle c, Point p) {
		if(c == null) {
			return false;
		}
		Point center = new Point (c.getCenterX(), c.getCenterY());
		if(c.getRadius() <= p.calucateDistanceToPoint(center)) {
			return true;
		}
		return false;
	}

}


