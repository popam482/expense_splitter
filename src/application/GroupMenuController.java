package application;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GroupMenuController {

    @FXML
    private TilePane groupsPane; 
    
    @FXML
    private Button logOutButton;
    
    private GroupManager groupManager;
    private User currentUser;
    private UserManager allUsers=new UserManager();
    
    protected void initialize(User user) {
        this.currentUser=user;
        this.groupManager=new GroupManager();
        loadGroups();
    }

    public void loadGroups(){
        groupsPane.getChildren().clear();
        
        List<Group> groups = groupManager.getGroupsForUser(currentUser.getUsername());     
        for (Group groupName : groups) {
            VBox card = createGroupCard(groupName);
            groupsPane.getChildren().add(card);
        }
    }

    private VBox createGroupCard(Group group) {
        VBox card = new VBox();
        card.setPrefSize(175, 150);
        card.setSpacing(10);
        card.getStyleClass().add("card");  

        Label title = new Label(group.getName());
        title.getStyleClass().add("group-title");
        

        VBox actionsBox = new VBox();
        actionsBox.setSpacing(8);
        actionsBox.setAlignment(Pos.CENTER); 
        actionsBox.setPrefHeight(100);        

        Button openBtn = new Button("Open");
        openBtn.getStyleClass().add("open-button");
        openBtn.setOnAction(e -> openGroup(group));
        actionsBox.getChildren().add(openBtn);

        if (!group.getMembers().isEmpty() && group.getMembers().get(0).equals(currentUser.getUsername())) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("delete-button");
            deleteBtn.setOnAction(e -> {
                groupManager.delete(group);
                loadGroups();
            });

            Button addMembersBtn = new Button("Add new members");
            addMembersBtn.getStyleClass().add("add-button");
            addMembersBtn.setOnAction(e -> addMembersToGroup(group));

            actionsBox.getChildren().addAll(deleteBtn, addMembersBtn);
        }

        card.getChildren().addAll(title, actionsBox);

        return card;
    }

    @FXML
    private void addNewGroup() {
        TextInputDialog dialog=new TextInputDialog();
        dialog.setTitle("New Group");
        dialog.setHeaderText("Creating a new group");
        dialog.setContentText("Enter the group name:");
        Optional<String> groupName=dialog.showAndWait();
        groupName.ifPresent(name->{
            groupManager.addGroup(name,  currentUser.getUsername());
            loadGroups();
            Group newGroup=groupManager.getAllGroups().get(groupManager.getAllGroups().size()-1);
            addMembersToGroup(newGroup);
        });
    }
    
    
    private void addMembersToGroup(Group group) {
        TextInputDialog dialog=new TextInputDialog();
        dialog.setTitle("Add members");
        dialog.setHeaderText("Add members to "+ group.getName());
        dialog.setContentText("Enter the usernames separated by , :");
        
        Optional<String>members=dialog.showAndWait();
        members.ifPresent(usernames->{
            boolean addedUser=false;
            for(String username: usernames.split(",")) {
                username=username.trim();
                if(!allUsers.usernameExists(username)) {
                    Alert alert=new Alert(AlertType.ERROR);
                    alert.setTitle("Member error");
                    alert.setContentText(username + " doesn't exist");
                    alert.showAndWait();
                    
                }
                else if(!group.getMembers().contains(username)){
                    group.addMember(username);
                    addedUser=true;
                }
            }
            if(addedUser) {
                Alert alert=new Alert(AlertType.INFORMATION);
                alert.setTitle("New members");
                alert.setHeaderText("New members have been added");
                alert.showAndWait();
            }
            groupManager.write();
            loadGroups();
        });
    }
    
    @FXML
    private void backToLogIn() {
        try {
            Stage stage=Main.getPrimaryStage();
            FXMLLoader loader=new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root=loader.load();
            Scene scene=new Scene(root);
            scene.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openGroup(Group group) {
        try {
            Stage stage=Main.getPrimaryStage();
            FXMLLoader loader=new FXMLLoader(getClass().getResource("GroupContent.fxml"));
            Parent root=loader.load();
            GroupContentController controller = loader.getController();
            Scene scene=new Scene(root);
            controller.initializeGroup(group,currentUser);
            
            stage.setScene(scene);
            stage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
