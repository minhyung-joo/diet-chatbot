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
import com.example.bot.spring.models.Menu;
import net.sourceforge.tess4j.*;

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
        		String fdName = fd.getName().toLowerCase();
        		if(fdName.contains(",")) {
        			fdName = fdName.substring(0,fdName.indexOf(","));
        		}
        		if(fdName.endsWith("s")) {
            		fdName = fdName.substring(0, fdName.length()-1);
            	}
        		if(menu[i].toLowerCase().contains(fdName)) { 
    	        	names.add(fdName);
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
    	ITesseract tess = new Tesseract();
    	
    	return "";
    }
    
    @GetMapping(path="/getfooddetails")
    public @ResponseBody String getFoodDetails(@RequestParam String food) {
    		String resultFood = "You have entered " + food + "\n";
    		String[] splitFood = food.split("\\s+");
    		
	    	for (int i = 0; i < splitFood.length; i++) {	
	    		for (Food fd : foodRepository.findAll()) {
	    		    String fdName = fd.getName().toLowerCase();
	            	if (fdName.contains(",")) {
	            		fdName = fdName.substring(0,fdName.indexOf(","));
	            	}
	            	
	            	if (fdName.endsWith("s")) {
	            		fdName = fdName.substring(0, fdName.length()-1);
	            	}
	            	
	    		    if (checkEquality(fdName, splitFood[i]) || splitFood[i].toLowerCase().contains(fdName)) { 
	    		    		resultFood += "Here are the details for " + fdName + "\n" + fd.getDetails() + "\n" + "\n";
	    		    		break;
	    		    }
	    		}
	    	}
    		
    		return resultFood;
    }
    
    private boolean checkEquality(String s1, String s2) {
    	return getDistance(s1, s2) <= 2;
    }
    
    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    /**
     * Using Dynamic Programming, the Wagner-Fischer algorithm is able to 
     * calculate the edit distance between two strings.
     * @return edit distance between s1 and s2
     */
    private int getDistance(String str1, String str2) {
    	char[] s1 = str1.toCharArray();
    	char[] s2 = str2.toCharArray();
    	
        int[][] dp = new int[s1.length + 1][s2.length + 1];
        for (int i = 0; i <= s1.length; dp[i][0] = i++);
        for (int j = 0; j <= s2.length; dp[0][j] = j++);

        for (int i = 1; i <= s1.length; i++) {
            for (int j = 1; j <= s2.length; j++) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, 
                    		dp[i - 1][j - 1] + 1);
                }
            }
        }
        return dp[s1.length][s2.length];
    }
}
