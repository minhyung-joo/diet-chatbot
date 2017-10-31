package com.example.bot.spring.classes;
import com.example.bot.spring.tables.Profile;
import com.example.bot.spring.tables.ProfileRepository;


public class User {
	
	public long userID;
		
	public void setInterests(String[] Categories) {
		
	}
	
	public void inputWeight(int weight) {
		
	}
	
	public void inputMeal(String food) {
		
	}
	
	public void createProfile() {
		Profile p1 = new Profile();
		ProfileRepository repo = (ProfileRepository) SpringApplication.run(Application.class, args).
		getBean("profileRepository");
		
		repo.save(p1);
	}
	
	public void showProfile() {
		Profile p1 = repo.findById(userID);
		
	}
	
	
	
}