package com.example.bot.spring.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Profile {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	String userID;
	private String[] interests;
	
	public long getID() {
		return id;
	}
	
	public String getUserID () {
		return userID;
	}
	
	public String[] getInterests () {
		return interests;
	}
	
	public void setUserID(String id) {
		userID = id;
	}
	
	public void setInterest (String[] interestArray) {
		interests = interestArray;
	}
	
}
