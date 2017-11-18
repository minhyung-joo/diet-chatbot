package com.example.bot.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
	private TextAnnotation[] textAnnotations;
	
	public Response() {}
	
	public TextAnnotation[] getTextAnnotations() {
		return textAnnotations;
	}
	
	public void setTextAnnotations(TextAnnotation[] textAnnotations) {
		this.textAnnotations = textAnnotations;
	}
}
