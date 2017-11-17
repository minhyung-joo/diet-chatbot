package com.example.bot.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextAnnotation {
	private String locale;
	private String description;
	private BoundingPolygon boundingPoly;
	
	public TextAnnotation() {}
	
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public BoundingPolygon getBoundingPoly() {
		return boundingPoly;
	}
	
	public void setBoundingPoly(BoundingPolygon boundingPoly) {
		this.boundingPoly = boundingPoly;
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BoundingPolygon {
	private Point[] vertices;
	
	public BoundingPolygon() {}
	
	public Point[] getVertices() {
		return vertices;
	}
	
	public void setVertices(Point[] vertices) {
		this.vertices = vertices;
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Point {
	private int x;
	private int y;
	
	public Point() {}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}