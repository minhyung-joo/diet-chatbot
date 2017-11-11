package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Campaign {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	private Timestamp time;
	
	public Timestamp getTime() {
		return time;
	}
	
	public void setTime() {
		time = new Timestamp(System.currentTimeMillis());
	}
	
}