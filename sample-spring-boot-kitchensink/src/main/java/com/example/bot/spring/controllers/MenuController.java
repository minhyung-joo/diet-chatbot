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
@RequestMapping(path="/menu")
public class MenuController{
	
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private WeightRepository weightRepository;

	@Autowired
	private MealRepository mealRepository;
	
	@Autowired
	private FoodRepository foodRepository;
	
	private String userID;
	private String menu;
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String id) {
		userID = id;
	}
	
	public String getMenu() {
		return menu;
	}
	
	public void setMenu(String menuList) {
		menu = menuList;
	}
	
	@GetMapping(path="/pickfood")
	public @ResponseBody String pickFood() {
		String[] choices = menu.split(System.getProperty("line.separator"));
		
		//For scoring and generating reply based on reasons
		String[][] scores = new String[choices.length][10];
		int[] finalScore = new int[choices.length];
		
		//Get Food IDs from Menu
		List<Set<Food>> result = new ArrayList<Set<Food>>();
    	for(int i=0;i<choices.length;i++) {
    		Set<Food> foods = generateFoods(choices[i]);
    		result.add(foods);
    	}
    	
    	//Get Meals Eaten Food IDs from today (Daily progress?)
    	
    	//Get Weight (Daily progress?)
    	
    	//Get nutrients needed today (Daily progress?)
    	
    	//Check each nutrient
    	
		//Get FoodIDs from past few days
		Set<Food> pastFoods = getFoodsFromPastMeals();
		
    	//Check if eaten
		if(!pastFoods.isEmpty()) {
			for(int i=0;i<choices.length;i++) {
	    		for(Food fd : result.get(i)) {
	    			for(Food pastFd : pastFoods) {
		    			if(pastFd.getFoodID() == fd.getFoodID()) {
		    				fdName = processFoodName(fd.getName().toLowerCase());
		    				scores[i][0] += ", " + fdName;
		    			}
	    			}
	    		}
	    	}
		}
		
    	//Get Interests
    	String[] interests = profileRepository.findByUserID(userID).getInterests();
    	
    	//Check if interests align
		if(interests != null) {
	    	for(int i=0;i<choices.length;i++) {
	    		for(Food fd : result.get(i)) {
	    	    	for(int j=0;j<interests.length;j++) {
	    	    		if(interests[j].equals(fd.getCategory())) {
	    	    			scores[i][1] += ", " + interests[j];
	    	    		}
	    	    	}
	    		}
	    	}
		}

    	//Loop through each food
    	for(int i=0;i<choices.length;i++) {
    		for(Food f : result.get(i)) {
    			
    		}
    	}
		
    	//Calculate Score
    	finalScore = calculateScores(scores);
		
    	//Find Max
    	int finalChoice = findMax(finalScore);
    	
    	return generateReply(scores[finalChoice], choices[finalChoice]);
	}
	
	private processFoodName(String fdName) {
		if(fdName.contains(",")) {
    		fdName = fdName.substring(0,fdName.indexOf(","));
    	}
    	if(fdName.endsWith("s")) {
        	fdName = fdName.substring(0, fdName.length()-1);
        }
	}
	
	@GetMapping(path="/generatefoods")
	public @ResponseBody Set<Food> generateFoods(@RequestParam String meal) {
		int size = 0;
		Set<String> foodNames = new HashSet<String>();
    	Set<Food> foods = new HashSet<Food>();
        for(Food fd : foodRepository.findAll()) {
        	fdName = processFoodName(fd.getName().toLowerCase());
        	if(meal.toLowerCase().contains(fdName)) { 
        		foodNames.add(fdName);
        		if(size<foodNames.size()) {
        	        foods.add(fd);
        	        size++;
        		}
   	        }   
       	}
        
    	return foods;
	}
	
	@GetMapping(path="/getpastmeals")
	public @ResponseBody Set<Food> getFoodsFromPastMeals(){
		Set<Food> foods = new HashSet<Food>();
		for(Meal ml : mealRepository.findAll()) {
			if(ml.getUserID().equals(userID)) { 
	        		Date threeDaysAgo = new Date(System.currentTimeMillis()-(3*24*60*60*1000));
	        		Date mealTime = new Date(ml.getTime().getTime());
	        		if(mealTime.after(threeDaysAgo)) {
	        			foods.addAll(generateFoods(ml.getFood()));
	        		}
	        }
		}
		return foods;
	}
	
	private int[] calculateScores(String[][] scores) {
		int[] finalScore = new int[scores.length];
		for(int i=0;i<scores.length;i++) {
    		if(scores[i][0]!=null && !scores[i][0].isEmpty()) {
            	String[] items = scores[i][0].split(",", -1);
            	finalScore[i] -= items.length;
            	System.out.println(i+ " meals:" + items.length);
    		}
    		if(scores[i][1]!=null && !scores[i][1].isEmpty()) {
            	String[] items = scores[i][1].split(",", -1);
            	finalScore[i] += items.length;
            	System.out.println(i + " interests:" + items.length);
    		}
    	}
		return finalScore;
	}
	
	private int findMax(int[] finalScore) {
		int max = finalScore[0];
    	int finalChoice = 0;
    	for(int i=0;i<finalScore.length;i++) {
    		if(max < finalScore[i]) {
    			max = finalScore[i];
    			finalChoice = i;
    		}
    	}
    	return finalChoice;
	}
	
	private String generateReply(String[] scores, String finalChoice) {
		String reply = new String();
    	if(scores[0]!=null && !scores[0].isEmpty()) {
    		reply += "I know that you have eaten "+scores[0].substring(2)+" in the past few days.";
    	}
    	if(reply!=null && !reply.isEmpty()) {
    		reply += " But I still ";
    	}
    	else {
    		reply += "I ";
    	}
    	reply += "recommend you to choose "+finalChoice+" because ";
    	if(scores[1]!=null && !scores[1].isEmpty()) {
    		reply += "I know that you like foods that are "+scores[1].substring(2)+ ".";
    	}
    	return reply;
	}
}