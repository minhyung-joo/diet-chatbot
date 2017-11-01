package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;

import java.util.*;
import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import com.example.bot.spring.models.MenuList;
import com.example.bot.spring.models.Menu;


@Controller
@RequestMapping(path="/input")
public class InputToFood {
	@Autowired
	private FoodRepository foodRepository;

	@GetMapping(path="/readfromtext")
    public @ResponseBody String readFromText(@RequestParam String text) {
		String[] menu = text.split(System.getProperty("line.separator"));
    	List<Set<String>> result = new ArrayList<Set<String>>();
    	for(int i=0;i<menu.length;i++) {
    		Set<String> names = new HashSet<String>();
        	int j=0;
        	for(Food fd : foodRepository.findAll()) {
        		if(menu[i].toLowerCase().contains(fd.getName().substring(0,fd.getName().indexOf(",")).toLowerCase())) { 
    	        	names.add(fd.getName());
       		    	j++;
   		        }   
       		}
        	result.add(names);
    	}
    	String resultText = "The Foods in each entree are as followed:\n";
    	for(int i=0;i<menu.length;i++) {
    		resultText += (i+1)+". ";
    		for(String s : result.get(i)) {
    			resultText += s + ", ";
    		}
    		resultText += "\n";
    	}
    	return resultText;
    }

    public String readFromJSON(String url) {
    	RestTemplate restTemplate = new RestTemplate();
    	Menu[] menuList = restTemplate.getForObject(url, Menu[].class);
    	StringBuilder builder = new StringBuilder();
    	int counter = 1;
    	for (Menu menu : menuList) {
    		String[] ingredients = menu.getIngredients();
    		builder.append(counter);
    		builder.append(". ");
    		for (String ingredient : ingredients) {
    			builder.append(ingredient);
    			builder.append(",");
    		}
    		counter++;
    		builder.append("\n");
    	}
    	
    	return builder.toString();
    }

    public String readFromJPEG() {
    	return "";
    }
    
    @GetMapping(path="/getfooddetails")
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "You have entered " + food + "\n";
    		foodRepository.findAll().forEach(new Consumer<Food>() {
    		    public void accept(Food fd) {
    		        if(fd.getName().substring(0,fd.getName().indexOf(",")).equalsIgnoreCase(food)) { 
    		        		resultFood.concat(fd.getDetails() + "\n");
    		        }
    		    }
    		});
    		
    		return resultFood;
    }
    
}
