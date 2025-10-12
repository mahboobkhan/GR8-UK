package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

    public class WhatsOnModel {

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;

        public ArrayList<whatson> whatson;

        public class whatson{
            public String title;
            public String description;
            public String image;
        }
    }

    public WhatsOnModel WhatsOnModel(String response) {
        Gson gson = new Gson();
        return (WhatsOnModel) gson.fromJson(response,WhatsOnModel.class);
    }
}
