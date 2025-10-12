package com.Gr8niteout.model;

import com.google.gson.Gson;

public class PubRegistrationModel {
    public PubRegistrationModel PubRegistrationModel(String response) {

        return (PubRegistrationModel) new Gson().fromJson(response, PubRegistrationModel.class);
    }

    public response response;

    public class response{
        public String status;
        public String code;
        public String msg;
        public int pub_id;
    }
}
