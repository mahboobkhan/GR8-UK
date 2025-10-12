package com.Gr8niteout.model;

import com.google.gson.Gson;

public class PubLoginModel {
    public PubLoginModel PubLoginModel(String response) {

        return (PubLoginModel) new Gson().fromJson(response, PubLoginModel.class);
    }

    public response response;

    public class response{
        public String status;
        public String code;
        public String msg;
        public data data;
    }

    public class data{
        public String owner_id;
        public String title;
        public String username;
        public String first_name;
        public String last_name;
        public String position;
        public String gender;
        public String dob;
        public String profile_pic;
        public String mobile_no;
        public String email;
        public String is_active;
        public String pub_id;
        public String pub_name;
    }

}