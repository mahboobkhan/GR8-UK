package com.Gr8niteout.model;

import com.google.gson.Gson;

public class CheckUserEmailModel {
    public CheckUserEmailModel CheckUserEmailModel(String response){
        return (CheckUserEmailModel) new Gson().fromJson(response, CheckUserEmailModel.class);
    }

    public response response;

    public class response{
        public String status;
        public String code;
        public ResponseInfo ResponseInfo;
    }

    public class ResponseInfo{
        public boolean valid;
        public String message;
    }

}
