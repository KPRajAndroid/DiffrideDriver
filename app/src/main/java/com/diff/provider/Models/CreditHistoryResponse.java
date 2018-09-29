package com.diff.provider.Models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CreditHistoryResponse{

	@SerializedName("history")
	private List<HistoryItem> history;

	public void setHistory(List<HistoryItem> history){
		this.history = history;
	}

	public List<HistoryItem> getHistory(){
		return history;
	}
}