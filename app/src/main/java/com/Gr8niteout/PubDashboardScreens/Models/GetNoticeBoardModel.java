package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

import java.util.List;

public class GetNoticeBoardModel {
    public GetNoticeBoardModel GetNoticeBoardModel(String response) {
        return (GetNoticeBoardModel) new Gson().fromJson(response, GetNoticeBoardModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public String count;
        public List<data> data;

        public class data {
            public String pub_notice_id;
            public String title;
            public String description;
            public String created_date;
            public String notices_image;
        }
    }
}
