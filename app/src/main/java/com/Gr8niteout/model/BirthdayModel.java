package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sudhansu on 2/4/2017.
 */
public class BirthdayModel {

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;

        public ArrayList<friends_birthdays> friends_birthdays;

        public  class friends_birthdays{
            public String days_ago;
            public String photo;
            public String name;
        }
    }

    public BirthdayModel BirthdayModel(String response) {
        Gson gson = new Gson();
        return (BirthdayModel) gson.fromJson(response,BirthdayModel.class);
    }
}
