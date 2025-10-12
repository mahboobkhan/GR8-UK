package com.Gr8niteout.model;

import com.google.gson.Gson;

public class StripeAccountAddModel {
    public StripeAccountAddModel StripeAccountAddModel(String response) {

        return (StripeAccountAddModel) new Gson().fromJson(response, StripeAccountAddModel.class);
    }

    public response response;

    public class response{
        public String status;
        public String code;
        public String msg;
        public data data;
    }

    public class data{
        public String url;
    }
}
