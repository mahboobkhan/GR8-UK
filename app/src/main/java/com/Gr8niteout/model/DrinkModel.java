package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DrinkModel {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public drinklist drinklist;

        public class drinklist{
            public String status;
            public String count;
            public int limit;

            public ArrayList<DrinkModel.response.drinklist.drinks_lists> getDrinks_lists() {
                return drinks_lists;
            }

            public void setDrinks_lists(ArrayList<DrinkModel.response.drinklist.drinks_lists> drinks_lists) {
                this.drinks_lists = drinks_lists;
            }

            public ArrayList<drinks_lists> drinks_lists;

            public class drinks_lists{
                public String names;
                public String photo;
                public String days_ago;
                public String amount;
                public String pub_name;
                public String pub_img;
                public String message;
                public String credit_code;
                public String image_attached;
                public String video_attached;
                public String expire_days;
                public String pub_id;
                public String pub_credit_id;
                public String temp_pub_rcode;
                public String expStatus;
                public String total_amount;
            }
        }

    }
    public DrinkModel DrinkModel(String response) {
        Gson gson = new Gson();
        return (DrinkModel) gson.fromJson(response,DrinkModel.class);
    }
}
