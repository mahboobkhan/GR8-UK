package com.Gr8niteout.model;

import com.google.gson.Gson;

public class User_Status {

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
    }

    public User_Status User_Status(String response) {
        Gson gson = new Gson();
        return (User_Status) gson.fromJson(response,User_Status.class);
    }
}
