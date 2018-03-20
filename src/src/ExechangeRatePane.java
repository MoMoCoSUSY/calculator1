import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.geometry.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
public class ExechangeRatePane extends VBox{
	private RateExchanger RE=new RateExchanger();
	private TextField amountTF=new TextField();
	private TextField resultTF=new TextField();
	private ComboBox<String> currency1=new ComboBox<>();
	private ComboBox<String> currency2=new ComboBox<>();
	public ExechangeRatePane() {
		this.setPrefSize(500, 100);
		this.setSpacing(20);
		this.setStyle("-fx-background-color:pink");
		this.setPadding(new Insets(10,10,10,10));
		initial();
	}
	public void initial() {
		Map<String,Double> rateMap=RE.getRateMap();
		HBox inputPane=new HBox(10);
		inputPane.setAlignment(Pos.CENTER);
		amountTF.setPrefWidth(150);	
		currency1.setItems(FXCollections.observableArrayList(rateMap.keySet()));
		currency1.setValue("人民币(CNY)");
		currency1.setPrefWidth(150);
		currency2.setItems(FXCollections.observableArrayList(rateMap.keySet()));
		currency2.setValue("人民币(CNY)");
		currency2.setPrefWidth(150);
		inputPane.getChildren().addAll(amountTF,currency1,new Label("to"),currency2);
		resultTF.setEditable(false);
		this.getChildren().addAll(inputPane,resultTF);
		
		amountTF.setOnAction(e->showResult());
		resultTF.setOnAction(e->showResult());
	}
	
	private void showResult() {
		try {
			double amount=Double.parseDouble(amountTF.getText());
			String c1=currency1.getValue();
			String c2=currency2.getValue();
			double r=RE.getExchangeResult(amount, c1, c2);
			resultTF.setText(String.format("%.2f %s = %.2f %s", amount,c1,r,c2));
		}catch(RuntimeException ex) {
			Alert dialog=new Alert(AlertType.WARNING);
			dialog.setContentText(null);
			dialog.setHeaderText("Illegal input! Try again.");
			dialog.showAndWait();
		}
	}
}

class RateExchanger{
	private Map<String,Double> rateMap;
	private String savePath="D:/456.dat";
	private Alert dialog=new Alert(AlertType.INFORMATION);
	public RateExchanger(){
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		updateRate();
		loadExchangeRate();
	}
	private  void updateRate(){
		Map<String,Double> rateMap=new LinkedHashMap<>();
		rateMap.put("人民币(CNY)",1.0);
		try {
			URL url=new URL("http://hl.anseo.cn");
			Scanner input=new Scanner(url.openStream(),"utf-8");
			boolean start=false;
			double rate;
			String currency;
			while(input.hasNext()){
				String line=input.nextLine();
				if(line.equals("<html>"))
					throw new UnknownHostException();
				if(start){
					if(line.contains("/div>"))
						break;
					//<li ___>1 人民币 = 1.2159 <a ___>澳门元</a>(TWD)___<br><br><b ___><a ___>___</a></b></li>
					if(!line.matches("<li .*>.*<a .*>.*</a>.*<br><br><b .*><a .*>.*</a></b></li>"))
						throw new Exception("You need to update your version");
					int ind1,ind2;
					ind1=line.indexOf("人民币 = ");
					ind2=line.indexOf("<",ind1);
					rate=Double.parseDouble(line.substring(ind1+5,ind2));
					ind1=line.indexOf(">",ind2);
					ind2=line.indexOf("<",ind1);
					currency=line.substring(ind1+1,ind2);
					ind1=line.indexOf("(",ind2);
					ind2=line.indexOf(")",ind1);
					currency+=line.substring(ind1,ind2+1);
					rateMap.put(currency, rate);
				}
				else if(line.contains("<div id=\"rates\"")) {
					start=true;
				}
			}
			if(rateMap.size()==1)
				throw new Exception("You need to update your version");
			input.close();	
			ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream(new File(savePath)));
			output.writeObject(rateMap);
			output.close();
		}catch(UnknownHostException e) {
			dialog.setContentText("Failed to connect to Internet");
			dialog.showAndWait();
		}
		catch(Exception e){
			e.printStackTrace();
			dialog.setContentText(e.getMessage());
			dialog.showAndWait();
		}
	}
	
	private  void loadExchangeRate() {
		try {
			ObjectInputStream in=new ObjectInputStream(new FileInputStream(new File(savePath)));
			rateMap=(Map<String,Double>)(in.readObject());
			in.close();
		}catch(ClassNotFoundException e) {
		}catch(IOException e) {
			dialog.setContentText("Exchange rate data lost");
			dialog.showAndWait();
			rateMap=new LinkedHashMap<>();
			rateMap.put("人民币(CNY)",1.0);
		}
	}
	public Map<String,Double> getRateMap(){
		return rateMap;
	}
	
	public double getExchangeResult(double amount,String c1,String c2) {
		double r1=rateMap.get(c1),r2=rateMap.get(c2);
		return amount/r1*r2;
	}
}