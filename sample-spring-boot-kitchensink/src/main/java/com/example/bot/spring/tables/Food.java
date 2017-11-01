package com.example.bot.spring.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Food {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long foodID;
	String name;
	String category;
	double calories;
	double sodium;
	double saturatedFat;
	double protein;
	double carbohydrate;

	public Food(String name, String cat, double cal, double sod, double fat, double protein, double carb) {
		this.name = name;
		this.category = cat;
		this.calories = cal;
		this.sodium = sod;
		this.saturatedFat = fat;
		this.protein = protein;
		this.carbohydrate = carb;
	}

	public long foodID() {
		return foodID;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public double getCalories() {
		return calories;
	}

	public double getSodium() {
		return sodium;
	}

	public double getSaturatedFat() {
		return saturatedFat;
	}

	public double getProtein() {
		return protein;
	}

	public double getCarbohydrate() {
		return carbohydrate;
	}
	
	public String getDetails() {
		String details =
				"Here are the details for " + this.getName() + "\n" +
				"Calories: " + getCalories() + "\n" +
				"Sodium: " + getSodium() + "\n" +
				"Saturated Fat: " + getSaturatedFat() + "\n" +
				"Protein: " + getProtein() + "\n" +
				"Carbohydrate: " + getCarbohydrate();
		
		return details;
	}

}