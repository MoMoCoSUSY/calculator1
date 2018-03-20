import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;
import javafx.util.Duration;

import java.io.*;

import javafx.collections.FXCollections;
import javafx.geometry.*;
public class MusicPlayer extends HBox{
	private String[] musicLib;
	private int currInd=0;
	private MediaPlayer player=null;
	private Button last=new Button("<<");
	private Button next=new Button(">>");
	private Button pause=new Button("||");
	private Button play=new Button("♬");
	private Slider volume=new Slider();
	private RadioButton circle=new RadioButton();
	public MusicPlayer(){
		File f=new File("bin/music/");
		musicLib=f.list();
		this.setPadding(new Insets(5,5,5,5));
		this.setSpacing(10);
		this.setAlignment(Pos.CENTER);
		initial();
		System.out.println("OK");
		this.cutSong(true);
	}	
	
	private void initial() {
		next.setOnAction(e->cutSong(true));
		
		last.setOnAction(e-> cutSong(false));
		
		play.setPrefSize(25, 25);
		play.setOnAction(e->{
			player.play();
			this.getChildren().set(2,pause);
		});
		pause.setPrefSize(25, 25);
		pause.setOnAction(e->{
			player.pause();
			this.getChildren().set(2,play);
		});
		
		Label vol=new Label("   ☄");

		volume.setOrientation(Orientation.HORIZONTAL);
		volume.setMax(100);
		volume.setMin(0);
		volume.setValue(50);
		
		this.getChildren().addAll(circle,last,pause,next,vol,volume);
	}
	
	private void cutSong(boolean next){
		if(next)
			currInd=(currInd+1)%musicLib.length;
		else if(currInd==0)
			currInd=musicLib.length-1;
		else currInd=currInd-1;
		
		if(musicLib.length==0)
			return ;
		String s=MusicPlayer.class.getResource("/music/"+musicLib[currInd]).toString();  
		if(player!=null)
			player.stop();
		player=new MediaPlayer(new Media(s));
		player.volumeProperty().bind(volume.valueProperty().divide(100));
		player.play();
		player.setOnEndOfMedia(()->{
			if(circle.isSelected()){
				player.seek(Duration.ZERO);
			}
			else 
				cutSong(true);
		});
	}
}
