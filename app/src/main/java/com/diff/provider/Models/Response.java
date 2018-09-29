package com.diff.provider.Models;
import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("message")
	private String message;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}