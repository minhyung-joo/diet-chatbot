package com.example.bot.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Menu {
	private String name;
	private int price;
	private String[] ingredients;
	
	public Menu() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String[] setIngredients() {
		return ingredients;
	}
	
	public void setIngredients(String[] ingredients) {
		this.ingredients = ingredients;
	}
}
