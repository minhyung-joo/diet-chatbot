package com.example.bot.spring.controllers;
import com.example.bot.spring.tables.*;
import java.util.*;
import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
        		if(menu[i].contains(fd.getName())) { 
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
    	return "";
    }

    public String readFromJPEG() {
    	return "";
    }
    
    @GetMapping(path="/getfooddetails")
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "You have entered " + food + "\n";
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
