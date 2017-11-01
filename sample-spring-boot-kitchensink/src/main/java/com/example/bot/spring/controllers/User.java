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
public class User {
	@Autowired
	private ProfileRepository profileRepository;
	private Profile profile;
	
	public User (String id) {
		profileRepository.findAll().forEach(new Consumer<Profile>() {
		    public void accept(Profile pf) {
		        if(pf.getUserID().equals(id)) { 
		        		profile = pf;
		        }
		    }
		});
		if (profile ==null) {
			Profile pf = new Profile(id);
			profileRepository.save(pf);
			profile = pf;
		}
	}
	
	public void setInterests(String[] Categories) {
		
	}
	
	public void inputWeight(int weight) {
		
	}
	
	public void inputMeal(String food) {
		
	}
	
	public void createProfile() {
	}
	
	public void showProfile() {
		
	}
	
	
	
}