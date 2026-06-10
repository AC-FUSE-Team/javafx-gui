import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @title ${type_name}
 * @author Aspen
 * @date ${date}{time}
 * @version 
 */

public class Controller {
	/**
	 * button for learning section
	 */
	@FXML
	private Button buttonL;
	
	/**
	 * button for testing section
	 */
	@FXML
	private Button buttonT;
	
	/**
	 * button to back to intro screen
	 */
	@FXML
	private Button buttonBack;

	
	/**
	 * click button to go to learning section
	 * @param event the action event triggered by clicking the Learning button
	 */
	@FXML
	protected void clickLearn(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Learning_Mode_Screen.fxml"));
			Parent root = loader.load();
			
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * click button to go to learning section
	 * @param event the action event triggered by clicking the Testing button
	 */
	@FXML
	protected void clickTest(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Testing_Mode_Screen.fxml"));
			Parent root = loader.load();
			
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * click button to go to learning section
	 * @param event the action event triggered by clicking the Back to Menu button
	 */
	@FXML
	protected void clickBack(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Intro_Screen.fxml"));
			Parent root = loader.load();
			
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
