import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.util.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;
import javafx.event.*;

public class FormulaEditor extends VBox{
	private Formula[] formulaLib;
	private ObservableList<FormulaCheckBox> FCBList=FXCollections.observableArrayList();
	private String dataPath="D:/123.dat";
	private String currSortMode;
	private String currADMode;
	private ListView<FormulaCheckBox> listShow=new ListView<>();
	private Button selectBt=new Button("All");
	private Button deleteBt=new Button("Del");
	private Button addBt=new Button("Add");
	private Button saveBt=new Button("save");
	private ComboBox<String> sortMode=new ComboBox<>();
	private ComboBox<String> adMode=new ComboBox<>();
	private FormulaDialog fdialog=new FormulaDialog();
	private boolean allSelected=false;
	
	public FormulaEditor(){
		this.setPrefSize(400, 500);
		this.setStyle("-fx-background-color:pink");
		this.setAlignment(Pos.TOP_CENTER);
		this.setSpacing(10);
		initial();	
	}
	
	private void initial() {
		load();
		setFuncButton();
		setListShowArea();
		setSaveBt();
	}
	
	private void setFuncButton() {
		double w=50,h=20;
		selectBt.setPrefSize(w, h);
		selectBt.setOnAction(new SelectAll());
		deleteBt.setPrefSize(w, h);
		deleteBt.setOnAction(new DelFormula());
		addBt.setPrefSize(w,h);
		addBt.setOnAction(new AddFormula());
		sortMode.setPrefSize(w+25, h); 
		sortMode.setItems(FXCollections.observableArrayList(
				new String[]{"name","count","level"}));
		sortMode.setValue(currSortMode);
		sortMode.setOnAction(new SetSortMode());
		adMode.setPrefSize(w+25, h);
		adMode.setItems(FXCollections.observableArrayList(
				new String[] {"ASC","DESC"}));
		adMode.setValue(currADMode);
		adMode.setOnAction(new SetADMode());
		//add btEvent.......
		
		HBox box1=new HBox(10);
		box1.getChildren().addAll(selectBt,deleteBt,addBt,sortMode,adMode);
		HBox box2=new HBox(10);
		box2.getChildren().addAll(sortMode,adMode);
		BorderPane box=new BorderPane();
		box.setPadding(new Insets(0,10,10,10));
		box.setLeft(box1);
		BorderPane.setAlignment(box1, Pos.CENTER_LEFT);
		box.setRight(box2);
		BorderPane.setAlignment(box2, Pos.CENTER_RIGHT);
		this.getChildren().add(box);
	}

	private void setListShowArea(){
		listShow.setItems(FCBList);
		listShow.setPrefSize(200, 350);
		this.getChildren().add(listShow);
	}
	
	private void setSaveBt() {
		saveBt.setPrefSize(50, 30);
		saveBt.setOnAction(e-> save());
		BorderPane pane=new BorderPane();
		pane.setRight(saveBt);
		pane.setPadding(new Insets(10,50,10,0));
		this.setAlignment(Pos.CENTER_RIGHT);
		this.getChildren().add(pane);
	}
	
	private void load() {
		try {
			ObjectInputStream in=new ObjectInputStream(
				new FileInputStream(new File(dataPath)));
			currSortMode=in.readUTF();
			currADMode=in.readUTF();
			formulaLib=(Formula[])(in.readObject());
			FormulaCheckBox[] arr=new FormulaCheckBox[formulaLib.length];
			for(int i=0;i<formulaLib.length;i++)
				arr[i]=new FormulaCheckBox(formulaLib[i]);
			FCBList=FXCollections.observableArrayList(arr);
			in.close();
		}catch(FileNotFoundException e){
			currSortMode="name";
			currADMode="ASC";
			this.save();
		}catch(IOException | ClassNotFoundException e){
		}
	}
	
