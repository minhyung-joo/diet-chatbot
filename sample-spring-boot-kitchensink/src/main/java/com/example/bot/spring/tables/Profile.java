package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity

public class Profile {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	String userID;
	String gender;
	int age;
	double height;
	private String[] interests;
	private Timestamp registeredTime;
	private boolean claimedNewUserCoupon;
	private boolean admin;
	public long getID() {
		return id;
	}
	
	public String getUserID () {
		return userID;
	}
	
	public String getGender() {
		return gender;
	}
	
	public int getAge() {
		return age;
	}
	
	public double getHeight() {
		return height;
	}
	
	public String[] getInterests () {
		return interests;
	}
	
	
	public Timestamp getRegisteredTime() {
		return registeredTime;
	}
	
	public boolean getClaimedNewUserCoupon() {
		return claimedNewUserCoupon;
	}
	public boolean getAdmin() {
		return admin;
	}
	
	public void setUserID(String id) {
		userID = id;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public void setInterest (String[] interestArray) {
		interests = interestArray;
	}
	public void setTime() {
		registeredTime = new Timestamp(System.currentTimeMillis());
	}
	
	
	public void setClaimedNewUserCoupon(boolean claimed) {
		claimedNewUserCoupon = claimed;
	}
	
	public void setAdmin(boolean admin) {
		this.admin=admin;
	}
	
	
	
}
