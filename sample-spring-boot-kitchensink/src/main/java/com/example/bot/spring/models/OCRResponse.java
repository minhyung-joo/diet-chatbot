package com.example.bot.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OCRResponse {
	private Response[] responses;
	
	public OCRResponse() {}
	
	public Response[] getResponses() {
		return responses;
	}
	
	public void setResponses(Response[] responses) {
		this.responses = responses;
	}
}