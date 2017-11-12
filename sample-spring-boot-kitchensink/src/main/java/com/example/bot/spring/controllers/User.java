package com.example.bot.spring.controllers;
import java.util.function.Consumer;
import java.util.Date;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import com.example.bot.spring.tables.*;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

	@Autowired
	private CampaignRepository campaignRepository;
	
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
	
	@GetMapping(path="/inputinterest")
	public @ResponseBody void inputInterest (@RequestParam String id, @RequestParam String interest) {	
		
		String[] splitInterest = interest.split(", ");
		
		for(Profile pf : profileRepository.findAll()) {
			if(pf.getUserID().equals(id)) { 
				pf.setInterest(splitInterest);
				profileRepository.save(pf);
	        }
		}
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
	public @ResponseBody String acceptRecommendation (@RequestParam String uniqueCode, @RequestParam String userID) {		
		Recommendation rd = recommendationRepository.findByUniqueCode(uniqueCode);
		if (!rd.getClaimed()) {
			if (!rd.getUserID().equals(userID)) {
				rd.setClaimed(true);
				recommendationRepository.save(rd);
				return rd.getUserID();
			}
			else {
				
			}
		}
		else {
			
		}
		return "";
	}
	
	public String makeUniqueCode(String code) {
		return code;
	}
	
	@GetMapping(path="/makeCampaign")
	public @ResponseBody void makeCampaign (@RequestParam InputStream is) {		
		Campaign cp = new Campaign();
		cp.setTime();
		try {
			cp.setCouponImage(readImageOldWay(is));
		} catch (IOException e) {
			
		}
		campaignRepository.save(cp);	
	}
	
	@GetMapping(path="/getCoupon")
	public @ResponseBody byte [] getCoupon () {	
		Campaign campaign;
		for(Campaign cp : campaignRepository.findAll()) {
			return cp.getCouponImage();
		}
		return null;
	}
	
	
	public byte[] readImageOldWay(InputStream is) throws IOException
	{
	  long length = is.available();
	  // Create the byte array to hold the data
	  byte[] bytes = new byte[(int) length];
	  // Read in the bytes
	  int offset = 0;
	  int numRead = 0;
	  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
	  {
	    offset += numRead;
	  }
	  // Ensure all the bytes have been read in
	  if (offset < bytes.length)
	  {
	    throw new IOException("Could not completely read file ");
	  }
	  // Close the input stream and return bytes
	  is.close();
	  return bytes;
	}
	
}