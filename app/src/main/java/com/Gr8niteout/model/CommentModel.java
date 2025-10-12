package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;


    public class CommentModel {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;

        public ArrayList<comments_list> comments_list;

        public class comments_list{
            public String user_image;
            public String user_name;
            public String comment;
            public String days_ago;
        }
    }

    public CommentModel CommentModel(String response) {
        Gson gson = new Gson();
        return (CommentModel) gson.fromJson(response,CommentModel.class);
    }
}
