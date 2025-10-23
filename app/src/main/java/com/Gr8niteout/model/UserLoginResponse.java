package com.Gr8niteout.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {
    
    @SerializedName("response")
    public Response response;
    
    public class Response {
        @SerializedName("status")
        public String status;
        
        @SerializedName("code")
        public String code;
        
        @SerializedName("ResponseInfo")
        public ResponseInfo responseInfo;
        
        @SerializedName("msg")
        public String msg;
    }
    
    public class ResponseInfo {
        @SerializedName("status")
        public String status;
        
        @SerializedName("code")
        public String code;
        
        @SerializedName("data")
        public Data data;
    }
    
    public class Data {
        @SerializedName("user_logged_in")
        public boolean user_logged_in;
        
        @SerializedName("user_id")
        public String user_id;
        
        @SerializedName("end_user_name")
        public String end_user_name;
        
        @SerializedName("end_first_name")
        public String end_first_name;
        
        @SerializedName("end_last_name")
        public String end_last_name;
        
        @SerializedName("end_email")
        public String end_email;
        
        @SerializedName("user_type")
        public String user_type;
        
        @SerializedName("facebook_id")
        public String facebook_id;
        
        @SerializedName("end_profile_pic")
        public String end_profile_pic;
        
        // Legacy fields for compatibility
        public String pub_id;
        public String owner_id;
        public String pub_name;
        public String first_name;
        public String last_name;
        public String username;
        public String email;
        public String profile_pic;
        public String title;
        public String gender;
        public String position;
        public String dob;
        public String mobile_no;
        public String is_active;
        public String token;
        public String flag;
        public String user_active_status;
    }
}
