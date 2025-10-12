package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

public class ChangePasswordModel {
    public ChangePasswordModel ChangePasswordModel(String response) {

        return (ChangePasswordModel) new Gson().fromJson(response, ChangePasswordModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
    }
}
