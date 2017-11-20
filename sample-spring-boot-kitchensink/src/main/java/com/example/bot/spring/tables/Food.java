package com.example.bot.spring.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "food")
public class Food {
	@Id
	@Column(name="foodID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	long foodID;
	
	@Column(name="name")
	String name;
	
	@Column(name="category")
	String category;
	
	@Column(name="calories")
	double calories;
	
	@Column(name="sodium")
	double sodium;
	
	@Column(name="saturatedFat")
	double saturatedFat;
	
	@Column(name="protein")
	double protein;
	
	@Column(name="carbohydrate")
	double carbohydrate;

	public long getFoodID() {
		return foodID;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String cat) {
		this.category = cat;
	}

	public double getCalories() {
		return calories;
	}
	
	public void setCalories(double cal) {
		this.calories = cal;
	}

	public double getSodium() {
		return sodium;
	}
	
	public void setSodium(double sod) {
		this.sodium = sod;
	}

	public double getSaturatedFat() {
		return saturatedFat;
	}
	
	public void setSaturatedFat(double fat) {
		this.saturatedFat = fat;
	}

	public double getProtein() {
		return protein;
	}
	
	public void setProtein(double protein) {
		this.protein = protein;
	}

	public double getCarbohydrate() {
		return carbohydrate;
	}
	
	public void setCarbohydrate(double carb) {
		this.carbohydrate = carb;
	}
	
	public String getDetails() {
		String details =
				"Calories: " + getCalories() + "\n" +
				"Sodium: " + getSodium() + "\n" +
				"Saturated Fat: " + getSaturatedFat() + "\n" +
				"Protein: " + getProtein() + "\n" +
				"Carbohydrate: " + getCarbohydrate();
		
		return details;
	}

}