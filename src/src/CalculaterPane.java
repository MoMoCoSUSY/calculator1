import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
public class CalculaterPane extends VBox{
	
	private ExechangeRatePane ER=new ExechangeRatePane();

	
	private CalculatorLogic logic=new CalculatorLogic();
	private String currResult="";
	private HBox buttonPane=new HBox(20);
	private VBox screenPane=new VBox(5);
	private TextArea sta=new TextArea();
	private TextField stf=new TextField();
	private Formula[] formulaLib;
	private ComboBox<String> fmlcb=new ComboBox<>();
	private Button[] funcButtons= {
			new Button("("),new Button(")"),
			new Button("AC"),new Button("DEL"),
			new Button("+"),new Button("-"),
			new Button("*"),new Button("/"),
			new Button("sin"),new Button("cos"),
			new Button("tan"),new Button("sqrt"),
			new Button("log"),new Button("ln"),
			new Button("exp"),new Button("pow"),
			new Button("rand"),new Button("rInt") ,
			new Button("E"),new Button("PI")	
	};
	
	private Button[] digitButtons= {
			new Button("1"),new Button("2"),new Button("3"),
			new Button("4"),new Button("5"),new Button("6"),
			new Button("7"),new Button("8"),new Button("9"),
			new Button("0"),new Button("."),new Button("%")
	};
	private Button equalBt=new Button("=");
	private ComboBox<String> resultPattern=new ComboBox<>();
	
	private AddToScreen add=new AddToScreen();
	
	public CalculaterPane() {
		this.setPrefSize(400,500);
		this.setSpacing(20);
		this.setAlignment(Pos.TOP_CENTER);
		this.getChildren().addAll(screenPane,fmlcb,buttonPane,ER);
		this.setStyle("-fx-background-color: pink");
		initial();
	}
	
	public void initial() {
		setScreenPane();
		setFmlcb();
		setFuncButtonPane();
		setDigitButtonPane();
		buttonPane.setAlignment(Pos.CENTER);
	}
	
	private void setScreenPane(){
		sta.setPrefHeight(80);
		sta.setWrapText(true);
		stf.setEditable(false);
		stf.setAlignment(Pos.CENTER_RIGHT);
		sta.setOnKeyPressed(e->{
			if(e.getCode()==KeyCode.ENTER)
				compute();
		});
		screenPane.getChildren().addAll(sta,stf);
	}
	public void setFmlcb() {
		fmlcb.setPrefSize(360, 25);
		fmlcb.setOnAction(e->{
			String tmp=fmlcb.getValue();
			if(tmp!=null) {
				int index=fmlcb.getItems().indexOf(tmp);
				sta.appendText(formulaLib[index].content);
				formulaLib[index].count++;
			}
		});
	}
	
	public void updateFmlcbList(Formula[] flib){
		this.formulaLib=flib;
		String[] fmlList=new String[flib.length];
		for(int i=0;i<flib.length;i++)
			fmlList[i]=flib[i].id+"  :  "+flib[i].content;
		fmlcb.setItems(null);
		fmlcb.setItems((FXCollections.observableArrayList(fmlList)));
	}
	private void setFuncButtonPane() {
		GridPane fgp=new GridPane();
		fgp.setPadding(new Insets(10,10,10,10));
		fgp.setHgap(10);
		fgp.setVgap(20);
		for(int i=0;i<5;i++)
			for(int j=0;j<4;j++) {
				Button bt=funcButtons[i*4+j];
				bt.setPrefHeight(20);
				bt.setPrefWidth(45);
				bt.setAlignment(Pos.CENTER);;
				fgp.add(bt, j, i);
				if(bt==funcButtons[2])
					bt.setOnAction(e->{
							sta.setText("");stf.setText("");
						});
				else if(bt==funcButtons[3])
					bt.setOnAction(e->{
						String his=sta.getText();
						if(his.length()!=0)
							sta.setText(his.substring(0,his.length()-1));
					});
				else 
					bt.setOnAction(add);
			}
		buttonPane.getChildren().add(fgp);
		GridPane.setHalignment(fgp, HPos.CENTER);
		GridPane.setValignment(fgp, VPos.CENTER);
	}
	
	
	private void setDigitButtonPane(){
		GridPane dgp=new GridPane();
		dgp.setVgap(11);
		dgp.setHgap(10);
		for(int i=0;i<4;i++)
			for(int j=0;j<3;j++) {
				Button bt=digitButtons[i*3+j];
				bt.setPrefHeight(30);
				bt.setPrefWidth(30);
				dgp.add(bt,j, i);
				bt.setOnAction(add);
			}
		
		HBox resultBox=new HBox(10);
		equalBt.setPrefHeight(30);
		equalBt.setPrefWidth(30);
		equalBt.setOnAction(e-> compute());
		resultPattern.setPrefHeight(30);
		resultPattern.setPrefWidth(70);
		String[] patterns={"Dec","Sci", "Bin","Oct","Hex"};
		resultPattern.setValue("Dec");
		resultPattern.setItems(FXCollections.observableArrayList(patterns));
		resultPattern.setOnAction(e->
			showResult());
		resultBox.getChildren().addAll(resultPattern,equalBt);
		
		VBox digitPane=new VBox(11);
		digitPane.getChildren().addAll(dgp,resultBox);
		digitPane.setPadding(new Insets(10,10,10,10));
		buttonPane.getChildren().add(digitPane);
	}
	
	private class AddToScreen implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			if (e.getSource() instanceof Button){
				Button b=(Button)(e.getSource());
				sta.appendText(b.getText());
			}
		}
	}
	
	public void compute(){
		try {
			logic.scan(sta.getText());
			Map<String,Double> IdMap=logic.getIdMap();
			Set<String> Ids=IdMap.keySet(); 
			for(String id:Ids){
				TextInputDialog dialog=new TextInputDialog();
				dialog.setHeaderText(null);
				dialog.setGraphic(null);
				dialog.setContentText(id+"=");
				Optional<String> result=dialog.showAndWait();
				if(result.isPresent()){
					try {
						IdMap.put(id, Double.parseDouble(result.get()));
						dialog.setResult("");
					}catch(RuntimeException ex) {
						throw new Exception("You should input a number for "+id);
					}
				}
				else 
					throw new Exception("Empty input!");
			}
			currResult=logic.compute()+"";
			showResult();
		}catch(Exception ex){
			currResult="";
			stf.setText(ex.getMessage());
		}
	}
	
	private void showResult() {
		int ind=resultPattern.getItems().indexOf(resultPattern.getValue());
		if(!currResult.equals("")) {
			switch(ind){
				case 0:	stf.setText(currResult);break;
				case 1: stf.setText(ResultTransformer.toScientific(currResult));break;
				case 2: stf.setText(ResultTransformer.radixTransform(currResult,2));break;
				case 3: stf.setText(ResultTransformer.radixTransform(currResult,8));break;
				case 4: stf.setText(ResultTransformer.radixTransform(currResult,16));break;
			}
		}
	}
}
