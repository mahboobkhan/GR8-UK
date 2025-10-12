package com.Gr8niteout.model;


import com.google.gson.Gson;

public class TokenInfoModel {

	public response response;

	public class response {
		public String status;
		public String code;
		public String msg;
		public tokenInfo token_info;

		public class tokenInfo{

			public String token;
			public int ResponseFlag;
			public String term_url;
			public String privacy_url;
		}
	}

	public TokenInfoModel TokenInfoModel(String response) {
		Gson gson = new Gson();
		return (TokenInfoModel) gson.fromJson(response,TokenInfoModel.class);
	}
}
