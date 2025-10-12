package com.Gr8niteout.model;


import com.google.gson.Gson;

public class Edit_Profile_Model {

	public response response;

	public class response {
		public String status;
		public String code;
		public String msg;
		public profile_status profile_status;

		public class profile_status{
			public String success;

			public String getPhoto() {
				return photo;
			}

			public void setPhoto(String photo) {
				this.photo = photo;
			}

			public String photo;

			public String getCc_code() {
				return cc_code;
			}

			public void setCc_code(String cc_code) {
				this.cc_code = cc_code;
			}

			public String cc_code;

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String mobile;
		}
	}

	public Edit_Profile_Model Edit_Profile(String response) {
		Gson gson = new Gson();
		return (Edit_Profile_Model) gson.fromJson(response,Edit_Profile_Model.class);
	}
}
