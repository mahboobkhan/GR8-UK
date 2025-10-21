package com.Gr8niteout.model;

import com.google.gson.annotations.SerializedName;

public class UserSignUpResponse {
    @SerializedName("response")
    public Response response;

    public static class Response {
        @SerializedName("status")
        public String status;

        @SerializedName("code")
        public String code;

        @SerializedName("ResponseInfo")
        public ResponseInfo responseInfo;
    }

    public static class ResponseInfo {
        @SerializedName("error")
        public Boolean error;

        @SerializedName("status")
        public String status;

        @SerializedName("code")
        public String code;

        @SerializedName("msg")
        public String msg;

        @SerializedName("data")
        public UserData data;
    }

    public static class UserData {
        @SerializedName("data")
        public UserDetails userDetails;
    }

    public static class UserDetails {
        @SerializedName("user_id")
        public String user_id;

        @SerializedName("username")
        public String username;

        @SerializedName("email")
        public String email;

        @SerializedName("gender")
        public String gender;

        @SerializedName("birthdate")
        public String birthdate;

        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("postcode")
        public String postcode;

        @SerializedName("udid")
        public String udid;

        @SerializedName("app_ver")
        public String app_ver;

        @SerializedName("d_type")
        public String d_type;

        @SerializedName("signup_through")
        public String signup_through;

        @SerializedName("login_through")
        public String login_through;

        @SerializedName("user_time_zone")
        public String user_time_zone;

        @SerializedName("first_name")
        public String first_name;

        @SerializedName("last_name")
        public String last_name;

        @SerializedName("user_type")
        public String user_type;

        @SerializedName("createddate")
        public String createddate;

        @SerializedName("activation_token")
        public String activation_token;

        @SerializedName("cc_code")
        public String cc_code;

        @SerializedName("is_auth")
        public String is_auth;

        @SerializedName("messageid")
        public String messageid;

        @SerializedName("sms_flag")
        public String sms_flag;

        @SerializedName("sms_code")
        public String sms_code;

        @SerializedName("access_token")
        public String access_token;

        @SerializedName("reset_token")
        public String reset_token;

        @SerializedName("is_deleted")
        public String is_deleted;

        @SerializedName("facebook_id")
        public String facebook_id;

        @SerializedName("mobile_no")
        public String mobile_no;
    }
}
