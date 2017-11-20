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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

import com.example.bot.spring.controllers.User;
import com.example.bot.spring.KitchenSinkController.DownloadedContent;
import com.example.bot.spring.tables.RecommendationRepository;
import com.example.bot.spring.tables.Recommendation;



@RunWith(SpringRunner.class)
public class RecommendationTest {

	private User user = new User();
	
//	@Autowired
//	private RecommendationRepository recommendationRepository;
	
	private String userID1 = "1234567890123456789012345678901234";
	private String userID2 = "1234567890123456789012345678901235";
	private long uniqueCode1 = 999999;
//	@Before
//	public void before () {
//		Recommendation rd = recommendationRepository.findByUniqueCode(uniqueCode1);
//		if (rd ==null) {
//			Recommendation rec = new Recommendation();
//			rec.setUserID(userID1);
//			rec.setClaimed(false);
//			rec.setUniqueCode(uniqueCode1);
//			recommendationRepository.save(rec);
//		}
//	}
//	
	@Test
	public void testAcceptRecommendation() throws Exception {
		
		String nullStr = null;
		String emptyStr = "";
		String notSixDigits = "1234";
		String notSixDigitNumber = "ABCDEF";
		
		String validButNullCode = "999998";
		String validButClaimedCode;
		String validButOwnCode;
		

		
		String userID1 = "U861b3649f59765641656b970d5a8659b";
		String userID2 = "U23240e381b87718a1b290fd3a9fcbebe";
		
		String invalidInput = user.acceptRecommendation(nullStr,userID2);
		assertEquals(invalidInput,"That is not a 6 digit number");
		
		invalidInput = user.acceptRecommendation(emptyStr,userID2);
		assertEquals(invalidInput,"That is not a 6 digit number");

		invalidInput = user.acceptRecommendation(notSixDigits,userID2);
		assertEquals(invalidInput,"That is not a 6 digit number");

		invalidInput = user.acceptRecommendation(notSixDigitNumber,userID2);
		assertEquals(invalidInput,"That is not a 6 digit number");	
		
//		invalidInput = user.acceptRecommendation(validButNullCode,userID2);
//		assertEquals(invalidInput,"There is no such code");	
		
		
	}
}
