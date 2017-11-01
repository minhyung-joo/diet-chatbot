package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;
import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller    // This means that this class is a Controller
public class InputToFood {
	@Autowired
	private FoodRepository foodRepository;
	
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
    		String resultFood = "";
    		foodRepository.findAll().forEach(new Consumer<Food>() {
    		    public void accept(Food fd) {
    		        if (fd.getName().equals(food) && resultFood.length() == 0) {
    		        		resultFood.concat(fd.getDetails());
    		        }
    		    }
    		});
    		
    		return resultFood;
    	}
}
