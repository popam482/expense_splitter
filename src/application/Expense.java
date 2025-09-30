package application;

import java.util.List;

public class Expense {

	String payer;
	double amount;
	String description;
	List<String> participants;
	
	protected Expense(String payer, String description, double amount, List<String> participants) {
		this.payer=payer;
		this.amount=amount;
		this.description=description;
		this.participants=participants;
	}
	
	protected String getPayer() {
		return payer;
	}
	
	protected double getAmount() {
		return amount;
	}
	
	protected String getDescription() {
		return description;
	}
	
	protected List <String> getParticipants() {
		return participants;
	}
}
