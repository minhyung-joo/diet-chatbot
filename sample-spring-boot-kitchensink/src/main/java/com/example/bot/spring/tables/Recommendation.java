package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Recommendation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	String uniqueCode;
	private String userID;
	private boolean claimed;
	
	public String getUserID () {
		return userID;
	}
	
	public String getUniqueCode () {
		return uniqueCode;
	}
	
	public boolean getClaimed() {
		return claimed;
	}
	
	public void setUserID(String id) {
		userID = id;
	}
	
	public void setUniqueCode (String unique) {
		uniqueCode = unique;
	}
	
	public void setClaimed(boolean claim) {
		claimed = claim;
	}
}
