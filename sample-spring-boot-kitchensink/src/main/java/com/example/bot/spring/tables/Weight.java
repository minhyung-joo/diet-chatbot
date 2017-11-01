package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Weight {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	String userID;
	private double weight;
	private Timestamp time;
	
	public String getUserID() {
		return userID;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public void setUserID(String id) {
		userID = id;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	public void setTime() {
		time = new Timestamp(System.currentTimeMillis());
	}
	
}