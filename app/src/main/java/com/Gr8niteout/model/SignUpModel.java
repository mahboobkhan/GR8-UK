package com.Gr8niteout.model;


import com.google.gson.Gson;

public class SignUpModel {

	public response response;

	public class response {
		public String status;
		public String code;
		public String msg;
		public user_data user_data;

		public class user_data{

			public String user_id;
			public String fb_id;
			public String access_token;
			public String email;
			public String fname;
			public String flag;
			public String user_active_status;
			public String user_status;
			public String token;

			public String getLname() {
				return lname;
			}

			public void setLname(String lname) {
				this.lname = lname;
			}

			public String getFname() {
				return fname;
			}

			public void setFname(String fname) {
				this.fname = fname;
			}

			public String getEmail() {
				return email;
			}

			public void setEmail(String email) {
				this.email = email;
			}

			public String lname;

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String mobile;


			public String getCc_code() {
				return cc_code;
			}

			public void setCc_code(String cc_code) {
				this.cc_code = cc_code;
			}

			public String cc_code;
			public String birthdate;
			public String gender;

			public String getPhoto() {
				return photo;
			}

			public void setPhoto(String photo) {
				this.photo = photo;
			}

			public String photo;
		}
	}

	public SignUpModel SignUpModel(String response) {
		try {
			Gson gson = new Gson();
			return (SignUpModel) gson.fromJson(response, SignUpModel.class);
		} catch (Exception e) {
			// Return null if parsing fails
			return null;
		}
	}
}
