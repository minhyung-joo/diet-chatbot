package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;
import com.example.bot.spring.controllers.MenuController;

import java.util.*;
import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import com.example.bot.spring.models.Menu;

@Controller
@RequestMapping(path="/input")
public class InputToFood {
	@Autowired
	private FoodRepository foodRepository;
	
	@Autowired
	private MenuController menuController;

	@GetMapping(path="/readfromtext")
    public @ResponseBody String readFromText(@RequestParam String userId, @RequestParam String text) {
		menuController.setUserID(userId);
		menuController.setMenu(text);
    	return menuController.pickFood();
    }

    public String readFromJSON(String url) {
    	try {
    		RestTemplate restTemplate = new RestTemplate();
        	Menu[] menuList = restTemplate.getForObject(url, Menu[].class);
        	StringBuilder builder = new StringBuilder();
        	int counter = 1;
        	builder.append("The Foods in each entree are as followed:\n");
        	for (Menu menu : menuList) {
        		String[] ingredients = menu.getIngredients();
        		builder.append(counter);
        		builder.append(". ");
        		for (String ingredient : ingredients) {
        			builder.append(ingredient);
        			builder.append(", ");
        		}
        		counter++;
        		builder.append("\n");
        	}
        	builder.deleteCharAt(builder.length() - 1);
        	
        	return builder.toString();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return "Failed to load URL.";
    	}
    }

    public String readFromJPEG() {
    	return "";
    }
    
    @GetMapping(path="/getfooddetails")
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "You have entered " + food + "\n";
    		String[] splitFood = food.split("\\s+");
    		
	    	for(int i=0; i<splitFood.length; i++) {	
	    		for(Food fd : foodRepository.findAll()) {
	    		    String fdName = fd.getName().toLowerCase();
	            	if(fdName.contains(",")) {
	            		fdName = fdName.substring(0,fdName.indexOf(","));
	            	}
	            	if(fdName.endsWith("s")) {
	            		fdName = fdName.substring(0, fdName.length()-1);
	            	}
	            	
	    		    if(splitFood[i].toLowerCase().contains(fdName)) { 
	    		    		resultFood += "Here are the details for " + fdName + "\n" + fd.getDetails() + "\n" + "\n";
	    		    		break;
	    		    }
	    		}
	    	}
    		
    		return resultFood;
    }
    
}
