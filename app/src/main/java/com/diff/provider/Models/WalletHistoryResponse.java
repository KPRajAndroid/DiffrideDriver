package com.diff.provider.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WalletHistoryResponse{

	@SerializedName("history")
	private List<HistoryItem> history;

	public void setHistory(List<HistoryItem> history){
		this.history = history;
	}

	public List<HistoryItem> getHistory(){
		return history;
	}
}