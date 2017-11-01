package com.example.bot.spring.controllers;
import java.util.function.Consumer;
import com.example.bot.spring.tables.*;

import sun.java2d.cmm.Profile;

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
	
	public void inputWeight(int weight) {
		
	}
	
	public void inputMeal(String food) {
		
	}
	
	public void createProfile() {
	}
	
	public void showProfile() {
		
	}
	
	
	
}