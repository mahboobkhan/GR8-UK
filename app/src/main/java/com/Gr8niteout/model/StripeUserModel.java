package com.Gr8niteout.model;

import com.google.gson.Gson;

public class StripeUserModel {
    public StripeUserModel StripeUserModel(String response) {

        return (StripeUserModel) new Gson().fromJson(response, StripeUserModel.class);
    }

    public String status;
    public String code;
    public String msg;
    public data data;

    public class data{
        public String cancel_url;
        public String success_url;
        public String url;
        public error error;
        
        public class error{
            public String code;
            public String doc_url;
            public String message;
            public String param;
            public String request_log_url;
            public String type;
        }
    }

}