package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;

public class InputToFood {
	
	public InputToFood() {
		
	}
	
    public String readFromText(String text) {
    	return text;
    }

    public String readFromJSON(String url) {
    	return "";
    }

    public String readFromJPEG() {
    	return "";
    }
    
    public String getFoodDetails(String food) {
    		FoodRepository foodRepository = new FoodRepository();
    		Iterable<Food> allFood = new Iterable<Food>;
    		allFood = foodRepository.findAll();
    		
    		allFood.forEach(new Consumer<Food>() {
    		    public void accept(Food fd) {
    		        if(fd.getName().equals(food)) { 
    		        		return fd.getDetails();
    		        }
    		    }
    		});
    		
    		return null;
    	}
}
