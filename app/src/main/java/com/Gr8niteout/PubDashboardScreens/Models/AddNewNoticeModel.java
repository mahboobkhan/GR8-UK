package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

public class AddNewNoticeModel {
    public AddNewNoticeModel AddNewNoticeModel(String response) {

        return (AddNewNoticeModel) new Gson().fromJson(response, AddNewNoticeModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public data data;

        public class data {
            public String success;
        }
    }
}
