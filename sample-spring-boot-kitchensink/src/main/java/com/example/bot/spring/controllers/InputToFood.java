package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;
import java.util.*;
import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/input")
public class InputToFood {
	@Autowired
	private FoodRepository foodRepository;
	
	public InputToFood() {
		
	}
	
    public @ResponseBody String readFromText(@RequestParam String text) {
    	String[] menu = text.split(System.getProperty("line.separator"));
    	String[][] result = new String[menu.length][50];
    	for(int i=0;i<menu.length;i++) {
        	int j=0;
        	if(foodRepository.findAll()!==null) {
        		for(Food fd : foodRepository.findAll()) {
        			if(menu[i].contains(fd.getName())) { 
    		        	result[i][j] = fd.getName();
        		    	j++;
    		        }   
        		}
        	}		 
    	}
    	String result = "";
    	for(int i=0;i<menu.length;i++) {
    		
    	}
    	return text;
    }

    public String readFromJSON(String url) {
    	return "";
    }

    public String readFromJPEG() {
    	return "";
    }
    
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "";
    		foodRepository.findAll().forEach(new Consumer<Food>() {
    		    public void accept(Food fd) {
    		        if(fd.getName().equals(food)) { 
    		        		resultFood.concat(fd.getDetails());
    		        }
    		    }
    		});
    		
    		return resultFood;
    }
    
}
