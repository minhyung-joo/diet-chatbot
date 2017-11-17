package com.example.bot.spring.tables;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.sql.Timestamp;
import java.util.Date;


@Entity
public class Recommendation {
	@Id
	@Column(name = "id")
	@GeneratedValue(
		    strategy=GenerationType.SEQUENCE, 
		    generator="SEQ_GEN")
	@javax.persistence.SequenceGenerator(
		    name="SEQ_GEN",
		    sequenceName="my_sequence",
		    allocationSize=20,
		    initialValue=100000
		    )
	
	
	private long id;
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
