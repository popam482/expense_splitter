package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class GroupManager {

	private List<Group> usersGroups=new ArrayList<>();
	
	protected GroupManager() {
		read();
	}
	
	protected void read() {
		usersGroups.clear();
		try(BufferedReader br=new BufferedReader(new FileReader("groups.csv"))){
			String line;
			while((line=br.readLine())!=null) {
				String parts[]=line.split(";");
				if(parts.length==2) {
					Group gr=new Group(parts[0]);
					if(!parts[1].isEmpty()) {
						String[] members=parts[1].split(",");
						for(String m: members)
							gr.addMember(m.trim());
					}
					usersGroups.add(gr);
				}
			}
		}catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Groups file");
			alert.setContentText("Reading from the groups file has failed");
		}
	}
	
	protected void write() {
		try(BufferedWriter bw=new BufferedWriter(new FileWriter("groups.csv"))){
			for(Group gr:usersGroups) {
				bw.write(gr.getName()+";"+String.join(",", gr.getMembers()));
				bw.newLine();
			}
			
		}catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Groups file");
			alert.setContentText("Writing in the groups file has failed");
		}
	}
	
	protected void addGroup(String name, String creator) {
		Group gr=new Group(name);
		gr.addMember(creator);
		usersGroups.add(gr);
		write();
		createGroupFile(name);
	}
	
	private void createGroupFile(String name) {
		File groupFile=new File("groups/"+name+".csv");
		try{
			groupFile.createNewFile();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void delete(Group group) {
		Alert alert=new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete group");
		alert.setContentText("Are you sure you want to delete this group?");
		Optional<ButtonType> choice=alert.showAndWait();
		if(choice.isPresent() && choice.get()==ButtonType.OK) {
			usersGroups.remove(group);
			write();
		}
	}
	
	protected List<Group> getAllGroups() {
	    return usersGroups;
	}

	
	protected List<Group> getGroupsForUser(String username){
		List <Group> result=new ArrayList<>();
		for(Group gr:usersGroups) {
			if(gr.getMembers().contains(username)) {
				result.add(gr);
			}
		}
		return result;
	}
	
}
