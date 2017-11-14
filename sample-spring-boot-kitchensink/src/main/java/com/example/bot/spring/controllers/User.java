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
import java.io.ByteArrayOutputStream;
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
	
	@GetMapping(path="/inputgender")
	public @ResponseBody void inputGender (@RequestParam String id, @RequestParam String gender) {
		Profile pf = profileRepository.findByUserID(id)
		pf.setGender(gender);
		profileRepository.save(pf);
	}
	
	@GetMapping(path="/inputage")
	public @ResponseBody void inputAge (@RequestParam String id, @RequestParam int age) {
		Profile pf = profileRepository.findByUserID(id)
		pf.setAge(age);
		profileRepository.save(pf);
	}
	
	@GetMapping(path="/inputheight")
	public @ResponseBody void inputHeight (@RequestParam String id, @RequestParam Double height) {
		Profile pf = profileRepository.findByUserID(id)
		pf.setHeight(height);
		profileRepository.save(pf);
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
		if (rd!=null) {
			if (!rd.getClaimed()) {
				if (!rd.getUserID().equals(userID)) {
					rd.setClaimed(true);
					recommendationRepository.save(rd);
					
					Profile pf = profileRepository.findByUserID(userID);
					pf.setClaimedNewUserCoupon(true);
					profileRepository.save(pf);
					return rd.getUserID();
				}
				
				else {
					//the recommender entered the code
					return "recommender";
				}
			}
			else {
				//already claimed
				return "claimed";
			}
		}
		else {
			//no such code
			return "none";

		}
	}
	
	public String makeUniqueCode(String code) {
		return code;
	}
	
	@GetMapping(path="/uploadCouponCampaign")
	public @ResponseBody void uploadCouponCampaign (@RequestParam InputStream is) {		
		
		Campaign campaign = null;
		for(Campaign cp : campaignRepository.findAll()) {
			campaign = cp;
		}
		
		if (campaign == null) {
			campaign = new Campaign();
			campaign.setTime();
		}
		
		try {
			campaign.setCouponImage(readImage(is));
		} catch (IOException e) {
			
		}
		campaignRepository.save(campaign);	
	}
	
	@GetMapping(path="/getCoupon")
	public @ResponseBody byte [] getCoupon () {	
		Campaign campaign;
		for(Campaign cp : campaignRepository.findAll()) {
			cp.incrementCount();
			campaignRepository.save(cp);
			return cp.getCouponImage();
		}
		return null;
	}
	
	@GetMapping(path="/checkValidity")
	public @ResponseBody boolean checkValidityOfUser (String id) {	
		Profile pf = profileRepository.findByUserID(id);
		if (pf.getClaimedNewUserCoupon()) {
			//already claimed new user coupon
			return false;
		}
		
		Campaign campaign=null;
		for(Campaign cp : campaignRepository.findAll()) {
			campaign = cp;
		}
		
		if (campaign != null) {
			if (campaign.getCount()>=5000) {
				//5000 coupons already taken
				return false;
			}
			if (campaign.getTime().getTime()>pf.getRegisteredTime().getTime()) {
				//user registered before campaign began
				return false;
			}
			else {
				return true;
			}	
		}
		else {
			//no campaign
			return false;
		}
		
		
	}
	
	@GetMapping(path="/isAdmin")
	public @ResponseBody boolean isAdmin (String userID) {	
		return true;
	}
	
	
	
	public byte[] readImage(InputStream is) throws IOException
	{
	    byte[] buffer = new byte[8192];
	    int bytesRead;
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    while ((bytesRead = is.read(buffer)) != -1)
	    {
	        output.write(buffer, 0, bytesRead);
	    }
	    return output.toByteArray();

	}
	
}