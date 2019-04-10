package com.coronation.collections.util;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class Utilities {
	
	private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

	@Autowired
	private JavaMailSender sender;

	public String getCurrentTimeUsingDate() {
		Date date = new Date();
		String strDateFormat = "hh:mm:ss a";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String formattedDate = dateFormat.format(date);
		logger.info("Current time of the day using Date - 12 hour format: " + formattedDate);
		return formattedDate;
	}

	public static void getCurrentTimeUsingCalendar() {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String formattedDate = dateFormat.format(date);
		logger.info("Current time of the day using Calendar - 24 hour format: " + formattedDate);
	}

	public static void getCurrentTime() {
		logger.info("-----Current time of your time zone-----");
		LocalTime time = LocalTime.now();
		logger.info("Current time of the day: " + time);
	}

	public static void getCurrentTimeWithTimeZone() {
		logger.info("-----Current time of a different time zone using LocalTime-----");
		ZoneId zoneId = ZoneId.of("America/Los_Angeles");
		LocalTime localTime = LocalTime.now(zoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String formattedTime = localTime.format(formatter);
		logger.info("Current time of the day in Los Angeles: " + formattedTime);
	}

	public static void getCurrentTimeWithOffset() {
		logger.info("-----Current time of different offset-----");
		ZoneOffset zoneOffset = ZoneOffset.of("-08:00");
		ZoneId zoneId = ZoneId.ofOffset("UTC", zoneOffset);
		LocalTime offsetTime = LocalTime.now(zoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
		String formattedTime = offsetTime.format(formatter);
		logger.info("Current time of the day with offset -08:00: " + formattedTime);
	}
	@Async
	public void sendEmailMsg(String toEmail, String body, String subject) throws Exception {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setTo(toEmail);
		helper.setText(body);
		helper.setSubject(subject);

		sender.send(message);
	}

	public String encrypt(String strClearText, String strKey) throws Exception {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted = cipher.doFinal(strClearText.getBytes());
			strData = new String(encrypted);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

	public String decrypt(String strEncrypted, String strKey) throws Exception {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
			strData = new String(decrypted);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

	public static float getBalRequest(String accountNumber, String url) throws IOException {
		
		StringBuffer response = new StringBuffer();
		JsonObject POST_PARAMS = new JsonObject();
		POST_PARAMS.addProperty("accountNumber", accountNumber);
		
		System.out.println(POST_PARAMS);
		// URL obj = new URL("https://jsonplaceholder.typicode.com/posts");
		URL obj = new URL(url);
		HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
		postConnection.setRequestMethod("POST");
		postConnection.setRequestProperty("Content-Type", "application/json");
		postConnection.setDoOutput(true);
		OutputStream os = postConnection.getOutputStream();
		os.write(POST_PARAMS.toString().getBytes());
		os.flush();
		os.close();
		int responseCode = postConnection.getResponseCode();
		System.out.println("POST Response Code :  " + responseCode);
		System.out.println("POST Response Message : " + postConnection.getResponseMessage());
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			System.out.println(response.toString());
			
			JSONObject myResponse = new JSONObject(response.toString());
			System.out.println("balance = : "+myResponse.getFloat("balance"));
			float balance = myResponse.getFloat("balance");
			
			return balance;
		} else {
			logger.info("POST NOT WORKED");
			return (float) 0.00;
		}
	}
	
	public static String localTransferRequest(String debitAccountNumber,String creditAccountNumber,Float tranAmount, String url) throws IOException {
		
		StringBuffer response = new StringBuffer();
		JsonObject POST_PARAMS = new JsonObject();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssss");

		POST_PARAMS.addProperty("uniqueIdentifier", sdf.format(timestamp));
		POST_PARAMS.addProperty("debitAccountNumber", debitAccountNumber);
		POST_PARAMS.addProperty("creditAccountNumber", creditAccountNumber);
		POST_PARAMS.addProperty("tranAmount", tranAmount);
		POST_PARAMS.addProperty("naration", "Collections");
		
		System.out.println(POST_PARAMS);
		// URL obj = new URL("https://jsonplaceholder.typicode.com/posts");
		URL obj = new URL(url);
		HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
		postConnection.setRequestMethod("POST");
		postConnection.setRequestProperty("Content-Type", "application/json");
		postConnection.setDoOutput(true);
		OutputStream os = postConnection.getOutputStream();
		os.write(POST_PARAMS.toString().getBytes());
		os.flush();
		os.close();
		int responseCode = postConnection.getResponseCode();
		logger.info("POST Response Code :  " + responseCode);
		logger.info("POST Response Message : " + postConnection.getResponseMessage());
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			logger.info(response.toString());
			
			JSONObject myResponse = new JSONObject(response.toString());
			logger.info("response code = : "+myResponse.getString("responseCode"));
			String respCode = myResponse.getString("responseCode");
			
			return respCode;
		} else {
			logger.info("POST DIDN'T WORK");
			return "ERROR";
		}
	}
}
