package com.diff.provider.Models;

import com.google.gson.annotations.SerializedName;

public class HistoryItem{

	@SerializedName("amount")
	private int amount;

	@SerializedName("user_id")
	private Object userId;

	@SerializedName("provider_id")
	private int providerId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private String status;

	@SerializedName("via")
	private String via;



	@SerializedName("description")
	private String description;

	public void setAmount(int amount){
		this.amount = amount;
	}

	public int getAmount(){
		return amount;
	}

	public void setUserId(Object userId){
		this.userId = userId;
	}

	public Object getUserId(){
		return userId;
	}

	public void setProviderId(int providerId){
		this.providerId = providerId;
	}

	public int getProviderId(){
		return providerId;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setVia(String via){
		this.via = via;
	}

	public String getVia(){
		return via;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}