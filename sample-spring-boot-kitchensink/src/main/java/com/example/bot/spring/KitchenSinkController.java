/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.*;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.regex.*;
import com.linecorp.bot.model.profile.UserProfileResponse;

import com.example.bot.spring.controllers.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.PushMessage;

import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import com.example.bot.spring.tables.Food;
import com.example.bot.spring.tables.FoodRepository;
import java.io.BufferedReader;
import java.io.FileReader;


@Slf4j
@LineMessageHandler
public class KitchenSinkController {
	@Autowired
	private FoodRepository foodRepository;
	
	@Autowired
	private LineMessagingClient lineMessagingClient;
	
	@Autowired
	private InputToFood inputToFood;
	
	@Autowired
	private User user;
	
	public enum Categories {MAIN_MENU, PROFILE, DAILY, FOOD, MENU, CODE, INIT, CAMPAIGN}
	public enum Profile {SET_GENDER,SET_AGE,SET_HEIGHT,SET_INTEREST, INPUT_WEIGHT, INPUT_MEAL, REQUEST_PROFILE}
	public enum Menu {TEXT, URL, JPEG}

	public Categories categories = null;
	public Profile profile = null;
	public Menu menu = null;
	
	public List<String> userList = new ArrayList<String>();
	public List<Categories> catList = new ArrayList<Categories>();
	public List<Profile> profList = new ArrayList<Profile>();
	public List<Menu> menuList = new ArrayList<Menu>();

	public String showMainMenu = "Hello I am your diet coach! What can I help you with?";
//            + "Profile - Record and view your weights and meals\n"
//			+ "Daily - View your progress on nutrients today\n"
//            + "Food - Get nutritional details of a food\n"
//            + "Menu - Input menu and let me pick a food for you to eat this meal\n"
//            + "Friend - Make recommendations to a friend to get an ice cream coupon!";	
	public Message mainMenuMessage = new TextMessage(showMainMenu);

