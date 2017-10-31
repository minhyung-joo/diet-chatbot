package com.example.bot.spring.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Profile {
	@Id
	long userID;
	private String[] interests;
	
	public Profile (long id, String[] interestArray) {
		userID = id;
		
		interests = interestArray;
	}
	
	public long getUserID () {
		return userID;
	}
	
	public String[] getInterests () {
		return interests;
	}
	
	public void setUserID(long id) {
		userID = id;
	}
	
	public void setInterest (String[] interestArray) {
		interests = interestArray;
	}
	
}
