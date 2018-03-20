import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.sun.javafx.iio.ImageStorage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.util.*;
import javafx.util.*;
public class FunctionImagePane extends VBox{
	private Pane imagePane=new Pane();
	private TextField ftf=new TextField();
	private Button drawBt=new Button("draw");
	private Button clearBt=new Button("clear");
	private double centx=400,centy=400;
	private Line axisX=new Line();
	private Line axisY=new Line();
	private double ratio=30;
	private ArrayList<ArrayList<Token>> funcList=new ArrayList<>();
	private ArrayList<Map<String,Double>> IdMapList=new ArrayList<>();
	private ArrayList<Polyline> imageList=new ArrayList<>();
	private Color[] colors= {
			Color.BLACK,Color.BLUE,Color.GREEN,Color.RED,Color.PINK,Color.PURPLE,Color.YELLOW
	};
	private ScrollBar ratioBar=new ScrollBar(); 
	private CalculatorLogic logic=new CalculatorLogic();

	
	
	public FunctionImagePane() {
		this.setSpacing(20);
		this.setPrefSize(400, 500);
		this.setAlignment(Pos.TOP_CENTER);
		this.setStyle("-fx-background-color: pink");
		initial();
	}
	
	public void initial() {
		axisX.setStartX(0);
		axisX.setStartY(400);
		axisX.setEndX(800);
		axisX.setEndY(400);
		axisY.setStartX(400);
		axisY.setStartY(0);
		axisY.setEndX(400);
		axisY.setEndY(800);
		imagePane.getChildren().addAll(axisX,axisY);
		imagePane.setPrefSize(800, 800);
		imagePane.setStyle("-fx-background-color:white");
		
		ScrollPane pane=new ScrollPane(imagePane);
		pane.setPrefSize(400, 400);
		pane.setHvalue(0.5);
		pane.setVvalue(0.5);
		pane.setPannable(true);
		
		HBox btPane=new HBox(20);
		btPane.setAlignment(Pos.CENTER);
		btPane.setPadding(new Insets(0,20,20,20));
		ftf.setPrefWidth(200);
		Label lb=new Label("y=",ftf);
		lb.setContentDisplay(ContentDisplay.RIGHT);
		btPane.getChildren().addAll(lb,drawBt,clearBt);
		drawBt.setOnAction(e->draw());
		clearBt.setOnAction(e-> clear());
		ftf.setOnAction(e->draw());
		
		ratioBar.setBlockIncrement(5);
		ratioBar.setMin(10);
		ratioBar.setMax(50);
		ratioBar.setValue(30);
		ratioBar.setVisibleAmount(5);
		ratioBar.setPrefHeight(20);
		ratioBar.setMaxWidth(300);
		ratioBar.setUnitIncrement(5);
		ratioBar.setStyle("-fx-background-color: yellow");
		//ratioBar.setStyle("-fx-background-color: transparent");
		ratioBar.valueProperty().addListener(ov->{
			ratio=ratioBar.getValue();
			try {
				for(int i=0;i<imageList.size();i++)
					draw(funcList.get(i),IdMapList.get(i),imageList.get(i));
			}catch(Exception e) {}
		});
		
		this.getChildren().addAll(pane,ratioBar,btPane);
		
	}
	
	public void draw(ArrayList<Token> tokenStream,Map<String,Double> IdMap,Polyline image) throws Exception{
		Set<String> keys=IdMap.keySet();
		if(keys.size()!=1)
			throw new Exception("You must and can only \n input one unknown number!");
		String id="";
		for(String key:keys)
			id=key;
		image.getPoints().clear();
		double ypx,xpx;
		for(xpx=centx-400;xpx<=centx+400;xpx+=1){
			double x=(xpx-centx)/ratio;
			IdMap.put(id, x);
			double y=logic.compute(tokenStream,IdMap);
			ypx=-y*ratio+centy;
			image.getPoints().addAll(xpx,ypx);
		}
	}
	
	public void clear(){
		imagePane.getChildren().removeAll(imageList);
		imageList.clear();
		funcList.clear();
		IdMapList.clear();
	}
	
	public void draw() {
		Alert dialog=new Alert(AlertType.WARNING);
		dialog.setHeaderText(null);
		dialog.setContentText(null);
		try {
			logic.scan(ftf.getText());
			Polyline image=new Polyline();
			draw(logic.getTokenStream(),logic.getIdMap(),image);
			image.setStroke(colors[funcList.size()%(colors.length-1)]);
			image.setStrokeWidth(3);
			imagePane.getChildren().add(image);
			
			funcList.add(logic.getTokenStream());
			IdMapList.add(logic.getIdMap());
			imageList.add(image);
		}catch(Exception ex) {
			dialog.setHeaderText(ex.getMessage());
			dialog.showAndWait();
		}
	}
}
