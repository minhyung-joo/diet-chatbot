package com.example.bot.spring.controllers;
import java.util.function.Consumer;
import java.util.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import com.example.bot.spring.tables.*;
import com.example.bot.spring.controllers.MenuController;

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
import org.springframework.stereotype.Service;

@Service
public class User {
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private WeightRepository weightRepository;

	@Autowired
	private MealRepository mealRepository;
	
	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private RecommendationRepository recommendationRepository;

	@Autowired
	private CampaignRepository campaignRepository;
	
	@Autowired
	private MenuController mc;
	
	public void addUser(String id) {
		
		Profile pf = profileRepository.findByUserID(id);
		if (pf == null) {
			pf = new Profile();
			pf.setUserID(id);
			pf.setTime();
			if (profileRepository.count() == 0) {
				pf.setAdmin(true);
			}
			else {
				pf.setAdmin(false);
			}
			profileRepository.save(pf);
		}
	}
	
	public void inputGender(String id, String gender) {
		Profile pf = profileRepository.findByUserID(id);
		pf.setGender(gender);
		profileRepository.save(pf);
	}
	
	public void inputAge(String id, int age) {
		Profile pf = profileRepository.findByUserID(id);
		pf.setAge(age);
		profileRepository.save(pf);
	}
	
	public void inputHeight(String id, Double height) {
		Profile pf = profileRepository.findByUserID(id);
		pf.setHeight(height);
		profileRepository.save(pf);
	}
	
	public void inputWeight(String id, Double weight) {		
		Weight wt = new Weight();
		wt.setUserID(id);
		wt.setTime();
		wt.setWeight(weight);
		weightRepository.save(wt);
	}
	
