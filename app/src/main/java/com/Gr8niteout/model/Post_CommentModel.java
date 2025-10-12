package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;


public class Post_CommentModel {
public response response;

public class response {
    public String status;
    public String code;
    public String msg;

    public comments_status comments_status;

    public class comments_status{
        public String status;
    }
}

public Post_CommentModel Post_CommentModel(String response) {
    Gson gson = new Gson();
    return (Post_CommentModel) gson.fromJson(response,Post_CommentModel.class);
}
}
