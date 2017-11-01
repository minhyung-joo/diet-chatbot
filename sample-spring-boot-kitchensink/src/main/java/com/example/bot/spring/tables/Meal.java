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
	String userID;
	private long foodID;
	private Timestamp time;

	public long getUserID() {
		return userID;
	}

	public long getFoodID() {
		return foodID;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setUserID(long id) {
		userID = id;
	}

	public void setFoodID(long id) {
		foodID = id;
	}
	public void setTime() {
		time = new Timestamp(System.currentTimeMillis());
	}
}