package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Meal{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	private String userID;
	private String food;
	private Timestamp time;

	public String getUserID() {
		return userID;
	}

	public String getFood() {
		return food;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setUserID(String id) {
		userID = id;
	}
	
	public void setFood(String fd) {
		food = fd;
	}
	public void setTime() {
		time = new Timestamp(System.currentTimeMillis());
	}
}