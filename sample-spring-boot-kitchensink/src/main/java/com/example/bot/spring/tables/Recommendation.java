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

@SequenceGenerator(name="seq", initialValue=1, allocationSize=1)
public class Recommendation {
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    @Id long id;
	private String userID;
	private boolean claimed;
	private long uniqueCode;
	
	public long getID () {
		return id;
	}
	public String getUserID () {
		return userID;
	}
	
	public boolean getClaimed() {
		return claimed;
	}
	
	public long getUniqueCode() {
		return uniqueCode;
	}
	
	public void setUserID(String id) {
		userID = id;
	}
		
	public void setClaimed(boolean claim) {
		claimed = claim;
	}
	public void setUniqueCode(long unique) {
		uniqueCode = unique;
	}
}