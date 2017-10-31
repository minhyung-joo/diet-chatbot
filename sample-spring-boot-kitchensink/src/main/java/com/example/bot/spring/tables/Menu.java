package com.example.bot.spring.tables;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Menu{
	@Id
	private long userID;
	private long[] foodIdArray;

	public Menu(long uid, int numberOfFood) {
		userID = uid;
		foodIdArray = new long[numberOfFood];
	}

	public long getUserID() {
		return userID;
	}

	public long[] getFoodIdArray() {
		return foodIdArray;
	}

	public void setUserID(long id) {
		userID = id;
	}

	public void setFoodIdArray(long[] id) {
		for(int i=0; i<id.length; i++) {
			foodIdArray[i] = id[i];
		}
	}
}