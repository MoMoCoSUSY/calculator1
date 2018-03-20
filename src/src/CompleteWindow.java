import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
public class CompleteWindow extends VBox{
	
	
	private CalculaterPane cal=new CalculaterPane();
	private FormulaEditor fedit=new FormulaEditor();
	private FunctionImagePane funcImage=new FunctionImagePane();
	private HBox btPane=new HBox();
	private Pane windowPane=new Pane();

	private Button[] windows={
			new Button("Calculator"),new Button("FormulaLib"),new Button("FouncImage"),
			new Button("MiniGame"),new Button ("MyNoteBook"),new Button("MyStyle")
	};
	
	public CompleteWindow() {
		///mainPane set.....
		this.setPrefSize(400, 600);
		cal.updateFmlcbList(fedit.getFormulaLib());
		initial();
		
	}
	public void initial(){
		///btPane
		btPane.setPrefSize(400, 50);
		btPane.setPadding(new Insets(10,10,10,10));
		btPane.setStyle("-fx-background-color:orange");
		btPane.setSpacing(20);
		btPane.getChildren().addAll(windows[0],windows[1],windows[2],windows[3]);
		
		///windowsPane
		windowPane.setStyle("-fx-background-color: blue");
		windowPane.setPrefSize(400, 500);
		windowPane.getChildren().add(cal);
		
		//
		
		////total
		this.getChildren().addAll(btPane,windowPane,new MusicPlayer());
		
		registAction();
	}
	
	private void registAction() {
		windows[0].setOnAction(e->{
			cal.updateFmlcbList(fedit.getFormulaLib());
			windowPane.getChildren().clear();
			windowPane.getChildren().add(cal);
		});
		windows[1].setOnAction(e->{
			windowPane.getChildren().clear();
			windowPane.getChildren().add(fedit);
		});
		windows[2].setOnAction(e->{
			windowPane.getChildren().clear();
			windowPane.getChildren().add(funcImage);
		});
		
		Alert dialog=new Alert(AlertType.INFORMATION);
		dialog.setHeaderText("choose your game:");
		dialog.setContentText(null);
		HBox box=new HBox(20);
		box.setMaxHeight(30);
		box.setMaxWidth(150);
		Button g1=new Button("24Points");
		Button g2=new Button("Sudoku");
		box.getChildren().addAll(g1,g2);
		dialog.setGraphic(box);
		
		g1.setOnAction(e->{
			dialog.close();
			Stage stage=new Stage();
			stage.setScene(new Scene(new MiniGamePane(),250,200));
			stage.show();
		});
		g2.setOnAction(e->{
			dialog.close();
			Stage stage=new Stage();
			stage.setScene(new Scene(new SudokuGamePane(),400,450));
			stage.show();
		});
		windows[3].setOnAction(e->{
			dialog.showAndWait();
		});
		
	}
}
