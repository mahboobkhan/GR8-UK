package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;

public class SummaryModel {
    public SummaryModel SummaryModel(String response){
        return (SummaryModel) new Gson().fromJson(response, SummaryModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public data data;

        public class data{
            public String pub_credits_transactions;
        }
    }
}
