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
	private byte [] couponImage;
	private int count;
	
	public Timestamp getTime() {
		return time;
	}
	public byte [] getCouponImage () {
		return couponImage;
	}
	public int getCount() {
		return count;
	}
	
	public void setTime() {
		time = new Timestamp (System.currentTimeMillis());
	}
	public void setCouponImage(byte [] couponImg) {
		couponImage = couponImg;
	}
	public void incrementCount() {
		count +=1;
	}
}