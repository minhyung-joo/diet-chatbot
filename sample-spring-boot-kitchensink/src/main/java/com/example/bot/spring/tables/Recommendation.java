package com.example.bot.spring.tables;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.sql.Timestamp;
import java.util.Date;


@Entity
public class Recommendation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	@Min(100000)
	@Max(999999)
	private Integer id;
	private String uniqueCode;
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
