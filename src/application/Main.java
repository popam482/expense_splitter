package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private static Stage primaryStage;
	
	@Override
	public void start(Stage stage) {
		try {
			primaryStage=stage;
			Parent root=FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene=new Scene(root);
			scene.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setTitle("Expense Splitter");
			Image logo=new Image("logo.png");
			stage.getIcons().add(logo);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	   public static Stage getPrimaryStage() {
	        return primaryStage;
	    }
}
