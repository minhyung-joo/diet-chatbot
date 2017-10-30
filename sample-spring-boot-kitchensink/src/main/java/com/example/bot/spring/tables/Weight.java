package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Weight {
	long userID;
	private double weight;
	private Timestamp time;
	
	public Weight(long id, double w) {
		userID = id;
		weight = w;
		time = new Timestamp(System.currentTimeMillis());
	}
	
	public long getUserID() {
		return userID;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public void setUserID(long id) {
		userID = id;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
}