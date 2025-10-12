package com.Gr8niteout.model;

import com.google.gson.Gson;


public class CreditModel {
public response response;

public class response {
    public String status;
    public String code;
    public String msg;

    public pub_send_credits pub_send_credits;

    public class pub_send_credits{
        public paypal paypal;
        public class paypal{

            public String action;
            public String charset;
            public String cmd;
            public String paykey;
        }
        public String share_url;
        public String share_text;
        public String sucess_url;
        public String cancel_url;
    }
}

public CreditModel CreditModel(String response) {
    Gson gson = new Gson();
    return (CreditModel) gson.fromJson(response,CreditModel.class);
}
}
