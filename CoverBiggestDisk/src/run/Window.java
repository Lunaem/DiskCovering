package run;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import drawing.CircleSpaceCalculation;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Window extends Application {
	
	private int clicked = 0;
	private int clicked2 = 1;
	 
	private ArrayList<Circle> listOfPlacedCircles = new ArrayList<Circle>();
	private ArrayList<Circle> listOfAlignementCircles = new ArrayList<Circle>();	
	
	private Group root = new Group();
	private Data data = new Data();
	private Stage sec = new Stage();
	 
	private Button startButton = new Button("Start");
	private TextField stopIteration = new TextField();   
	private TextField tolerance = new TextField();   
	private Button nextButton = new Button("Next");
	private Button nextAlignButton = new Button("Next align");
	private Button againButton = new Button("Again");
	private CheckBox ccw = new CheckBox("CCW on/off"); 
	private CheckBox poh = new CheckBox("Place over hole on/off"); 
	private CheckBox intersectionPoint = new CheckBox("Show intersection points"); 
	private Label sizeOfDisk = new Label("Size of disk: ");
	private Label radiusOfDisk = new Label("Radius of disk: ");
	private Button loadButton = new Button("Load");
		
	@Override
    public void start(Stage primaryStage) throws Exception {
		
    	stopIteration.setPromptText("enter stop iteration");
		tolerance.setPromptText("enter tolerance");
		 
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(400, 200); 
        
        //Setting the padding  
        gridPane.setPadding(new Insets(10, 10, 10, 10)); 
        
        //Setting the vertical and horizontal gaps between the columns 
        gridPane.setVgap(5); 
        gridPane.setHgap(5);       
        
        //Setting the Grid alignment 
        gridPane.setAlignment(Pos.CENTER); 
 
        ccw.setIndeterminate(false); 
        intersectionPoint.setIndeterminate(false); 
        
        gridPane.add(startButton, 0, 0); 
        gridPane.add(stopIteration, 0, 1); 
        gridPane.add(tolerance, 0, 2);       
        gridPane.add(nextButton, 0, 3); 
        gridPane.add(nextAlignButton, 0, 4); 
        gridPane.add(againButton, 0, 5); 
        gridPane.add(loadButton, 0, 6); 
        gridPane.add(ccw, 1, 0); 
        gridPane.add(poh, 1, 1); 
        gridPane.add(intersectionPoint, 1, 2); 
        gridPane.add(sizeOfDisk, 1, 3);
        gridPane.add(radiusOfDisk, 1, 4);
 
        
        //data.setFileLocation("C:/Users/Lunaem/Desktop/data.csv");
        
       
        Pane pane = new Pane(root);
        Scene scene2 = new Scene(pane); 
        
        
        
        startButton.setOnAction(startButtonEvent);
        nextButton.setOnAction(nextButtonEvent);
        
        nextAlignButton.setOnAction(nextAlignButtonEvent);
        
       loadButton.setOnAction(loadButtonEvent);
       
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		pane.setPrefWidth(width-100);
		pane.setPrefHeight(height-100);
    
		pane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    
		
		Parent zoomPane = createZoomPane(root);

	    VBox layout = new VBox();
	    layout.getChildren().setAll(zoomPane);

	    VBox.setVgrow(zoomPane, Priority.ALWAYS);
	    Scene scene3 = new Scene(layout);
	    
        Scene scene = new Scene(gridPane); 
        sec.setScene(scene3);
		primaryStage.setTitle("CoverBiggestDisk"); 
		primaryStage.setScene(scene); 
		primaryStage.show(); 
	
    }
	
	 private Parent createZoomPane(final Group group) {
		    final double SCALE_DELTA = 1.1;
		    final StackPane zoomPane = new StackPane();

		    zoomPane.getChildren().add(group);
		    StackPane.setAlignment(group, Pos.CENTER);

		    final ScrollPane scroller = new ScrollPane();
		    final Group scrollContent = new Group(zoomPane);
		    scroller.setContent(scrollContent);

		    scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
		      @Override
		      public void changed(ObservableValue<? extends Bounds> observable,
		          Bounds oldValue, Bounds newValue) {
		        zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
		      }
		    });

		    scroller.setPrefViewportWidth(256);
		    scroller.setPrefViewportHeight(256);

		    zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
		      @Override
		      public void handle(ScrollEvent event) {
		        event.consume();

		        if (event.getDeltaY() == 0) {
		          return;
		        }

		        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
		            : 1 / SCALE_DELTA;

		        // amount of scrolling in each direction in scrollContent coordinate
		        // units
		        Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

		        group.setScaleX(group.getScaleX() * scaleFactor);
		        group.setScaleY(group.getScaleY() * scaleFactor);

		        // move viewport so that old center remains in the center after the
		        // scaling
		        repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

		      }
		    });

		    // Panning via drag....
		    final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
		    scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
		      @Override
		      public void handle(MouseEvent event) {
		        lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
		      }
		    });

		    scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
		      @Override
		      public void handle(MouseEvent event) {
		        double deltaX = event.getX() - lastMouseCoordinates.get().getX();
		        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
		        double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
		        double desiredH = scroller.getHvalue() - deltaH;
		        scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

		        double deltaY = event.getY() - lastMouseCoordinates.get().getY();
		        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
		        double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
		        double desiredV = scroller.getVvalue() - deltaV;
		        scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
		      }
		    });

		    return scroller;
	}
	 
	 private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
		    double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
		    double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
		    double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
		    double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
		    double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
		    double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
		    return new Point2D(scrollXOffset, scrollYOffset);
	 }
	 
	 private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
		    double scrollXOffset = scrollOffset.getX();
		    double scrollYOffset = scrollOffset.getY();
		    double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
		    if (extraWidth > 0) {
		      double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
		      double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
		      scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
		    } else {
		      scroller.setHvalue(scroller.getHmin());
		    }
		    double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
		    if (extraHeight > 0) {
		      double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
		      double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
		      scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
		    } else {
		      scroller.setHvalue(scroller.getHmin());
		    }
	}   
	 EventHandler<ActionEvent> nextButtonEvent = new EventHandler<ActionEvent>() {
    	 @Override
         public void handle(ActionEvent event) {
         	int i = clicked;
         	if(i > listOfPlacedCircles.size()-1) {
         		clicked = 0;
         		i = clicked;	
         	}
         	root.getChildren().add(listOfPlacedCircles.get(i));
         	clicked ++;	
         }
      };
      
      EventHandler<ActionEvent> nextAlignButtonEvent = new EventHandler<ActionEvent>() {
    	  @Override
          public void handle(ActionEvent event) {
          	int i = clicked2;
          	root.getChildren().add(listOfAlignementCircles.get(i));
          	clicked2 ++;	
          }
      };	
      EventHandler<ActionEvent> loadButtonEvent = new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent event) {
	      	 FileDialog dialog = new FileDialog((Frame) null, "Select a *.csv file to open", FileDialog.LOAD);
	      	 dialog.setFile("*.csv");
	      	 dialog.setVisible(true);
	      	 
	      	 //String fileLocation = "C:/Users/Lunaem/Desktop/data.csv";
	      	 String fileLocation = dialog.getDirectory() + dialog.getFile();
	      	 data.setFileLocation(fileLocation);
	      	 System.out.println( fileLocation + " chosen.");
	      }
      };
      EventHandler<ActionEvent> startButtonEvent = new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent event) {
	      	CircleSpaceCalculation drawingInstance = new CircleSpaceCalculation(data); 
	      	
	      	if (ccw.isSelected()) {
	              drawingInstance.setCCW(true);
	           }
	      	if (poh.isSelected()) {
	              drawingInstance.setPOH(true);
	           }
	      	if(stopIteration.getText() != "") {
	      		drawingInstance.setStopIteration(Integer.valueOf(stopIteration.getText()));
	      	}
	      	if(tolerance.getText() != "") {
	      		//double value = Double.parseDouble();
	      		drawingInstance.setTolerance(Double.parseDouble(tolerance.getText()));
	      		
	      	}
	      	
	      	drawingInstance.calculateBiggestCircle();
	      	if (intersectionPoint.isSelected()) {
	              root.getChildren().addAll(drawingInstance.getListOfABC());
	           }
	      	//root = UIInstance.showUI(drawingInstance);
	      	
	      	
	      	listOfAlignementCircles = drawingInstance.getListOfAlignmentCircles();
	      	listOfPlacedCircles = drawingInstance.getListOfPlacedCircles();
	      	root.getChildren().add(listOfAlignementCircles.get(0));
	      	sizeOfDisk.setText("Size of disk: " + String.valueOf(drawingInstance.getSpace(listOfAlignementCircles.get(0))));
	      	radiusOfDisk.setText("Radius of disk: " + String.valueOf(drawingInstance.getRadiusOfDisk()));
				
	      	sec.show();
	      }
      };
}
