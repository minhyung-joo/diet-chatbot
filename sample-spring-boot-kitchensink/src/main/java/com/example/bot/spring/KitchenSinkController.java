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

import java.io.IOException;
import java.io.OutputStream;
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
	private InputToFood i;
	
	@Autowired
	private User user;
	
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
		DownloadedContent jpg = saveContent("jpg", response);
		reply(((MessageEvent) event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri()));

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

	public enum Categories {MAIN_MENU, PROFILE, FOOD, MENU, INIT}
	public enum Profile {SET_INTEREST, INPUT_WEIGHT, INPUT_MEAL, REQUEST_PROFILE}
	public enum Menu {TEXT, URL, JPEG}
	
	public Categories categories = null;
	
	public Profile profile = null;
	
	public Menu menu = null;
	
	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
		
		String text = content.getText();
		String showMainMenu = "Hello! These are the features that we provide:\n"
                + "Profile - record weight, record meal,...\n"
                + "Food - Get the details of a food\n"
                + "Menu - Input menu and let me pick a food for you to eat this meal!";
		Message mainMenuMessage = new TextMessage(showMainMenu);
		Message response;
		List<Message> messages = new ArrayList<Message>();
		log.info("Got text message from {}: {}", replyToken, text);
		if (categories == null) {
            user.addUser(""+ event.getSource().getUserId());
            
			this.replyText(replyToken, showMainMenu);
			categories = Categories.MAIN_MENU;
		}
		else {
			switch (categories) {
		    		case MAIN_MENU:
		    			this.replyText(replyToken, handleMainMenu(text));
		    			break;
		    		case PROFILE:
		    			response = new TextMessage(handleProfile(text, event));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(mainMenuMessage);
		    			}
		    			this.reply(replyToken, messages);
		    			break;
		    		case FOOD:
		    			response = new TextMessage(handleFood(text));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(mainMenuMessage);
		    			}
		    			this.reply(replyToken, messages);	    			
		    			break;
		    		case MENU:
		    			response = new TextMessage(handleMenu(text));
		    			messages.add(response);
		    			if (categories == Categories.MAIN_MENU) {
		    				messages.add(mainMenuMessage);
		    			}
		    			this.reply(replyToken, messages);
		    			break;
		    		case INIT:
		    			this.handleInit();
		    			this.replyText(replyToken, "Database initialized.");
		    			break;
			}
		}
    }
	
	private String handleMainMenu (String text) {
		String result = "";
		Matcher m = Pattern.compile("profile|food|menu|initdb", Pattern.CASE_INSENSITIVE).matcher(text);
		
		if (m.find()) {
			switch (m.group().toLowerCase()) {
		    		case "profile": {
		    			categories = Categories.PROFILE;
		    			result = "Under profile, these are the features that we provide:\n"
		                     + "Weight - Record your weight\n"
		                     + "Meal - Record your meal\n"
		                     + "Profile - View your profile";
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
			}
		}
		else {
			result = "I don't understand";
		}

		return result;
	}
	
	// public enum Profile {SET_INTEREST, INPUT_WEIGHT, REQUEST_PROFILE}
	private String handleProfile (String text, Event event) {
		String result = "";
		if (profile == null) {
			Matcher m = Pattern.compile("weight|meal|profile", Pattern.CASE_INSENSITIVE).matcher(text);
			if (m.find()) {
				switch (m.group().toLowerCase()) {
			    		case "weight": {
			    			profile = Profile.INPUT_WEIGHT;
			    			result = "Please input your current weight in kgs";
			    			break;
			    		}
			    		case "meal": {
			    			profile = Profile.INPUT_MEAL;
			    			result = "What did you just eat?";
			    			break;
			    		}
			    		
			    		case "profile": {
			    			profile = Profile.REQUEST_PROFILE;
			    			result = "Which one would you like to display? Weight or meals?";
			    			break;
			    		}
				}
			}
			else {
				result = "I don't understand";
			}
		}
		else {
			switch (profile) {
		    		case INPUT_WEIGHT:
		    			boolean nan = false;
		    			try {
			    			user.inputWeight(""+ event.getSource().getUserId(),Double.parseDouble(text));
		    			} catch (NumberFormatException e) {
		    			    //error
		    				nan= true;
		    				return "Not a number. Please enter again";
		    			}		    			
		    			if (!nan) {
			    			result = "Input successful";
			    			profile = null;
			    			categories = Categories.MAIN_MENU;
		    			}
				    	break;
		    		case INPUT_MEAL:
		    			user.inputMeal(""+ event.getSource().getUserId(),text);
		    			result = "Input successful";
		    			profile = null;
		    			categories = Categories.MAIN_MENU;
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
		
		Matcher m = Pattern.compile("weight|meal", Pattern.CASE_INSENSITIVE).matcher(text);
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
		result = i.getFoodDetails(text);
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
	
	private String handleMenu (String text) {
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
                result = i.readFromText(text);
                menu = null;
    			categories = Categories.MAIN_MENU;
    			break;
    		case URL:
    			result = i.readFromJSON(text);
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
