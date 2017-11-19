package com.example.bot.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.example.bot.spring.controllers.InputToFood;
import com.example.bot.spring.tables.FoodRepository;
import com.example.bot.spring.KitchenSinkController.DownloadedContent;

@RunWith(SpringRunner.class)
public class InputToFoodTest {
	private InputToFood inputToFood = new InputToFood();
	
	@Test
	public void testReadFromJSON() throws Exception {
		String nullStr = null;
		String emptyStr = "";
		String invalidUrl = "htt:asdf1234.com";
		String jsonUrl = "https://randomjsonserver.herokuapp.com";
		
		String nullResult = inputToFood.readFromJSON(nullStr);
		assertEquals(nullResult, "Invalid input");
		
		String emptyResult = inputToFood.readFromJSON(emptyStr);
		assertEquals(emptyResult, "Failed to load URL.");
		
		String invalidResult = inputToFood.readFromJSON(invalidUrl);
		assertEquals(invalidResult, "Failed to load URL.");
		
		String result = inputToFood.readFromJSON(jsonUrl);
		assertEquals(result, "The Foods in each entree are as followed:\n" + 
				"1. Pork, Bean curd, Rice, \n" + 
				"2. Pork, Sweet and Sour Sauce, Pork, \n" + 
				"3. Chili, Chicken, Rice, ");
	}
	
	@Test
	public void testReadFromJPEG() throws Exception {
		DownloadedContent nullContent = null;
		DownloadedContent invalidFormatContent = new DownloadedContent(null, "");
		URI uri = getClass().getResource("/static/test/sample-menu2.jpg").toURI();
		File file = new File(uri);
		Path path = file.toPath();
		DownloadedContent jpeg = new DownloadedContent(path, uri.toString());
		
		String nullResult = inputToFood.readFromJPEG(nullContent);
		assertEquals(nullResult, "Invalid input");
		
		String invalidResult = inputToFood.readFromJPEG(invalidFormatContent);
		assertEquals(nullResult, "Invalid input");
		
		String result = inputToFood.readFromJPEG(jpeg);
		System.out.println(result);
		assertEquals(result, "Spicy Bean curd with Minced Pork served with Rice\n" + 
				"Sweet and Sour Pork served with Rice\n" + 
				"Chili Chicken on Rice\n" + 
				"Fried instance noodle with Luncheon Meat\n" + 
				"35\n" + 
				"36\n" + 
				"28 F\n" + 
				"40\n");
	}
}
