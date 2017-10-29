package com.example.bot.spring.tables;


@Entity
public class Profile {
	long userID;
	private String [] intersts;
	
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
