import java.util.Scanner;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.util.*;
public class MiniGamePane extends VBox{
	private CalculatorLogic logic=new CalculatorLogic();
	
	private TextArea ta=new TextArea();
	private TextField tf=new TextField();
	private Button okBt=new Button("Done");
	private Button helpBt=new Button("Help");
	private Button againBt=new Button("Again");
	HBox btPane=new HBox(30);
	private Get24 game=new Get24();
	private int[] nums;

	public MiniGamePane(){
		///pane Style..........
		this.setPrefSize(250,200);
		this.setSpacing(10);
		this.setStyle("-fx-background-color: pink");
		initial();
	}
	
	private void initial() {
		nums=game.getNums();
		ta.setEditable(false);
		ta.setText(String.format("%d\t%d\t%d\t%d\n\n", nums[0],nums[1],nums[2],nums[3]));
		ta.setPrefHeight(100);
		tf.setOnAction(e->checkAnswer());
	
		btPane.setAlignment(Pos.CENTER);
		btPane.getChildren().addAll(okBt,helpBt);
		///bt action
		helpBt.setOnAction(e->{
			String[] solution=game.getSolution();
			for(int i=0;i<3;i++)
				ta.appendText(solution[i]+'\n');
			btPane.getChildren().clear();
			btPane.getChildren().add(againBt);
		});
		againBt.setOnAction(e->{
			nums=game.getNums();
			ta.setText(String.format("%d\t%d\t%d\t%d\n\n", nums[0],nums[1],nums[2],nums[3]));
			btPane.getChildren().clear();
			btPane.getChildren().addAll(okBt,helpBt);
		});
		okBt.setOnAction(e->checkAnswer());
	
		this.getChildren().addAll(ta,tf,btPane);
	}

	private void checkAnswer() {
		Alert dialog=new Alert(AlertType.INFORMATION);
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		try {
			logic.scan(tf.getText());
			Map<String,Double> IdMap=logic.getIdMap();
			if(IdMap.size()!=0)
				throw new Exception("Illegal Input!Try again.");
			ArrayList<Token> tokenStream=logic.getTokenStream();
			ArrayList<Integer> inputNums=new ArrayList<>();
			for(Token t:tokenStream) {
				if(t.cat==3)
					inputNums.add(Integer.parseInt(t.content));
			}
			if(inputNums.size()!=4)
				throw new RuntimeException();
			
			for(int n :nums)
				inputNums.remove(new Integer(n));
			if(inputNums.size()!=0)
				throw new RuntimeException();
			double result=logic.compute();
			System.out.println(result);
			if(Math.abs(result-24)<=0.0000000001){
				btPane.getChildren().clear();
				btPane.getChildren().addAll(okBt,helpBt);
				throw new Exception("Congradulations! You are right!");
			}
			else throw new Exception("Not correct,try again!");
		}catch(RuntimeException ex) {
			dialog.setContentText("Illegal input! Try again.");
			dialog.showAndWait();
		}
		catch(Exception ex){
			dialog.setContentText(ex.getMessage());
			dialog.showAndWait();
		}
	}
	
	private class Get24{
		private int[] arr=new int[4];
		private String[] step=new String[3];
		
		public int[] getNums() {
			while(true) {
				for(int i=0;i<4;i++) {
					arr[i]=1+(int)(Math.random()*10);
				}
				if(m1(arr,0))
					break;
			}
			return arr;
		}
		
		public String[] getSolution() {
			return step;
		}
		
		private  boolean m1(int[] arr,int h) {
			if(h==arr.length-1)
				return m2(arr,1,arr[0]);
			for(int i=h;i<arr.length;i++) {
				int tmp=arr[h];
				arr[h]=arr[i];
				arr[i]=tmp;
				if(m1(arr,h+1))
					return true;
				arr[i]=arr[h];
				arr[h]=tmp;
			}
			return false; 
		}
		
		private char[] op={'+','-','*','/'};
		private boolean m2(int[] arr,int h,int a){
			int r,b=arr[h];
			for(int i=0;i<4;i++) {
				if(i==0)
					r=a+b;
				else if(i==1)
					r=a-b;
				else if(i==2)
					r=a*b;
				else{
					if(a%b!=0)
						return false;
					r=a/b;
				}
				if(h==3){
					if(r==24) {
						step[h-1]=String.format("%d%c%d=%d",a,op[i],b,r);
						return true;
					}
				}
				else if(m2(arr,h+1,r)) {
					step[h-1]=String.format("%d%c%d=%d",a,op[i],b,r);
					return true;
				}
			}
			return false;
		}
	}
}

