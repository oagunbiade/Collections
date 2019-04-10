package com.coronation.collections.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonConverter {
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
			.create();
	public static <T> T getElement(String value, Class<T> clazz) {
		return gson.fromJson(value, clazz);
	}
	
	public static <T> T[] getElements(String value, Class<T[]> clazz) {
		return gson.fromJson(value, clazz);
	}
	
	public static <T> String getJson(T element) {
		return gson.toJson(element);
	}
	
	public static <T> String getJsonRecursive(T element) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(element);
			return jsonInString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}		
	}
}
