package com.Gr8niteout.model;

import com.google.gson.Gson;

/**
 * Created by sudhansu on 2/4/2017.
 */
public class Logout_Model {

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public user_logout user_logout;
        public  class user_logout{
            public String message;
            public String token;
        }


    }

    public Logout_Model Logout_Model(String response) {
        Gson gson = new Gson();
        return (Logout_Model) gson.fromJson(response,Logout_Model.class);
    }
}
