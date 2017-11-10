package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
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
	private Timestamp registeredTime;
	private boolean claimedNewUserCoupon = false;
	private int couponCodeCount = 0;
	
	
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
	public void setTime() {
		registeredTime = new Timestamp(System.currentTimeMillis());
	}
	public void addCoupon() {
		couponCodeCount +=1;
	}
	
}
