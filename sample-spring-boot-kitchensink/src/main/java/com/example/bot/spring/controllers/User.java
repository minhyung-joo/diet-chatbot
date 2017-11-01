package com.example.bot.spring.controllers;
import java.util.function.Consumer;
import com.example.bot.spring.tables.*;

import java.util.function.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/user")
public class User {
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private WeightRepository weightRepository;

	
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
	public @ResponseBody void inputWeight (@RequestParam String id) {		
		boolean weightFound = false;
		String outputStr = "";
		for(Weight wt : weightRepository.findAll()) {
			if(wt.getUserID().equals(id)) { 
	        		weightFound = true;
	        		outputStr += "This is your weight" + wt.getWeight() + "\n";
	        }
		}
		if (!weightFound) {
			outputStr += "You did not log any weight";
		}
		
	}

	
	public void inputMeal(String food) {
		
	}
	
	public void createProfile() {
	}
	
	public void showProfile() {
		
	}
	
	
	
}