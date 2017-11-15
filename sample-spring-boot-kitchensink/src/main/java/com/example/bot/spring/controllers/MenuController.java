package com.example.bot.spring.controllers;

import com.example.bot.spring.tables.*;
import com.example.bot.spring.controllers.User;

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
	
	@Autowired
	private User user;
	
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
		//0 - eaten
		//1 - interest
		//2 - calories
		//3 - protein
		//4 - carbohydrate
		//5 - fat
		int[] finalScore = new int[choices.length];
		
		//Get Food IDs from Menu
		List<Set<Food>> result = new ArrayList<Set<Food>>();
    	for(int i=0;i<choices.length;i++) {
    		Set<Food> foods = generateFoods(choices[i]);
    		result.add(foods);
    	}
    	
    	double minimum = Double.MAX_VALUE;
    	int minIndex = -1;
    	double difference = 0;
    	//Check calories
		for(int i=0;i<choices.length;i++) {
    		double totalCalories = 0; 
    		minimum = Double.MAX_VALUE;
    		minIndex = -1;
    		for(Food f : result.get(i)) {
    			totalCalories += f.getCalories();
    		}
    		if(totalCalories>user.getRemainingCalories(userID)) {
    			scores[i][2] = "Over";
    		}
    		if(scores[i][2]!=null && !scores[i][2].isEmpty()) {
    			difference = Math.abs(totalCalories - user.getBMR(userID)/3.0);
    			if(difference<minimum) {
    				minimum = difference;
    				minIndex = i;
    			}
    		}
    	}
		if(minIndex!=-1) {
	    	scores[minIndex][2] = "calories";
		}
		
		//Check protein
		for(int i=0;i<choices.length;i++) {
    		double totalProtein = 0; 
    		minimum = Double.MAX_VALUE;
    		minIndex = -1;
    		for(Food f : result.get(i)) {
    			totalProtein += f.getProtein();
    		}
    		if(totalProtein>user.getRemainingProtein(userID)) {
    			scores[i][3] = "Over";
    		}
    		if(scores[i][3]!=null && !scores[i][3].isEmpty()) {
    			difference = Math.abs(totalProtein - user.getBMR(userID)*0.2/4.0/3.0);
    			if(difference<minimum) {
    				minimum = difference;
    				minIndex = i;
    			}
    		}
    	}
		if(minIndex!=-1) {
	    	scores[minIndex][3] = "protein";
		}
		
		//Check carbohydrate
		for(int i=0;i<choices.length;i++) {
    		double totalCarbohydrate = 0; 
    		minimum = Double.MAX_VALUE;
    		minIndex = -1;
    		for(Food f : result.get(i)) {
    			totalCarbohydrate += f.getCarbohydrate();
    		}
    		if(totalCarbohydrate>user.getRemainingCarbohydrate(userID)) {
    			scores[i][4] = "Over";
    		}
    		if(scores[i][4]!=null && !scores[i][4].isEmpty()) {
    			difference = Math.abs(totalCarbohydrate - user.getBMR(userID)*0.55/4.0/3.0);
    			if(difference<minimum) {
    				minimum = difference;
    				minIndex = i;
    			}
    		}
    	}
		if(minIndex!=-1) {
	    	scores[minIndex][4] = "carbohydrate";
		}
		
		//Check fat
		for(int i=0;i<choices.length;i++) {
	  		double totalFat = 0; 
    		minimum = Double.MAX_VALUE;
    		minIndex = -1;
    		for(Food f : result.get(i)) {
    			totalFat += f.getSaturatedFat();
    		}
    		if(totalFat>user.getRemainingFat(userID)) {
    			scores[i][5] = "Over";
    		}
    		if(scores[i][5]!=null && !scores[i][5].isEmpty()) {
    			difference = Math.abs(totalFat - user.getBMR(userID)*0.25/9.0/3.0);
	    		if(difference<minimum) {
	    			minimum = difference;
		    		minIndex = i;
		    	}
		    }
		}
		if(minIndex!=-1) {
		    scores[minIndex][5] = "fat";
		}
    	
		//Get FoodIDs from past few days
		Set<Food> pastFoods = getFoodsFromPastMeals();
		
    	//Check if eaten
		if(!pastFoods.isEmpty()) {
			for(int i=0;i<choices.length;i++) {
				StringBuilder builder = new StringBuilder();
	    		for(Food fd : result.get(i)) {
	    			for(Food pastFd : pastFoods) {
		    			if(pastFd.getFoodID() == fd.getFoodID()) {
		    				if (builder.length() != 0) {
		    			        builder.append(", ");
		    				}
		    				String fdName = processFoodName(fd.getName().toLowerCase());
		    				builder.append(fdName);
		    			}
	    			}
	    		}
	    		scores[i][0] = "";
	    		scores[i][0] += builder;
	    	}
		}
		
    	//Get Interests
    	String[] interests = profileRepository.findByUserID(userID).getInterests();
    	
    	//Check if interests align
		if(interests != null) {
	    	for(int i=0;i<choices.length;i++) {
    			StringBuilder builder = new StringBuilder();
	    		for(Food fd : result.get(i)) {
	    	    	for(int j=0;j<interests.length;j++) {
	    	    		if(interests[j].equals(fd.getCategory())) {
	    	    			if (builder.length() != 0) {
	        			        builder.append(", ");
	    	    			}
	    	    			builder.append(interests[j]);
	    	    		}
	    	    	}
	    		}
	    		scores[i][1] = "";
    	    	scores[i][1] += builder;
	    	}
		}
		
    	//Calculate Score
    	finalScore = calculateScores(scores);
		
    	//Find Max
    	int finalChoice = findMax(finalScore);
    	
    	return generateReply(scores[finalChoice], choices[finalChoice]);
	}
	
	private String processFoodName(String fdName) {
		if(fdName.contains(",")) {
    		fdName = fdName.substring(0,fdName.indexOf(","));
    	}
    	return fdName;
	}
	
	public Set<Food> generateFoods(String meal) {
		int size = 0;
		Set<String> foodNames = new HashSet<String>();
    	Set<Food> foods = new HashSet<Food>();
        for(Food fd : foodRepository.findAll()) {
        	String fdName = processFoodName(fd.getName().toLowerCase());
        	if(fdName.endsWith("s")) {
            	fdName = fdName.substring(0, fdName.length()-1);
            }
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
	
	private Set<Food> getFoodsFromPastMeals(){
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
    		}
    		if(scores[i][1]!=null && !scores[i][1].isEmpty()) {
            	String[] items = scores[i][1].split(",", -1);
            	finalScore[i] += items.length;
    		}
    		for(int j=2;j<scores[i].length;j++) {
    			if(scores[i][j]!=null && !scores[i][j].isEmpty()) {
        			if(scores[i][j].equals("over")) {
        				finalScore[i] -= 3;
        			}
        			else if(!scores[i][j].equals("")) {
        				finalScore[i] += 1;
        			}
        		}
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
    		reply += "I know that you have eaten "+scores[0]+" in the past few days.";
    	}
    	if(reply!=null && !reply.isEmpty()) {
    		reply += " But I still ";
    	}
    	else {
    		reply += "I ";
    	}
    	reply += "recommend you to choose "+finalChoice;
    	if(scores[1]!=null && !scores[1].isEmpty()) {
    		Set<String> interests = new HashSet<String>();
    		String[] items = scores[1].split(", ", -1);
    		for(int i=0;i<items.length;i++) {
    			interests.add(items[i]);
    		}
    		reply += " because I know that you like foods that are ";
    		StringBuilder builder = new StringBuilder();
    		for(String s : interests) {
    			 if (builder.length() != 0) {
    			        builder.append(", ");
    			 }
    			 builder.append(s.toLowerCase());
    		}
    		reply += builder;
    	}
    	
    	StringBuilder builder2 = new StringBuilder();
    	for(int i=2;i<scores.length;i++) {
    		if(!scores[i].equals("")) {
    			if (builder2.length() != 0) {
			        builder2.append(", ");
    			}
    			builder2.append(scores[i]);
    		}
    	}
    	reply += ".";
    	if(builder2.length() != 0) {
    		reply += "  This choice has the most suitable amount of " + builder2 + ".";
    	}
    	return reply;
	}
}