package com.ms.loginapplication.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.ms.loginapplication.model.AuthenticationTokensData;
import com.ms.loginapplication.model.UserData;
import com.ms.loginapplication.repository.AuthenticationTokensDataRepository;

import static org.toilelibre.libe.curl.Curl.curl;
import static org.toilelibre.libe.curl.Curl.$;

@RestController
public class LoginController {

	
	@Autowired
	AuthenticationTokensDataRepository authenticationTokensDataRepository;
	
	@RequestMapping(value="/login" , method = RequestMethod.POST)
	public  ResponseEntity<Object>  login(@RequestBody UserData userData) throws Exception { 
		String apiKey= userData.getApiKey();
		String userNameFromRequest = userData.getFirstName();
		String lastNameFromRequest = userData.getLastName();
		String emailFromRequest = userData.getEmail();
		HttpResponse curlResponse = curl(
				"curl -k -X POST  --header 'Content-Type: application/x-www-form-urlencoded' --header 'Accept: application/json' --data-urlencode 'grant_type=urn:ibm:params:oauth:grant-type:apikey' --data-urlencode 'apikey="
						+ apiKey + "' 'https://iam.cloud.ibm.com/identity/token'");
		
		System.out.println("3333"+curlResponse);
		org.apache.http.HttpEntity entity = curlResponse.getEntity();
		
		String result = null;
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = cnvrtString(instream);
			result = result.replaceAll("\"", "'");
			instream.close();
		}
		AuthenticationTokensData authenticationData = new AuthenticationTokensData();
		authenticationData.setUserToken("NA");
		authenticationData.setUserName("NA");
		authenticationData.setServiceToken("NA");
		authenticationData.setTransactionToken("NA");
		
		JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
		JsonElement jsonAccessTokenElementError = jsonObject.get("errorMessage");
		if(jsonAccessTokenElementError != null) {
			authenticationData.setErrorMessage("authentication failed "+jsonAccessTokenElementError);
			return new ResponseEntity<>(authenticationData, HttpStatus.OK);
			
		}
		/////
		Decoder decoder = java.util.Base64.getUrlDecoder();
		Encoder encoder = java.util.Base64.getEncoder();
		
		 System.out.println("result "+result);
		String[] tokens = result.split("\\.");
		
		
		
		 String payload = new String(decoder.decode(tokens[1]));
		 System.out.println("payload "+payload);
		 String givenName = "";
		 String familyName = "";
		 JsonObject jsonObject1 = new Gson().fromJson(payload, JsonObject.class);
		 if (!jsonObject1.isJsonNull()) {
			 JsonElement givenNameElment = jsonObject1.get("given_name");
			 JsonElement familyNameElement = jsonObject1.get("family_name");
			 JsonElement emailNameElement = jsonObject1.get("email");
			 JsonElement accountNameElement = jsonObject1.get("account");
			 
			  givenName = givenNameElment.toString().replace("\"", "");
			 familyName = familyNameElement.toString().replace("\"", "");
			 String email = emailNameElement.toString().replace("\"", "");
			
			System.out.println(givenName + familyName);
			
			JsonObject jsonObjectAccount = accountNameElement.getAsJsonObject();
			JsonElement validElement = jsonObjectAccount.get("valid");
			String valid = validElement.toString().replace("\"", "");
			
			
			
			if(!valid.equals("true")) {
				authenticationData.setErrorMessage("authentication failed - account is not valid");
				return new ResponseEntity<>(authenticationData, HttpStatus.OK);
				}
			
			if(!givenName.equals(userNameFromRequest)) {
				authenticationData.setErrorMessage("authentication failed - user name does not match");
				return new ResponseEntity<>(authenticationData, HttpStatus.OK);
				
			}
				
			
			if(!familyName.equals(lastNameFromRequest)) {
				authenticationData.setErrorMessage("authentication failed - user name does not match");
				return new ResponseEntity<>(authenticationData, HttpStatus.OK);
			}
			
			if(!email.equals(emailFromRequest)) {
				authenticationData.setErrorMessage("authentication failed - email does not match");
				return new ResponseEntity<>(authenticationData, HttpStatus.OK);
			}
		 }
		 
		/////
		
		if (!jsonObject.isJsonNull()) {

			
			JsonElement jsonAccessTokenElement = jsonObject.get("access_token");
			JsonElement jsonRefreshTokenElement = jsonObject.get("refresh_token");
			JsonElement jsonExpiresIn = jsonObject.get("expires_in");
			

			String accessToken = jsonAccessTokenElement.toString().replace("\"", "");
			String refreshToken = jsonRefreshTokenElement.toString().replace("\"", "");
			String expiresInStr = jsonExpiresIn.toString();
			
			System.out.println("accessToken"+accessToken);
			System.out.println("refreshToken"+refreshToken);
			System.out.println("expiresInStr"+expiresInStr);
			
			int expiresIn = Integer.parseInt(expiresInStr);
		
			String userToken = encoder.encodeToString((apiKey+new Date()).getBytes());
			System.out.println("userToken  is"+userToken);
			 
			authenticationData = new AuthenticationTokensData(apiKey , userToken, accessToken, refreshToken,givenName + familyName,expiresIn,LocalDateTime.now());
			authenticationTokensDataRepository.deleteAuthenticationData(apiKey);
			authenticationTokensDataRepository.save(authenticationData);
			
			 return new ResponseEntity<>(authenticationData, HttpStatus.OK);
		}
		return null;
	}
	
	@RequestMapping(value="/getAuthenticationTokensData/{apiKey}" , method = RequestMethod.GET)
	public AuthenticationTokensData getUserTokensData(@PathVariable("apiKey") String
			apiKey) { 
		AuthenticationTokensData authenticationTokensData  = authenticationTokensDataRepository.findByuserToken(apiKey);
		return authenticationTokensData;
	}
	
	private static String cnvrtString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	}
