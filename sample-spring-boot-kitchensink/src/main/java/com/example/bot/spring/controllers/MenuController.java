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
	
	private long userID;
	private String menu;
	
	public long getUserID() {
		return userID;
	}

	public void setUserID(String id) {
		userID = profileRepository.findByUserID(id).getID();
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
		List<Set<Long>> result = new ArrayList<Set<Long>>();
    	for(int i=0;i<choices.length;i++) {
    		Set<Long> foodIDs = generateFoodIDs(choices[i]);
    		result.add(foodIDs);
    	}
    	
    	//Get Meals Eaten Food IDs from today (Daily progress?)
    	
    	//Get Weight (Daily progress?)
    	
    	//Get nutrients needed today (Daily progress?)
    	
    	//Check each nutrient
    	
		//Get FoodIDs from past few days
		Set<Long> pastFoodIDs = getFoodIDsFromPastMeals();
		System.out.println("HERE");
    	//Check if eaten
		for(int i=0;i<choices.length;i++) {
    		for(long id : result.get(i)) {
    			if(pastFoodIDs.contains(id)) {
    				scores[i][0] += ", " + foodRepository.findByFoodID(id).getName();
    			}
    		}
    	}
    	
    	//Get Interests
    	
    	//Check if interests align
    	
    	//Loop through each food
    	for(int i=0;i<choices.length;i++) {
    		for(long s : result.get(i)) {
    			
    		}
    	}
    	
    	//Calculate Score
    	for(int i=0;i<choices.length;i++) {
        	String[] items = scores[i][0].split(",", -1);
        	finalScore[i] -= items.length;
    	}
    	int max = finalScore[0];
    	int finalChoice = 0;
    	for(int i=0;i<choices.length;i++) {
    		if(max < finalScore[i]) {
    			max = finalScore[i];
    			finalChoice = i;
    		}
    	}

    	//Generate reply
    	String reply = new String();
    	if(scores[finalChoice][0]!="") {
    		reply += "I know that you have eaten "+scores[finalChoice][0].substring(2)+" in the past few days.";
    	}
    	if(reply!="") {
    		reply += "But I still ";
    	}
    	else {
    		reply += "I ";
    	}
    	reply += "recommend you to choose "+choices[finalChoice]+" because ";
    	
    	return reply;
	}
	
	@GetMapping(path="/generatefoodids")
	public @ResponseBody Set<Long> generateFoodIDs(@RequestParam String meal) {
    	Set<Long> foodIds = new HashSet<Long>();
        int j=0;
        for(Food fd : foodRepository.findAll()) {
        	String fdName = fd.getName().toLowerCase();
        	if(fdName.contains(",")) {
        		fdName = fdName.substring(0,fdName.indexOf(","));
        	}
        	if(fdName.endsWith("s")) {
            	fdName = fdName.substring(0, fdName.length()-1);
            }
        	if(meal.toLowerCase().contains(fdName)) { 
    	        foodIds.add(fd.getFoodID());
      		    j++;
   	        }   
       	}
        	
    	return foodIds;
	}
	
	@GetMapping(path="/getpastmeals")
	public @ResponseBody Set<Long> getFoodIDsFromPastMeals(){
		Set<Long> foodIds = new HashSet<Long>();
		for(Meal ml : mealRepository.findAll()) {
			if(ml.getUserID().equals(userID)) { 
	        		Date threeDaysAgo = new Date(System.currentTimeMillis()-(3*24*60*60*1000));
	        		Date mealTime = new Date(ml.getTime().getTime());
	        		if(mealTime.after(threeDaysAgo)) {
	        			foodIds.addAll(generateFoodIDs(ml.getFood()));
	        		}
	        }
		}
		return foodIds;
	}
}