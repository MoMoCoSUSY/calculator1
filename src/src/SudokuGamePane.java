import java.util.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.*;

public class SudokuGamePane extends VBox{
	private GridPane[][] units=new GridPane[3][3];
	private TextField[][] numstf=new TextField[9][9];
	private GridPane group=new GridPane();
	private Button okBt=new Button("done");
	private Button helpBt=new Button("help");
	private Button againBt=new Button("again");
	HBox btPane=new HBox(30);
	private Sudoku game=new Sudoku();
	private int[][] key;
	private int[][] answer=new int[9][9];
	public SudokuGamePane () {
		//style...............
		this.setSpacing(20);
		this.setStyle("-fx-background-color: pink");
		this.setPrefSize(400, 450);
		initial();
	}
	
	private void initial() {
		///number input pane............
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				numstf[i][j]=new TextField();
				numstf[i][j].setPrefSize(30, 30);
				numstf[i][j].setPrefColumnCount(1);
			}
		}
		
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				units[i][j]=new GridPane();
				units[i][j].setPadding(new Insets(10,10,10,10));
				units[i][j].setHgap(5);
				units[i][j].setVgap(5);
				for(int a=0;a<3;a++) {
					for(int b=0;b<3;b++) {
						int baseI=i*3,baseJ=j*3;
						units[i][j].add(numstf[baseI+a][baseJ+b],b,a);
					}
				}
			}
		}
		
		group.setPadding(new Insets(10,10,10,10));
		group.setAlignment(Pos.CENTER);
		group.setStyle("-fx-background-color: pink");
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				group.add(units[i][j],j,i);
			}
		}
		initialGame();
		
		///btPanae............
		
		btPane.setAlignment(Pos.CENTER);
		btPane.getChildren().addAll(okBt,helpBt);
		okBt.setOnAction(e->{
			Alert dialog=new Alert(AlertType.INFORMATION);
			dialog.setHeaderText(null);
			dialog.setGraphic(null);
			try {
				for(int i=0;i<9;i++){
					for(int j=0;j<9;j++) {
						answer[i][j]=Integer.parseInt(numstf[i][j].getText());
					}
				}
				if(game.checkSudoku(answer)) {
					btPane.getChildren().clear();
					btPane.getChildren().add(againBt);
					throw new Exception("Congradulations!");
				}
				else 
					throw new Exception("Not correct,try again.");
			}catch(RuntimeException ex) {
				dialog.setContentText("Illegal input! Check your number.");
				dialog.showAndWait();
			}catch(Exception ex) {
				dialog.setContentText(ex.getMessage());
				dialog.showAndWait();
			}
		});
		
		helpBt.setOnAction(e->{
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++) {
					numstf[i][j].setText(key[i][j]+"");
				}
			btPane.getChildren().clear();
			btPane.getChildren().add(againBt);
		});
		
		againBt.setOnAction(e->{
			initialGame();
			btPane.getChildren().clear();
			btPane.getChildren().addAll(okBt,helpBt);
		});
		
		this.getChildren().addAll(group,btPane);
	}

	private void initialGame() {
		key=game.getSudoku();
		boolean[][] lock=new boolean[9][9];
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				lock[i][j]=false;
		
		for(int i=0;i<9;i+=3) {
			for(int j=0;j<9;j+=3) {
				for(int k=0;k<3;){
					int a=(int)(Math.random()*3);
					int b=(int)(Math.random()*3);
					if(lock[i+a][j+b]!=true) {
						lock[i+a][j+b]=true;
						k++;
					}
				}
			}
		}
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(lock[i][j]) {
					numstf[i][j].setEditable(false);
					numstf[i][j].setText(key[i][j]+"");
					numstf[i][j].setStyle("-fx-border-color:red");
				}
				else {
					numstf[i][j].setEditable(true);
					numstf[i][j].setText("");
					numstf[i][j].setStyle("-fx-border-color:null");
				}
			}
		}
	}
	private class Sudoku{
		private int[][] numArr=new int[9][9];
		private ArrayList<Integer>[][] numForCell=new ArrayList[9][9];
		public boolean m(int i,int j) {
			ArrayList<Integer>cell=numForCell[i][j]=new ArrayList<>();
			cell.addAll(Arrays.asList(randomOrder()));
			for(int ind=0;ind<cell.size();) {
				Integer tmp=cell.get(ind);
				if(!check(i,j,tmp))
					cell.remove(tmp);
				else ind++;
			}
			if(i==8 && j== 8){
				if(cell.size()!=0) {
					numArr[i][j]=cell.get(0);
					return true;
				}
				return false;
			}
			
			int nextI,nextJ;
			if(j+1==9) {
				nextI=i+1;
				nextJ=0;
			}
			else {
				nextI=i;
				nextJ=j+1;
			}
			for(int n:cell){
				numArr[i][j]=n;
				if(m(nextI,nextJ))
					return true;
			}
			return false;
		}
		
		private Integer[] randomOrder() {
			Integer[] arr= {
				1,2,3,4,5,6,7,8,9	
			};
			for(int i=0;i<9;i++) {
				int ind=(int)(Math.random()*9);
				int tmp=arr[i];
				arr[i]=arr[ind];
				arr[ind]=tmp;
			}
			return arr;
		}
		private boolean check(int i,int j,int n) {
			for(int b=0;b<j;b++)
				if(numArr[i][b]==n)
					return false;
			for(int a=0;a<i;a++)
				if(numArr[a][j]==n)
					return false;
			int baseI=i-i%3,baseJ=j-j%3;
			for(int a=baseI;a<=i;a++) {
				int endJ;
				endJ= a<i ? baseJ+3:j;
				for(int b=baseJ;b<endJ;b++)
					if(numArr[a][b]==n)
						return false;
			}		
			return true;
		}
		public int[][] getSudoku() {
			m(0,0);
			return numArr;
		}
		public boolean checkSudoku(int numArr[][]) {
			int[] ruler;
			for(int i=0;i<9;i++) {
				ruler=new int[9];
				for(int j=0;j<9;j++) {
					int ind=numArr[i][j];
					if(ruler[ind-1]!=0) {
						System.out.println("11111");
						return false;
					}
					ruler[ind-1]=1;
				}	
			}
			
			for(int j=0;j<9;j++) {
				ruler=new int[9];
				for(int i=0;i<9;i++) {
					int ind=numArr[i][j];
					if(ruler[ind-1]!=0) {
						System.out.println("22222");
						return false;
					}
					ruler[ind-1]=1;
				}	
			}
			
			for(int i=0;i<9;i+=3)
				for(int j=0;j<9;j+=3) {
					ruler=new int[9];
					for(int a=i;a<i+3;a++)
						for(int b=j;b<j+3;b++) {
							int ind=numArr[a][b];
							if(ruler[ind-1]!=0) {
								System.out.println("33333");
								return false;
							}
							ruler[ind-1]=1;
						}
				}
			return true; 			
		}
	}
}