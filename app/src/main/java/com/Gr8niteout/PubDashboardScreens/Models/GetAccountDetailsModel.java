package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

public class GetAccountDetailsModel {
    public GetAccountDetailsModel GetAccountDetailsModel(String response) {

        return (GetAccountDetailsModel) new Gson().fromJson(response, GetAccountDetailsModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public data data;

        public class data {
            public String title;
            public String username;
            public String first_name;
            public String last_name;
            public String position;
            public String gender;
            public String day;
            public String month;
            public String year;
            public String profile_pic;
            public String mobile_no;
            public String email;
        }
    }
}
