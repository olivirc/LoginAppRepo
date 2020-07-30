package com.ms.loginapplication.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="authentication_tokensdata")
public class AuthenticationTokensData {
	int id;
	String apiKey;
	String userToken;
	String serviceToken;
	String transactionToken;
	String userName;
	int expiresIn;
	LocalDateTime createDate;
	String errorMessage;
	
	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public AuthenticationTokensData() {
		
	}
	
	public AuthenticationTokensData(String apiKey ,String userToken, String serviceToken, String transactionToken,String userName , int expiresIn,LocalDateTime createDate) {
		super();
		this.apiKey = apiKey;
		this.userToken = userToken;
		this.serviceToken = serviceToken;
		this.transactionToken = transactionToken;
		this.userName = userName;
		this.expiresIn = expiresIn;
		this.createDate = createDate;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name="user_token",columnDefinition="LONGTEXT")
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	@Column(name="service_token",columnDefinition="LONGTEXT")
	public String getServiceToken() {
		return serviceToken;
	}
	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}
	
	@Column(name="transaction_token",columnDefinition="LONGTEXT")
	public String getTransactionToken() {
		return transactionToken;
	}
	public void setTransactionToken(String transactionToken) {
		this.transactionToken = transactionToken;
	}
	
	@Column(name="user_name",columnDefinition="LONGTEXT")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	

	public int getExpiresIn() {
		return expiresIn;
	}


	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	@Column(name="api_key",columnDefinition="LONGTEXT")
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

}
