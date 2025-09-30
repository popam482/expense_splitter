package application;

import java.io.IOException;
import java.lang.ModuleLayer.Controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserController {

	@FXML
	private Label signIn;
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Button signinButton;
	
	@FXML
	private Label statsText;
	
	@FXML
	private Hyperlink hyperlink;
	
	private boolean signinMode=true;
	private UserManager userManager=new UserManager();
	private User currentUser;
	
	public UserController() {
		userManager.read();
	}
	
	
	@FXML
	private void changeMethod() {
		FadeTransition ftSignIn=new FadeTransition(Duration.millis(200), signIn);
		ftSignIn.setFromValue(1.0);
		ftSignIn.setToValue(0.0);
		
		FadeTransition ftButton=new FadeTransition(Duration.millis(200), signinButton);
		ftButton.setFromValue(1.0);
		ftButton.setToValue(0.0);
		
		FadeTransition ftStats=new FadeTransition(Duration.millis(200), statsText);
		ftStats.setFromValue(1.0);
		ftStats.setToValue(0.0);
		
		FadeTransition ftLink=new FadeTransition(Duration.millis(200), hyperlink);
		ftLink.setFromValue(1.0);
		ftLink.setToValue(0.0);
		
		ParallelTransition fadeOut=new ParallelTransition(ftSignIn, ftButton, ftStats, ftLink);
	
		fadeOut.setOnFinished(e->{
			signinMode=!signinMode;
			if(signinMode) {
				signIn.setText("Sign in");
				signinButton.setText("Sign in");
				statsText.setText("Don't have an account?");
				hyperlink.setText("Sign up now!");
			}
			else {
				signIn.setText("Sign up");
				signinButton.setText("Sign up");
				statsText.setText("Already have an account?");
				hyperlink.setText("Sign in now!");
			}
			FadeTransition ftSignIn1=new FadeTransition(Duration.millis(200), signIn);
			ftSignIn1.setFromValue(0.0);
			ftSignIn1.setToValue(1.0);
			
			FadeTransition ftButton1=new FadeTransition(Duration.millis(200), signinButton);
			ftButton1.setFromValue(0.0);
			ftButton1.setToValue(1.0);
			
			FadeTransition ftStats1=new FadeTransition(Duration.millis(200), statsText);
			ftStats1.setFromValue(0.0);
			ftStats1.setToValue(1.0);
			
			FadeTransition ftLink1=new FadeTransition(Duration.millis(200), hyperlink);
			ftLink1.setFromValue(0.0);
			ftLink1.setToValue(1.0);
			
			ParallelTransition fadeIn=new ParallelTransition(ftSignIn1, ftButton1, ftStats1, ftLink1);
			fadeIn.play();
		});
		fadeOut.play();
	}
	
	@FXML
	private void ManageInput() {
		String username=usernameField.getText();
		String password=passwordField.getText();
		
		if(signinMode) {
			User u=userManager.signinUser(username, password);
			if(u!=null) {
				currentUser=u;
				try {
					Stage stage=Main.getPrimaryStage();
					FXMLLoader loader=new FXMLLoader(getClass().getResource("GroupMenu.fxml"));
					Parent root=loader.load();
					GroupMenuController controller = loader.getController();
	                Scene scene=new Scene(root);
	                scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
	                controller.initialize(currentUser);
	                
	                stage.setScene(scene);
	                stage.show();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			else {
				Alert alert=new Alert(AlertType.ERROR);
				alert.setTitle("Sign in");
				alert.setContentText("Wrong username or password");
				alert.showAndWait();
			}
		}
		else {
			if(!userManager.strongPassword(password)) {
				Alert alert=new Alert(AlertType.ERROR);
				alert.setTitle("Weak password");
				alert.setContentText("Your password must contain at least one upper case, digit and special character");
				alert.showAndWait();
				return;
			}
			boolean userAdded=userManager.addUser(username, password);
			if(userAdded) {
				Alert alert=new Alert(AlertType.INFORMATION);
				alert.setTitle("Sign up successful");
				alert.setContentText("Account created successfuly");
				alert.showAndWait();
				changeMethod();
			}
			else {
				Alert alert=new Alert(AlertType.ERROR);
				alert.setTitle("Sign up error");
				alert.setContentText("Username already exists");
				alert.showAndWait();
			}
		}
	}
	
}
