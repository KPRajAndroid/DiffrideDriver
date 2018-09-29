package com.diff.provider.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBankResponse{

	@SerializedName("banks")
	private List<BanksItem> banks;

	public void setBanks(List<BanksItem> banks){
		this.banks = banks;
	}

	public List<BanksItem> getBanks(){
		return banks;
	}
}