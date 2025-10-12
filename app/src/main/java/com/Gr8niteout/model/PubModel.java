package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PubModel {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public find_pub find_pub;

        public class find_pub{
            public String status;
            public double count;
            public double limit;

            public ArrayList<find_pubs_lists> find_pubs_lists;

            public class find_pubs_lists{
                public String pub_name;
                public String rating;
                public String pub_image;
                public String avg_price;
                public String status;
                public String pub_id;
                public String distance;
                public String currency;
            }
        }

    }
    public PubModel PubModel(String response) {
        Gson gson = new Gson();
        return (PubModel) gson.fromJson(response,PubModel.class);
    }
}
