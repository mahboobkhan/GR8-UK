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
        
        @SerializedName("msg")
        public String msg;
        
        @SerializedName("data")
        public Data data;
    }
    
    public class Data {
        @SerializedName("pub_id")
        public String pub_id;
        
        @SerializedName("owner_id")
        public String owner_id;
        
        @SerializedName("pub_name")
        public String pub_name;
        
        @SerializedName("first_name")
        public String first_name;
        
        @SerializedName("last_name")
        public String last_name;
        
        @SerializedName("username")
        public String username;
        
        @SerializedName("email")
        public String email;
        
        @SerializedName("profile_pic")
        public String profile_pic;
        
        @SerializedName("title")
        public String title;
        
        @SerializedName("gender")
        public String gender;
        
        @SerializedName("position")
        public String position;
        
        @SerializedName("dob")
        public String dob;
        
        @SerializedName("mobile_no")
        public String mobile_no;
        
        @SerializedName("is_active")
        public String is_active;
        
        @SerializedName("user_id")
        public String user_id;
        
        @SerializedName("token")
        public String token;
        
        @SerializedName("flag")
        public String flag;
        
        @SerializedName("user_active_status")
        public String user_active_status;
    }
}
