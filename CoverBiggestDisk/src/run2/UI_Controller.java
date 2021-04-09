package run2;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import drawing.CircleSpaceCalculation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class UI_Controller{
	
	
	public UI_Controller() {
		
    }
	
	public Group showUI(CircleSpaceCalculation drawingInstance) {
		
		Stage stage = new Stage();
		
		
		drawingInstance.calculateBiggestCircle();
		
		//Group root = new Group(drawingInstance.returnElementsToDraw(true)); 
		
		Group root = new Group();
		
		
		
		Pane pane = new Pane(root);
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		pane.setPrefWidth(width-100);
		pane.setPrefHeight(height-100);
    
		pane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    
		root.setTranslateX(pane.getPrefWidth() / 2);
		root.setTranslateY(pane.getPrefHeight() / 2);
		root.setScaleY(1.5);
		root.setScaleX(1.5);
		
		Scene scene = new Scene(pane);
        //Setting title to the Stage 
        stage.setTitle("Controller"); 
           
        //Adding scene to the stage 
        stage.setScene(scene);
        
        //Displaying the contents of the stage 
        //stage.show(); 
        return root;
	} 
	
	
	
	
}



