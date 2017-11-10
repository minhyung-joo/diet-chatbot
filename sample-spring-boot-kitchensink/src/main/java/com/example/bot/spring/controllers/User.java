package com.example.bot.spring.controllers;
import java.util.function.Consumer;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.example.bot.spring.tables.*;

import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller// This means that this class is a Controller
@RequestMapping(path="/user")
public class User {
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private WeightRepository weightRepository;

	@Autowired
	private MealRepository mealRepository;

	@Autowired
	private RecommendationRepository recommendationRepository;

	
	@GetMapping(path="/createuser")
	public @ResponseBody void addUser (@RequestParam String id) {
		boolean userFound = false;
		for(Profile pf : profileRepository.findAll()) {
			if(pf.getUserID().equals(id)) { 
	        		userFound = true;
	        }
		}
		if (!userFound) {
			Profile pf = new Profile();
			pf.setUserID(id);
			pf.setTime();
			profileRepository.save(pf);
		}
	}
	
	public void setInterests(String[] Categories) {
		
	}
	
	@GetMapping(path="/inputweight")
	public @ResponseBody void inputWeight (@RequestParam String id, @RequestParam Double weight) {		
		Weight wt = new Weight();
		wt.setUserID(id);
		wt.setTime();
		wt.setWeight(weight);
		weightRepository.save(wt);
		
	}
	
	@GetMapping(path="/getWeights")
	public @ResponseBody String outputWeight (@RequestParam String id) {		
		boolean weightFound = false;
		String outputStr = "";
		for(Weight wt : weightRepository.findAll()) {
			if(wt.getUserID().equals(id)) { 
	        		weightFound = true;
	        		
	        		Date date = new Date();
	        		date.setTime(wt.getTime().getTime());
	        		
	        		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
	        		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
	        		String formattedDate = simpleDateFormat.format(date);
	        		if (!outputStr.equals("")) {
	        			outputStr += "\n";
	        		}
	        		outputStr += formattedDate + ":" + "\n" + wt.getWeight() + "kg";
	        }
		}
		if (!weightFound) {
			outputStr += "You did not log any weight";
		}
		
		return outputStr;
	}
	
	@GetMapping(path="/inputmeal")
	public @ResponseBody void inputMeal (@RequestParam String id, @RequestParam String food) {		
		Meal ml = new Meal();
		ml.setUserID(id);
		ml.setTime();
		ml.setFood(food);
		mealRepository.save(ml);	
	}
	
	@GetMapping(path="/getMeals")
	public @ResponseBody String outputMeal (@RequestParam String id) {		
		boolean mealFound = false;
		String outputStr = "";
		for(Meal ml : mealRepository.findAll()) {
			if(ml.getUserID().equals(id)) { 
	        		mealFound = true;
	        		
	        		Date date = new Date();
	        		date.setTime(ml.getTime().getTime());
	        		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	        		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
	        		String formattedDate = simpleDateFormat.format(date);
	        		if (!outputStr.equals("")) {
	        			outputStr += "\n";
	        		}
	        		outputStr += formattedDate + ":" + "\n" + ml.getFood();
	        }
		}
		if (!mealFound) {
			outputStr += "You did not log any meal";
		}
		
		return outputStr;
	}
	
	@GetMapping(path="/makeRecommendation")
	public @ResponseBody String makeRecommendation (@RequestParam String id) {		
		Recommendation rd = new Recommendation();
		rd.setUserID(id);
		rd.setUniqueCode(makeUniqueCode("123456"));
		rd.setClaimed(false);
		recommendationRepository.save(rd);	
		return rd.getUniqueCode();
	}
	
	@GetMapping(path="/acceptRecommendation")
	public @ResponseBody boolean acceptRecommendation (@RequestParam String uniqueCode, @RequestParam String userID) {		
		Recommendation rd = recommendationRepository.findByUniqueCode(uniqueCode);
		if (!rd.getClaimed()) {
//			if (!rd.getUserID().equals(userID)) {
				rd.setClaimed(true);
				recommendationRepository.save(rd);
				return true;
//			}
			else {
				
			}
		}
		else {
			
		}
		return false;
	}
	
	public String makeUniqueCode(String code) {
		return code;
	}
	
	
	
	
	
}