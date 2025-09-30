package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserManager {

	private List<User> users=new ArrayList<>();
	
	protected UserManager() {
		read();
	}
	
	
	public void read() {
		users.clear();
		try(BufferedReader br=new BufferedReader(new FileReader("userlist.csv")) ){
			String line;
			while((line=br.readLine())!=null) {
				String[]parts=line.split(";");
				if(parts.length==3)
					users.add(User.fromFile(parts[0], parts[1], parts[2]));
			}
		}
		catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Reading from the database");
			alert.setContentText("Reading from the database failed");
			alert.showAndWait();
		}
	}

	protected void write() {
		try(BufferedWriter bw=new BufferedWriter(new FileWriter("userlist.csv"))){
			for(User u: users) {
				bw.write(u.getUsername()+";"+u.getPassword()+";"+u.getSalt());
				bw.newLine();
			}
		}
		catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Writing in the baseline");
			alert.setContentText("Writing in the database failed");
			alert.showAndWait();
		}
	}
	
	protected boolean usernameExists(String username) {
		for(User u:users)
			if(u.getUsername().equals(username))
				return true;
		return false;
	}
	
	public User signinUser(String username, String password) {
		for(User u:users) {
			if(u.getUsername().equals(username) && u.verifyPassword(password))
				return u;
		}
		return null;
	}

	public boolean strongPassword(String password) {
		if(password==null)
			return false;
		if(password.length()<6)
			return false;
		boolean upper=false, digit=false, special=false;
		for(char c: password.toCharArray()) {
			if(Character.isUpperCase(c))
    			upper=true;
    		else
    			if(Character.isDigit(c))
    				digit=true;
    			else
    				if(!Character.isLetterOrDigit(c))
    					special=true;
		}
		return upper&&digit&&special;
	}

	public boolean addUser(String username, String password) {
		if(usernameExists(username)) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Wrong username");
			alert.setContentText("Username already exist");
			alert.showAndWait();
			return false;
		}
		User newUser=new User(username, password);
		users.add(newUser);
		write();
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Add user");
		alert.setContentText("User added successfully");
		return true;
	}

}
