package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.List;

public class CheckedInUserModel {

    public static CheckedInUserModel getModel(String response){
        return new Gson().fromJson(response,CheckedInUserModel.class);
    }

    public Response response;

    public class Response {
        public String status;
        public String code;
        public String msg;
        public  CheckedInUser checkin_user;
    }


    public class CheckedInUser{
        public int limit;
        public List<CheckInData> check_in_data;
        public String status;
        public String error;
        public String count;
        public String currency;
    }

    public class CheckInData{
        public String pub_id;
        public String user_id;
        public String email;
        public String first_name;
        public String last_name;
        public String mobile_no;
        public String profile_image;
        public String checkin_datetime;
        public String cc_code;
    }
}
