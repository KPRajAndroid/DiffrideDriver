package com.diff.provider.Models;

import com.google.gson.annotations.SerializedName;

public class BanksItem{

	@SerializedName("name")
	private String name;
	@SerializedName("id")
	private int id;

	@SerializedName("deleted_at")
	private Object deletedAt;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDeletedAt(Object deletedAt){
		this.deletedAt = deletedAt;
	}

	public Object getDeletedAt(){
		return deletedAt;
	}

	@Override
	public String toString() {
		return name;
	}
}