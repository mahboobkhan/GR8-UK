package com.Gr8niteout.model;


import com.google.gson.Gson;

public class OtpModel {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public verification_code verification_code;

        public class verification_code{
            public String varification_code;
        }
    }

    public OtpModel OtpModel(String response) {
        Gson gson = new Gson();
        return (OtpModel) gson.fromJson(response,OtpModel.class);
    }
}
