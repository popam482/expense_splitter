package application;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private String groupName;
	private List<String> members;
	
	protected Group(String groupName) {
		this.groupName=groupName;
		this.members=new ArrayList<>();
	}
	
	protected String getName() {
		return groupName;
	}
	
	protected List<String> getMembers(){
		return members;
	}
	
	protected void addMember(String username) {
		if(!members.contains(username))
			members.add(username);
	}
	
}
