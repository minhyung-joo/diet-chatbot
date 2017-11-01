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
	
	@GetMapping(path="/readfromtext")
    public @ResponseBody String readFromText(@RequestParam String text) {
		String[] menu = text.split(System.getProperty("line.separator"));
    	String[][] result = new String[menu.length][50];
    	for(int i=0;i<menu.length;i++) {
        	int j=0;
        	for(Food fd : foodRepository.findAll()) {
        		if(menu[i].contains(fd.getName())) { 
    	        	result[i][j] = fd.getName();
       		    	j++;
   		        }   
       		}	 
    	}
    	String resultText = "The Foods in each entree are as followed:\n";
    	for(int i=0;i<menu.length;i++) {
    		resultText += (i+1)+". ";
    		for(int j=0;j<result[i].length;j++) {
    			if (result[i][j] == null)
                    break;
    			resultText += result[i][j] + ", ";
    		}
    		resultText += "\n";
    	}
    	return resultText;
    }

    public String readFromJSON(String url) {
    	return "";
    }

    public String readFromJPEG() {
    	return "";
    }
    
    @GetMapping(path="/getfooddetails")
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "\"You have entered \" + food + \"\\n\"";
    		foodRepository.findAll().forEach(new Consumer<Food>() {
    		    public void accept(Food fd) {
    		        if(fd.getName().equalsIgnoreCase(food)) { 
    		        		resultFood.concat(fd.getDetails() + "\n");
    		        }
    		    }
    		});
    		
    		return resultFood;
    }
    
}
