package com.example.bot.spring.controllers;

import com.example.bot.spring.tables.*;
import com.example.bot.spring.controllers.MenuController;

import java.util.*;
import java.util.function.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import com.example.bot.spring.models.Menu;
import com.example.bot.spring.models.OCRResponse;
import com.example.bot.spring.models.Response;
import com.example.bot.spring.models.TextAnnotation;
import com.example.bot.spring.KitchenSinkController.DownloadedContent;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

@Service
public class InputToFood {
	@Autowired
	private FoodRepository foodRepository;
	
	@Autowired
	private MenuController menuController;

    public String readFromText(String userId, String text) {
		menuController.setUserID(userId);
		menuController.setMenu(text);
    	return menuController.pickFood();
    }
    
//    public String readFromText(String userId, String text) {
//    	return "";
//    }

    public String readFromJSON(String url) {
    	if (url == null) {
    		return "Invalid input";
    	}
    	
    	try {
    		RestTemplate restTemplate = new RestTemplate();
        	Menu[] menuList = restTemplate.getForObject(url, Menu[].class);
        	StringBuilder builder = new StringBuilder();
        	int counter = 1;
        	builder.append("The Foods in each entree are as followed:\n");
        	for (Menu menu : menuList) {
        		String[] ingredients = menu.getIngredients();
        		builder.append(counter);
        		builder.append(". ");
        		for (String ingredient : ingredients) {
        			builder.append(ingredient);
        			builder.append(", ");
        		}
        		counter++;
        		builder.append("\n");
        	}
        	builder.deleteCharAt(builder.length() - 1);
        	
        	return builder.toString();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return "Failed to load URL.";
    	}
    }

    public String readFromJPEG(DownloadedContent jpeg) {
    	if (jpeg == null || jpeg.getPath() == null || jpeg.getUri() == null) {
    		return "Invalid input";
    	}
    	
    	String menu = "";
    	RestTemplate restTemplate = new RestTemplate();
    	String apiKey = "AIzaSyCrPOUDlYLaAQLAXbFSiRgb16OSikBooP8";
    	String url = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;
    	String json = buildJson(jpeg);
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	HttpEntity<String> entity = new HttpEntity<String>(json, headers);
    	OCRResponse ocrResponse = restTemplate.postForObject(url, entity, OCRResponse.class);
    	Response response = ocrResponse.getResponses()[0];
    	TextAnnotation textAnnotation = response.getTextAnnotations()[0];
    	menu = textAnnotation.getDescription();
    	
    	return menu;
    }
    
    private String buildJson(DownloadedContent jpeg) {
    	try {
    		StringBuilder jsonBuilder = new StringBuilder();
        	File file = jpeg.getPath().toFile();
        	String imageCode = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
        	jsonBuilder.append("{\"requests\":[");
        	jsonBuilder.append("{\"image\":{");
        	jsonBuilder.append("\"content\":\"");
        	jsonBuilder.append(imageCode);
        	jsonBuilder.append("\"},");
        	jsonBuilder.append("\"features\":[{");
        	jsonBuilder.append("\"type\":\"TEXT_DETECTION\"");
        	jsonBuilder.append("}]");
        	jsonBuilder.append("}");
        	jsonBuilder.append("]}");
        	return jsonBuilder.toString();
    	} catch (IOException e) {
    		e.printStackTrace();
    		return "";
    	}
    }
    
    public String getFoodDetails(String food) {
    		String resultFood = "You have entered " + food + "\n";
    		String[] splitFood = food.split("\\s+");
    		
	    	for (int i = 0; i < splitFood.length; i++) {	
	    		for (Food fd : foodRepository.findAll()) {
	    		    String fdName = fd.getName().toLowerCase();
	            	if (fdName.contains(",")) {
	            		fdName = fdName.substring(0,fdName.indexOf(","));
	            	}
	            	
	            	if (fdName.endsWith("s")) {
	            		fdName = fdName.substring(0, fdName.length()-1);
	            	}
	            	
	    		    if (checkEquality(fdName, splitFood[i]) || splitFood[i].toLowerCase().contains(fdName)) { 
	    		    		resultFood += "Here are the details for " + fdName + "\n" + fd.getDetails() + "\n" + "\n";
	    		    		break;
	    		    }
	    		}
	    	}
    		
    		return resultFood;
    }
    
    private boolean checkEquality(String s1, String s2) {
    	return getDistance(s1, s2) <= 2;
    }
    
    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    /**
     * Using Dynamic Programming, the Wagner-Fischer algorithm is able to 
     * calculate the edit distance between two strings.
     * @return edit distance between s1 and s2
     */
    private int getDistance(String str1, String str2) {
    	char[] s1 = str1.toCharArray();
    	char[] s2 = str2.toCharArray();
    	
        int[][] dp = new int[s1.length + 1][s2.length + 1];
        for (int i = 0; i <= s1.length; dp[i][0] = i++);
        for (int j = 0; j <= s2.length; dp[0][j] = j++);

        for (int i = 1; i <= s1.length; i++) {
            for (int j = 1; j <= s2.length; j++) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, 
                    		dp[i - 1][j - 1] + 1);
                }
            }
        }
        return dp[s1.length][s2.length];
    }
}