    String imageProfile;
    String imageDaily;
    String imageFood;
    String imageMenu; 
    String imageFriend;
//    CarouselTemplate menuCarouselTemplate = new CarouselTemplate(
//            Arrays.asList(
//                    new CarouselColumn(imageProfile, "Your Profile", "Edit and view your profile", Arrays.asList(
//                            new MessageAction("Click here", "profile")
//                    )),
//                    new CarouselColumn(imageDaily, "Daily Progress", "View your nutritional progress today", Arrays.asList(
//                            new MessageAction("Click here", "daily")
//                    )),
//                    new CarouselColumn(imageFood, "Food Details", "Get nutritional details of a food", Arrays.asList(
//                            new MessageAction("Click here", "food")
//                    )),
//                    new CarouselColumn(imageMenu, "Choose Menu", "Let me choose a menu for you", Arrays.asList(
//                            new MessageAction("Click here", "menu")
//                    )),
//                    new CarouselColumn(imageFriend, "Refer a Friend", "Make recommendations to a friend to get an ice cream coupon!", Arrays.asList(
//                            new MessageAction("Click here", "friend")
//                    ))
//            ));
//    TemplateMessage menuTemplateMessage = new TemplateMessage("Front Menu", menuCarouselTemplate);

	
	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		handleSticker(event.getReplyToken(), event.getMessage());
	}

	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		LocationMessageContent locationMessage = event.getMessage();
		reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(), locationMessage.getAddress(),
				locationMessage.getLatitude(), locationMessage.getLongitude()));
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		
		int index = -1;
		for(int i=0;i<userList.size();i++) {
			if(userList.get(i).equals(event.getSource().getUserId())) {
				index = i;
				break;
			}
		}
		if(index == -1) {
			categories = null;
			profile = null;
			menu = null;
			index = userList.size();
			userList.add(event.getSource().getUserId());
			catList.add(categories);
			profList.add(profile);
			menuList.add(menu);
		}
		else {
			categories = catList.get(index);
			profile = profList.get(index);
			menu = menuList.get(index);
		}
		
		
		if (categories == Categories.CAMPAIGN) {
			InputStream initialStream = response.getStream();
			user.uploadCouponCampaign(initialStream);
			categories = Categories.MAIN_MENU;
			List<Message> messages = new ArrayList<Message>();
			TextMessage reply = new TextMessage("Uploaded successful");
			messages.add(reply);
			messages.add(mainMenuMessage);
			this.reply(replyToken, messages);
		}
		
		else if (categories == Categories.MENU && menu == Menu.JPEG) {
			DownloadedContent jpg = saveContent("jpg", response);
			String menu = inputToFood.readFromJPEG(jpg); // Use this menu string for features
			categories = Categories.MAIN_MENU;
			menu = null;
			reply(((MessageEvent) event).getReplyToken(), new TextMessage(menu));
		}
		else {
			String message = "What is this image for?";
			reply(((MessageEvent) event).getReplyToken(), new TextMessage(message));
		}
		
		catList.set(index, categories);
		profList.set(index, profile);
		menuList.set(index, menu);

	}

	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent mp4 = saveContent("mp4", response);
		reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100));
	}

	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got followed event");
	}

	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
	}

	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void sendPushMessage(@NonNull Message message,@NonNull String id) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.pushMessage(new PushMessage(id, message)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}


	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}

	private TemplateMessage getMenuTemplate() {
	    imageProfile = createUri("/static/buttons/menuProfile.jpg");
	    imageDaily = createUri("/static/buttons/menuDaily.jpg");
	    imageFood = createUri("/static/buttons/menuFood.jpg");
	    imageMenu = createUri("/static/buttons/menuMenu.jpg"); 
	    imageFriend = createUri("/static/buttons/menuFriend.jpg");
	    CarouselTemplate menuCarouselTemplate = new CarouselTemplate(
	            Arrays.asList(
	                    new CarouselColumn(imageProfile, "Your Profile", "Edit and view your profile", Arrays.asList(
	                            new MessageAction("Click here", "profile")
	                    )),
	                    new CarouselColumn(imageDaily, "Daily Progress", "View your nutritional progress today", Arrays.asList(
	                            new MessageAction("Click here", "daily")
	                    )),
	                    new CarouselColumn(imageFood, "Food Details", "Get nutritional details of a food", Arrays.asList(
	                            new MessageAction("Click here", "food")
	                    )),
	                    new CarouselColumn(imageMenu, "Choose Menu", "Let me choose a menu for you", Arrays.asList(
	                            new MessageAction("Click here", "menu")
	                    )),
	                    new CarouselColumn(imageFriend, "Refer a Friend", "Make recommendations to a friend to get an ice cream coupon!", Arrays.asList(
	                            new MessageAction("Click here", "friend")
	                    ))
	            ));
	   TemplateMessage menuTemplateMessage = new TemplateMessage("Front Menu", menuCarouselTemplate);
	   return menuTemplateMessage;
	}
	
	private TemplateMessage getProfileTemplate() {
	    imageRecord = createUri("/static/buttons/profileRecord.jpg");
	    imageView = createUri("/static/buttons/profileView.jpg");
	    CarouselTemplate profileCarouselTemplate = new CarouselTemplate(
	            Arrays.asList(
	                    new CarouselColumn(imageRecord, "Edit your profile", "Click below to set/update your information", Arrays.asList(
	                            new MessageAction("Gender", "gender"),
	                            new MessageAction("Age", "age"),
	                            new MessageAction("Height", "height")
	                    )),
	                    new CarouselColumn(imageRecord, "Edit your profile", "Click below to set/update your information", Arrays.asList(
	                            new MessageAction("Weight", "weight"),
	                            new MessageAction("Record your meals", "meal"),
	                            new MessageAction("Set your interests", "interest")
	                    )),
	                    new CarouselColumn(imageView, "View your profile", "Click below to view your updated profile", Arrays.asList(
	                            new MessageAction("Click here", "view")
	                    ))
	            ));
	   TemplateMessage profileTemplateMessage = new TemplateMessage("Profile", profileCarouselTemplate);
	   return profileTemplateMessage;
	}
	
	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
		
		String text = content.getText();

		Message response;
		List<Message> messages = new ArrayList<Message>();
		log.info("Got text message from {}: {}", replyToken, text);
		
		int index = -1;
		for(int i=0;i<userList.size();i++) {
			if(userList.get(i).equals(event.getSource().getUserId())) {
				index = i;
				break;
			}
		}
		if(index == -1) {
			categories = null;
			profile = null;
			menu = null;
			index = userList.size();
			userList.add(event.getSource().getUserId());
			catList.add(categories);
			profList.add(profile);
			menuList.add(menu);
		}
		else {
			categories = catList.get(index);
			profile = profList.get(index);
			menu = menuList.get(index);
		}
		
		if (categories == null) {		
			user.addUser(event.getSource().getUserId());
			messages.add(mainMenuMessage);
			messages.add(getMenuTemplate());
			this.reply(replyToken, messages); 
			categories = Categories.MAIN_MENU;
		}
		else {
			switch (categories) {
		    		case MAIN_MENU:
		    			response = new TextMessage(handleMainMenu(text, event));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(getMenuTemplate());
		    			}
		    			this.reply(replyToken, messages);
		    			break;
		    		case PROFILE:
		    			String responseText = handleProfile(replyToken, text, event);
		    			if(!responseText.equals("")) {
			    			response = new TextMessage(responseText);
			    			messages.add(response);
			    			messages.add(getProfileTemplate());
		    			}
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(getMenuTemplate());
		    			}
		    			if(messages.size()!=0) {
			    			this.reply(replyToken, messages);
		    			}
		    			break;
		    		case FOOD:
		    			response = new TextMessage(handleFood(text));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(getMenuTemplate());
		    			}
		    			this.reply(replyToken, messages);	    			
		    			break;
		    		case MENU:
		    			response = new TextMessage(handleMenu(text, event));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(getMenuTemplate());
		    			}
		    			this.reply(replyToken, messages);
		    			break;
		    		case CODE:
		    			String res = handleCode(text, event);
		    			break; 
		    		case CAMPAIGN:
		    			this.replyText(replyToken, "Please upload the coupon image.");
		    			break;
		    		case INIT:
		    			this.handleInit();
		    			this.replyText(replyToken, "Database initialized.");
		    			break;
			}
		}
		catList.set(index, categories);
		profList.set(index, profile);
		menuList.set(index, menu);
    }
	
	private String handleMainMenu (String text, Event event) {
		String result = "";
		Matcher m = Pattern.compile("profile|daily|food|menu|initdb|friend|code|admin", Pattern.CASE_INSENSITIVE).matcher(text);
		
		if (m.find()) {
			switch (m.group().toLowerCase()) {
		    		case "profile": {
		    			categories = Categories.PROFILE;
		    			result = "Under profile, these are the features that we provide:\n";
		    			break;
		    		}
		    		case "daily": {
		    			result = user.showDailyProgress(event.getSource().getUserId());
		    			categories = Categories.MAIN_MENU;
		    			break;
		    		}
		    		case "food": {
		    			categories = Categories.FOOD;
	    				result = "Enter a food name and I will provide you with the details!";
		    			break;
		    		}
		    		case "menu": {
		    			categories = Categories.MENU;
		    			result = "You may input the menu in the following three ways:\n"
		    				+ "Text\n"
		    				+ "URL\n"
		    				+ "JPEG";
		    			break;
		    		}
		    		case "initdb": {
		    			categories = Categories.INIT;
		    			result = "Initializing...";
		    			break;
		    		}
		    		case "friend": {
		    			result = "Your unique code is " + user.makeRecommendation(event.getSource().getUserId());
		    			break;
		    		}
		    		case "code": {
		    			switch(user.checkValidityOfUser(event.getSource().getUserId())) {
			    			case "claimed":
			    				result = "You already accepted a recommendation";
			    				break;
			    			case "taken":
			    				result = "This recommendation code is already used";
			    				break;
			    			case "before":
			    				result = "This campaign started after you registered";
			    				break;
			    			case "valid":
				    			result = "Insert the 6 digit code";
			    				categories = Categories.CODE;
			    				break;	
		    			}
		    			
		    			break;
		    		}
		    		case "admin": {
		    			if (user.isAdmin(event.getSource().getUserId())) {
		    				result = "Please upload the photo of the coupon";
		    				categories = Categories.CAMPAIGN;
		    			}
		    			else {
		    				result = "You are not an admin";
		    			}
		    		}
		    		
			}
		}
		else {
			result = "I don't understand";
		}

		return result;
	}
	
	// public enum Profile {SET_INTEREST, INPUT_WEIGHT, REQUEST_PROFILE}
	private String handleProfile (String replyToken, String text, Event event) {
		String result = "";
		if (profile == null) {
			Matcher m = Pattern.compile("gender|age|height|weight|meal|view|interest", Pattern.CASE_INSENSITIVE).matcher(text);
			if (m.find()) {
				switch (m.group().toLowerCase()) {
						case "gender":{
							profile = Profile.SET_GENDER;
							ConfirmTemplate confirmTemplate = new ConfirmTemplate(
			                        "Tell me your gender",
			                        new MessageAction("Male", "Male"),
			                        new MessageAction("Female", "Female")
			                );
							TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
			                this.reply(replyToken, templateMessage);			                
			                break;
						}
						case "age":{
							profile = Profile.SET_AGE;
							result = "Tell me your current age";
							break;
						}
						case "height":{
							profile = Profile.SET_HEIGHT;
							result = "Tell me your current height(cm)";
							break;
						}
			    		case "weight": {
			    			profile = Profile.INPUT_WEIGHT;
			    			result = "Tell me your current weight(kg)";
			    			break;
			    		}
			    		case "meal": {
			    			profile = Profile.INPUT_MEAL;
			    			result = "What did you just eat?";
			    			break;
			    		}
			    		
			    		case "view": {
			    			profile = Profile.REQUEST_PROFILE;
			    			result = "Would you like to view your \"general\" profile, or your past \"weights\" or \"meals\"?";
			    			break;
			    		}
			    		
			    		case "interest": {
			    			profile = Profile.SET_INTEREST;
			                String imageUrl = createUri("/static/buttons/foodCat.jpg");
			                CarouselTemplate interestCarouselTemplate = new CarouselTemplate(
			                        Arrays.asList(
			                                new CarouselColumn(imageUrl, "Select your interests", "Type \"done\" if you finish, \"reset\" if you want to reset", Arrays.asList(
			                                        new MessageAction("Breakfast/Eggs", "Dairy and Egg Products/ Breakfast Cereals"),
			                                        new MessageAction("Fast Foods", "Fast Foods/ Fats and Oils"),
			                                        new MessageAction("Spices/Herbs/Sauces", "Spices and Herbs/ Soups, Sauces, and Gravies")
			                                )),
			                                new CarouselColumn(imageUrl, "Select your interests", "Type \"done\" if you finish, \"reset\" if you want to reset", Arrays.asList(
			                                        new MessageAction("Sweets and Snacks", "Sweets/ Snacks"),
			                                        new MessageAction("Pork and meat", "Pork Products/ Sausages and Luncheon Meats"),
			                                        new MessageAction("Beef", "Beef Products")
			                                )),
			                                new CarouselColumn(imageUrl, "Select your interests", "Type \"done\" if you finish, \"reset\" if you want to reset", Arrays.asList(
			                                        new MessageAction("Chicken", "Poultry Products"),
			                                        new MessageAction("Lamb", "Lamb, Veal, and Game Products"),
			                                        new MessageAction("Nuts and Seeds", "Nut and Seed Products")
			                                )),
			                                new CarouselColumn(imageUrl, "Select your interests", "Type \"done\" if you finish, \"reset\" if you want to reset", Arrays.asList(
			                                        new MessageAction("Fruits/Vegetables", "Fruits and Fruit Juices/ Vegetables and Vegetable Products"),
			                                        new MessageAction("Beverages", "Beverages"),
			                                        new MessageAction("Country Cuisines", "American Indian/Alaska Native Foods/ Meals, Entrees, and Sidedishes")
			                                )),
			                                new CarouselColumn(imageUrl, "Select your interests", "Type \"done\" if you finish, \"reset\" if you want to reset", Arrays.asList(
			                                        new MessageAction("Bakeries", "Baked Products"),
			                                        new MessageAction("Rice, Pasta, Grains", "Cereal Grains and Pasta"),
			                                        new MessageAction("Baby Food", "Baby Foods")
			                                ))
			                                
			                        ));
			                TemplateMessage interestTemplateMessage = new TemplateMessage("Choosing your interests", interestCarouselTemplate);
			                this.reply(replyToken, interestTemplateMessage);
			    			break;
			    		}
				}
			}
			else {
				result = "I don't understand";
			}
		}
		else {
			boolean nan = false;
			switch (profile) {
					case SET_GENDER:
						user.inputGender(""+ event.getSource().getUserId(),text);
						result = "I successfully recorded your gender";
						profile = null;
		    			categories = Categories.MAIN_MENU;
		    			break;
					case SET_AGE:
						try {
			    			user.inputAge(""+ event.getSource().getUserId(),Integer.parseInt(text));
		    			} catch (NumberFormatException e) {
		    			    //error
		    				nan= true;
		    				return "Not a number. Please enter again";
		    			}		    			
		    			if (!nan) {
			    			result = "I successfully recorded your age";
			    			profile = null;
			    			categories = Categories.MAIN_MENU;
		    			}
		    			break;
					case SET_HEIGHT:
						try {
			    			user.inputHeight(""+ event.getSource().getUserId(),Double.parseDouble(text));
		    			} catch (NumberFormatException e) {
		    			    //error
		    				nan= true;
		    				return "Not a number. Please enter again";
		    			}		    			
		    			if (!nan) {
			    			result = "I successfully recorded your height";
			    			profile = null;
			    			categories = Categories.MAIN_MENU;
		    			}
		    			break;
		    		case INPUT_WEIGHT:
		    			try {
			    			user.inputWeight(""+ event.getSource().getUserId(),Double.parseDouble(text));
		    			} catch (NumberFormatException e) {
		    			    //error
		    				nan= true;
		    				return "Not a number. Please enter again";
		    			}		    			
		    			if (!nan) {
			    			result = "I successfully recorded your weight";
			    			profile = null;
			    			categories = Categories.MAIN_MENU;
		    			}
				    	break;
		    		case INPUT_MEAL:
		    			user.inputMeal(""+ event.getSource().getUserId(),text);
		    			result = "I successfully recorded your meal";
		    			profile = null;
		    			categories = Categories.MAIN_MENU;
		    			break;
		    		case SET_INTEREST:
		    			if(text.toLowerCase().equals("done")) {
			    			result = "Your interests were recorded.";
			    			profile = null;
			    			categories = Categories.MAIN_MENU;
		    			} else if(text.toLowerCase().equals("reset")) {
		    				result = user.resetInterest(""+ event.getSource().getUserId());
		    			} else {
			    			result = user.inputInterest(""+ event.getSource().getUserId(),text);
		    			}
//		    			user.inputInterest(""+ event.getSource().getUserId(),text);
//		    			result = "I successfully recorded your interests";
//		    			profile = null;
//		    			categories = Categories.MAIN_MENU;
		    			break;
		    		case REQUEST_PROFILE:
		    			result = handRequestProfile(text, event);
		    			
		    			break;
			}
		}
		return result;

	}
	
	private String handRequestProfile (String text, Event event) {
		String result = "";
		
		Matcher m = Pattern.compile("weight|meal|general", Pattern.CASE_INSENSITIVE).matcher(text);
		if (m.find()) {
			switch (m.group().toLowerCase()) {
				case "weight": {
					result = user.outputWeight(""+event.getSource().getUserId());
					break;
				}
				case "meal": {
					result = user.outputMeal(""+event.getSource().getUserId());
					break;
				}
				case "general": {
					result = user.outputGeneral(""+event.getSource().getUserId());
					result += user.outputInterest(""+event.getSource().getUserId());
					System.out.println("interest works here");
					break;
				}
			}
			profile = null;
			categories = Categories.MAIN_MENU;
		}
		else {
			result = "Did not understand";
		}
		return result;
	}
	
	private String handleFood (String text) {
		categories = Categories.MAIN_MENU;
		String result = "";
		result = inputToFood.getFoodDetails(text);
		return result;
	}
	
	private void handleInit() {
		try {
            String filePath = "/app/FOOD_DATA.txt";
            String line = null;
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

                String[] foodData = line.split("\\^");
        		for (int i = 2; i < foodData.length; i++) {
        			if (foodData[i].equals("")) {
        				foodData[i] = "-1";
        			}
        		}
                String foodName = foodData[0];
                String category = foodData[1];
                double calories = Double.parseDouble(foodData[2]);
                double sodium = Double.parseDouble(foodData[3]);
                double fat = Double.parseDouble(foodData[4]);
                double protein = Double.parseDouble(foodData[5]);
                double carbohydrate = Double.parseDouble(foodData[6]);
                Food food = new Food();
                food.setName(foodName);
                food.setCategory(category);
                food.setCalories(calories);
                food.setSodium(sodium);
                food.setSaturatedFat(fat);
                food.setProtein(protein);
                food.setCarbohydrate(carbohydrate);
                foodRepository.save(food);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	categories = Categories.MAIN_MENU;
        }
	}
	
	private String handleMenu (String text, Event event) {
		String result = "";
		if(menu == null) {
			Matcher m = Pattern.compile("text|url|jpeg", Pattern.CASE_INSENSITIVE).matcher(text);
			if (m.find()) {
				switch (m.group().toLowerCase()) {
			    		case "text": {
	                        menu = Menu.TEXT;
			    			break;
			    		}
			    		case "url": {
			    			menu = Menu.URL;
			    			break;
			    		}
			    		case "jpeg": {
			    			menu = Menu.JPEG;
			    			break;
			    		}
				}
				result = "OK! Show me the menu now!";
			}
			else {
				result = "I don't understand";
			}
		}
		else {
			switch (menu) {
    		case TEXT:
                result = inputToFood.readFromText(""+event.getSource().getUserId(),text);
                menu = null;
    			categories = Categories.MAIN_MENU;
    			break;
    		case URL:
    			result = inputToFood.readFromJSON(text);
    			menu = null;
    			categories = Categories.MAIN_MENU;
    			break;
    		case JPEG:
    			menu = null;
    			categories = Categories.MAIN_MENU;
    			break;
    		default:
    			result = "I don't understand";
    			break;
			}
		}
		return result;
			
	}
	
	private String handleCode (String text, Event event) {
		List<Message> messages = new ArrayList<Message>();
		String result = "";
		if (text.length() != 6) {
			result = "That is not 6 digits";
			messages.add(new TextMessage(result));
		}
		else {
			String id = user.acceptRecommendation(text ,event.getSource().getUserId());
			
			if (id.length()>11) {
				DownloadedContent jpg = saveContentFromDB("jpg", user.getCoupon());
				messages.add(new ImageMessage(jpg.getUri(), jpg.getUri()));
				sendPushMessage(new ImageMessage(jpg.getUri(), jpg.getUri()),id);
			}
			
			else {
				switch (id) {
					case "recommender": {
						result = "You made this recommendation";
						break;
					}
					case "claimed": {
						result = "Coupon has already been claimed";
						break;
					}
					case "none": {
						result = "No such code";
						break;
					}
				}
				messages.add(new TextMessage(result));

			}	
			messages.add(mainMenuMessage);

		}
		
		reply(((MessageEvent) event).getReplyToken(), messages);
		
		categories = Categories.MAIN_MENU;
		
		return result;
	}
	
	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	private static DownloadedContent saveContentFromDB(String ext, byte[] bytes) {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(bis, outputStream);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}

	public KitchenSinkController() {
		database = new SQLDatabaseEngine();
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}

	private DatabaseEngine database;
	private String itscLOGIN;
	

	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}


	//an inner class that gets the user profile and status message
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {
		private KitchenSinkController ksc;
		private String replyToken;
		
		public ProfileGetter(KitchenSinkController ksc, String replyToken) {
			this.ksc = ksc;
			this.replyToken = replyToken;
		}
		@Override
    	public void accept(UserProfileResponse profile, Throwable throwable) {
    		if (throwable != null) {
            	ksc.replyText(replyToken, throwable.getMessage());
            	return;
        	}
        	ksc.reply(
                	replyToken,
                	Arrays.asList(new TextMessage(
                		"Display name: " + profile.getDisplayName()),
                              	new TextMessage("Status message: "
                            		  + profile.getStatusMessage()))
        	);
    	}
    }
	
	

}
