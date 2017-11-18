package com.example.bot.spring.tables;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.*;

import java.sql.Timestamp;
import java.util.Date;


@Entity

@SequenceGenerator(name="seq", initialValue=100000, allocationSize=10000)
public class Recommendation {
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    @Id long id;
	private String uniqueCode;
	private String userID;
	private boolean claimed;
	
	public long getID () {
		return id;
	}
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
