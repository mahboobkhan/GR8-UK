package com.Gr8niteout.model;

import com.google.gson.Gson;

public class PubForgotPasswordModel {
    public PubForgotPasswordModel PubForgotPasswordModel(String response) {

        return (PubForgotPasswordModel) new Gson().fromJson(response, PubForgotPasswordModel.class);
    }

    public response response;

    public class response{
        public String status;
        public String code;
        public String msg;
    }
}