	public String outputWeight(String id) {		
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
	
	public void inputInterest(String id, String interest) {	
		
		String[] splitInterest = interest.split(", ");
		
		for(Profile pf : profileRepository.findAll()) {
			if(pf.getUserID().equals(id)) { 
				pf.setInterest(splitInterest);
				profileRepository.save(pf);
	        }
		}
	}
	
	public String outputGeneral(String id) {		
		Profile pf = profileRepository.findByUserID(id);
		String outputStr = "Gender: ";
		Integer age = pf.getAge();
		String gender = pf.getGender();
		Double height = pf.getHeight();
		if(pf.getGender()=="Female") {
			outputStr += "Female\n";
		}
		else {
			outputStr += "Male\n";
		}
		outputStr += "Age: ";
		if(age == null) {
			outputStr += "44\n";
		}
		else {
			outputStr += age.toString()+"\n";
		}
		outputStr += "Height: ";
		if(height == null) {
			outputStr += "177cm\n\n";
		}
		else {
			outputStr += height.toString()+"\n\n";
		}
		return outputStr;
	}
	
	public String outputInterest(String id) {
		boolean interestFound = false;
		String outputStr = "";
		Profile pf = profileRepository.findByUserID(id);
		if(pf.getInterests() != null) {
			interestFound = true;
			outputStr += "Your interests in food are: \n";
			for(int i=0; i<pf.getInterests().length; i++) {
				outputStr += pf.getInterests()[i] + "\n";
			}
			return outputStr;
		}
		outputStr += "You did not tell me your food interests yet.";		
		return outputStr;
	}
	
	public void inputMeal(String id, String food) {		
		Meal ml = new Meal();
		ml.setUserID(id);
		ml.setTime();
		ml.setFood(food);
		mealRepository.save(ml);	
	}
	
	public String outputMeal(String id) {		
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
	
	public String makeRecommendation(String id) {		
		Recommendation rd = new Recommendation();
		rd.setUserID(id);
		rd.setClaimed(false);
		recommendationRepository.save(rd);	
		return Long.toString(rd.getID());
	}
	
	public String acceptRecommendation(String uniqueCode, String userID) {		
		Recommendation rd = recommendationRepository.findById(Long.parseLong(uniqueCode));
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
		
	public void uploadCouponCampaign(InputStream is) {
		
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
	
	public byte[] getCoupon() {	
		Campaign campaign;
		for(Campaign cp : campaignRepository.findAll()) {
			cp.incrementCount();
			campaignRepository.save(cp);
			return cp.getCouponImage();
		}
		return null;
	}
	
	public String checkValidityOfUser (String id) {
		Profile pf = profileRepository.findByUserID(id);
		if (pf.getClaimedNewUserCoupon()) {
			
			return "claimed";
		}
		
		Campaign campaign=null;
		for(Campaign cp : campaignRepository.findAll()) {
			campaign = cp;
		}
		
		if (campaign != null) {
			if (campaign.getCount()>=5000) {
				//5000 coupons already taken
				return "taken";
			}
			if (campaign.getTime().getTime()>pf.getRegisteredTime().getTime()) {
				//user registered before campaign began
				return "before";
			}
			else {
				return "valid";
			}	
		}
		else {
			//no campaign
			return "none";
		}
		
		
	}
	
	public boolean isAdmin(String userID) {
		//TODO
		Profile pf = profileRepository.findByUserID(userID);
		if (pf !=null) {
			if (pf.getAdmin()) {
				return true;
			}
		}
		return false;
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
	
	private Double getLastWeight(String userID) {
		Date closest = new Date(0);
		Weight lastWeight = null;
		for(Weight wt : weightRepository.findAll()) {
			if(wt.getUserID().equals(userID)) { 
	        		Date weightTime = new Date(wt.getTime().getTime());
	        		if(weightTime.after(closest)) {
	        			closest = weightTime;
	        			lastWeight = wt;
	        		}
	        }
		}
		if(lastWeight == null) {
			return null;
		}
		else{
			return lastWeight.getWeight();
		}
	}
	
	public double getBMR(String userID) {		
		Profile pf = profileRepository.findByUserID(userID);
		Double weight = getLastWeight(userID);
		Double height = pf.getHeight();
		Integer age = pf.getAge();
		if(weight == null) {
			weight = 89.0;
		}
		if(height == null) {
			height = 177.0;
		}
		if(age == null) {
			age = 44;
		}
		double bmr = 10*weight + 6.25*height - 5*age;
		if(pf.getGender()=="Female") {
			bmr -= 161;
		}
		else {
			bmr += 5;
		}
		return bmr;
	}
	
	public double getBMI(String userID) {		
		Profile pf = profileRepository.findByUserID(userID);
		Double weight = getLastWeight(userID);
		Double height = pf.getHeight()/100.0;
		if(weight == null) {
			weight = 89.0;
		}
		if(height == null) {
			height = 1.77;
		}
		return weight/(height*height);
	}
	
	public String getBMICategory(String userID) {		
		double bmi = getBMI(userID);
		if(bmi<18.5) {
			return "Underweight";
		}
		else if(bmi<25) {
			return "Normal";
		}
		else if(bmi<30) {
			return "Overweight";
		}
		else {
			return "Obese";
		}
	}
	
	public double getBFP (String userID) {		
		Profile pf = profileRepository.findByUserID(userID);
		Integer age = pf.getAge();
		if(age == null) {
			age = 44;
		}
		double bfp = 1.2*getBMI(userID) + 0.23*age;
		if(pf.getGender()=="Female") {
			bfp -= 16.2;
		}
		else {
			bfp -= 5.4;
		}
		return bfp;
	}
	
	private Set<Food> getFoodsFromToday(String userID){
		Set<Food> foods = new HashSet<Food>();
		for(Meal ml : mealRepository.findAll()) {
			if(ml.getUserID().equals(userID)) { 
	        		Calendar today = Calendar.getInstance();
	        		Calendar mealTime = Calendar.getInstance();
	        		Date mealDate = new Date(ml.getTime().getTime());
	        		mealTime.setTime(mealDate);
	        		if((today.get(Calendar.ERA) == mealTime.get(Calendar.ERA) &&
	        			today.get(Calendar.YEAR) == mealTime.get(Calendar.YEAR) &&
	        			today.get(Calendar.DAY_OF_YEAR) == mealTime.get(Calendar.DAY_OF_YEAR))) {
	        			foods.addAll(mc.generateFoods(ml.getFood()));
	        		}
	        }
		}
		return foods;
	}
	
	public double getRemainingCalories(String userID) {		
		double currentCalories = 0;
		Set<Food> mealsToday = getFoodsFromToday(userID);
		for(Food fd : mealsToday) {
			currentCalories += fd.getCalories();
		}
		return getBMR(userID) - currentCalories;
	}
	
	public double getRemainingProtein(String userID) {		
		double currentProtein = 0;
		Set<Food> mealsToday = getFoodsFromToday(userID);
		for(Food fd : mealsToday) {
			currentProtein += fd.getProtein();
		}
		return getBMR(userID)*0.2/4.0 - currentProtein;
	}
	
	public double getRemainingCarbohydrate(String userID) {		
		double currentCarbohydrate = 0;
		Set<Food> mealsToday = getFoodsFromToday(userID);
		for(Food fd : mealsToday) {
			currentCarbohydrate += fd.getCarbohydrate();
		}
		return getBMR(userID)*0.55/4.0 - currentCarbohydrate;
	}
	
	public double getRemainingFat(String userID) {		
		double currentFat = 0;
		Set<Food> mealsToday = getFoodsFromToday(userID);
		for(Food fd : mealsToday) {
			currentFat += fd.getSaturatedFat();
		}
		return getBMR(userID)*0.25/9.0 - currentFat;
	}
	
	public String showDailyProgress(String userID) {
		DecimalFormat format = new DecimalFormat("##.00");
		return "Basal Metabolic Rate (BMR): "+format.format(getBMR(userID))+"\n"+
				"Body Mass Index (BMI): "+format.format(getBMI(userID))+"\n"+
				"Body Fat Percentage (BFP): "+format.format(getBFP(userID))+"\n"+
				"Current Status: "+getBMICategory(userID)+"\n"+"\n"+
				"Remaining Nutrients: \n"+
				"Calories: "+format.format(getRemainingCalories(userID))+"\n"+
				"Protein: "+format.format(getRemainingProtein(userID))+"\n"+
				"Carbohydrate: "+format.format(getRemainingCarbohydrate(userID))+"\n"+
				"Fat: "+format.format(getRemainingFat(userID))+"\n";			
	}
}