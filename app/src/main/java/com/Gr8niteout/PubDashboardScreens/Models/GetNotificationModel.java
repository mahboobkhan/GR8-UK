package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

import java.util.List;

public class GetNotificationModel {
    public GetNotificationModel GetNotificationModel(String response) {
        return (GetNotificationModel) new Gson().fromJson(response, GetNotificationModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public String count;
        public List<data> data;

        public class data {
            public String notification_id;
            public String notification;
            public String notification_time;
            public String notification_date;
        }
    }
}