	public void save(){
		try {
			ObjectOutputStream out=new ObjectOutputStream(
					new FileOutputStream(new File(dataPath)));
			out.writeUTF(currSortMode);
			out.writeUTF(currADMode);
			formulaLib=new Formula[FCBList.size()];
			for(int i=0;i<formulaLib.length;i++)
				formulaLib[i]=FCBList.get(i).getFormula();
			out.writeObject(formulaLib);
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Formula[] getFormulaLib() {
		return formulaLib;
	}	
	
	private class FormulaCheckBox extends CheckBox {
		private Formula fml;
		Text baseInfo=new Text();
		Text addInfo=new Text();
		private HBox fbar=new HBox();
		public FormulaCheckBox(Formula f) {
			this.fml=f;
			setPrefHeight(20);
			setPadding(new Insets(5,5,5,5));
			fbar.setSpacing(20);
			fbar.getChildren().addAll(baseInfo,addInfo);
			setGraphic(fbar);
			fresh();
			setOnMouseClicked(e->{
				if(e.getClickCount()==2)
					fdialog.renewStageAndShow(fml);
				fresh();
			});
		}
		public void fresh(){
			baseInfo.setText(fml.id+"  :  "+fml.content);
			if(currSortMode.equals("name"))
				addInfo.setText("");
			else if(currSortMode.equals("level"))
				addInfo.setText(fml.level);
			else 
				addInfo.setText(fml.count+"");
		}
		private Formula getFormula() {
			return fml;
		}
	}

	private class FormulaDialog extends Stage{
		private TextField ntf=new TextField();
		private TextField ctf=new TextField();
		private ComboBox<String> level=new ComboBox<>();
		private Button okbt=new Button("ok");
		private Button canbt=new Button("cancel");
		private Formula fml;
		private Label[] lbs= {
				new Label("name"),new Label("content"),new Label("level")
		};
		public FormulaDialog() {
			initial();
			setAction();
		};
		private void initial(){
			GridPane infoPane=new GridPane();
			infoPane.setPadding(new Insets(10,10,10,10));
			infoPane.setHgap(10);
			infoPane.setVgap(20);
			infoPane.add(lbs[0], 0, 0);
			infoPane.add(ntf, 1, 0);
			infoPane.add(lbs[1], 0, 1);
			infoPane.add(ctf, 1, 1);
			infoPane.add(lbs[2], 0, 2);
			infoPane.add(level, 1 ,2);
			infoPane.setAlignment(Pos.CENTER);
			GridPane.setHalignment(level, HPos.LEFT);
			//infoPane.setStyle("-fx-background-color:red");
			level.setItems(FXCollections.observableArrayList(
					new String[]{"A","B","C","D","E"}));
			HBox btbox=new HBox(30);
			btbox.setPadding(new Insets(10,10,10,10));
			btbox.getChildren().addAll(okbt,canbt);
			btbox.setAlignment(Pos.CENTER);
			//btbox.setStyle("-fx-background-color:green");
			VBox mainPane=new VBox(10);
			mainPane.getChildren().add(infoPane);
			mainPane.getChildren().add(btbox);
			mainPane.setAlignment(Pos.CENTER);
			mainPane.setStyle("-fx-background-color:pink");
			this.setScene(new Scene(mainPane,300,180));
		}
		
		private void setAction() {
			okbt.setOnAction(e->{
				if(ntf.getText().equals("")) {
					Alert alert=new Alert(AlertType.WARNING);
					alert.setContentText(null);
					alert.setHeaderText("Name can\'t be empty! Try again");
					alert.showAndWait();
				}
				else {
					fml.id=ntf.getText();
					fml.content=ctf.getText();
					fml.level=level.getValue();
					this.close();
				}
			});
			canbt.setOnAction(e->{
				this.close();
			});
		}
		
		public Formula renewStageAndShow(Formula f){
			if(f==null){
				fml=new Formula();
				ntf.setText("");
				ctf.setText("");
				level.setValue("A");
			}
			else{
				fml=f;
				ntf.setText(fml.id);
				ctf.setText(fml.content);
				level.setValue(fml.level);
			}
			// how to lock pane?????
			this.showAndWait();
			return fml;
		}
	}
	
	private class AddFormula implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			Formula f=fdialog.renewStageAndShow(null);
			if(!(f.id==null)){
				FCBList.add(new FormulaCheckBox(f));
			}
		}
	}
	
	private class DelFormula implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			// ArrayList is less effective than linkedList !!!
			FormulaCheckBox tmp;
			for(int i=0;i<FCBList.size();) {
				tmp=FCBList.get(i);
				if(tmp.isSelected())
					FCBList.remove(tmp);
				else i++;
			}
		}
	}
	
	private class SelectAll implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			//good code!!
			for(FormulaCheckBox fcb:FCBList)
				fcb.setSelected(!allSelected);
			allSelected=!allSelected;
		}
	}
	
	private SortExecutor exeSort =new SortExecutor();
	private class SetSortMode implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			currSortMode=((ComboBox<String>)(e.getSource())).getValue();
			exeSort.changeOrder();
			for(FormulaCheckBox fcb:FCBList)
				fcb.fresh();
		}
	}
	
	private class SetADMode implements EventHandler<ActionEvent>{
		public void handle(ActionEvent e) {
			currADMode=((ComboBox<String>)(e.getSource())).getValue();
			exeSort.changeOrder();
		}
	}
	
	private class SortExecutor {
		public void changeOrder(){
			if(currSortMode.equals("name")) {
				String[] arr=new String[FCBList.size()];
				for(int i=0;i<arr.length;i++)
					arr[i]=FCBList.get(i).fml.id;
				sort(arr);
			}
			else if(currSortMode.equals("count")) {
				Integer[] arr=new Integer[FCBList.size()];
				for(int i=0;i<arr.length;i++)
					arr[i]=FCBList.get(i).fml.count;
				sort(arr);
			}
			else if(currSortMode.equals("level")) {
				String[] arr=new String[FCBList.size()];
				for(int i=0;i<arr.length;i++)
					arr[i]=FCBList.get(i).fml.level;
				sort(arr);
			}
		}
		
		private <E extends Comparable<E>> void sort(E[] arr) {
			int i,j;
			for(i=1;i<arr.length;i++) {
				E tmpA=arr[i];
				FormulaCheckBox tmpB=FCBList.get(i);
				for(j=i;j>0 && putAhead(tmpA,arr[j-1]) ;j--) {
					arr[j]=arr[j-1];
					FCBList.set(j,FCBList.get(j-1));
				}
				arr[j]=tmpA;
				FCBList.set(j, tmpB);
			}
		}
		private<E extends Comparable<E>> boolean putAhead(E a, E b) {
			int n=a.compareTo(b);
			return currADMode.equals("ASC") && n< 0 || currADMode.equals("DESC") && n>0 ; 
		}
	}

}

class Formula implements Serializable{
	String id=null;
	String content="";
	String level="";
	int count=0;
}