import javafx.scene.control.CheckBox;

import java.io.File;
import java.util.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.*;

import java.util.Optional;

import javafx.application.*;
import javafx.stage.*;
public class Test extends Application{
	public void start(Stage stage) {
		CompleteWindow mainPane=new CompleteWindow();
		Scene scene=new Scene(mainPane,400,600);
		stage.setScene(scene);
		stage.show();
	}	
}


