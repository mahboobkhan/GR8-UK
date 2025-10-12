package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

public class CheckinResponse {

    public CheckinResponse  getModel(String response) {
        return new Gson().fromJson(response,CheckinResponse .class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public CheckInUser checkin_user;

        public class CheckInUser{
            public String success;
        }
    }
}
