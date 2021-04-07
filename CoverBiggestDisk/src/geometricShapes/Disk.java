package geometricShapes;
import javafx.scene.shape.Circle; 

//Class is not used !!!

public class Disk extends Circle{
	
	private double diameter = this.getRadius()*2.00;
	
	
	public Disk (double diameter){
		this.setDiameter(diameter);
		this.setCenterX(0.0);
		this.setCenterY(0.0);
	}
	
	
	
	public void setCor(double x, double y) {
		this.setCenterX(x);
		this.setCenterY(y);
	}



	/**
	 * @return the diameter
	 */
	public double getDiameter() {
		return diameter;
	}



	/**
	 * @param diameter the diameter to set
	 */
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}
	
		
		
	
}
