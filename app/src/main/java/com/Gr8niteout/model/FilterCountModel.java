package com.Gr8niteout.model;

import com.google.gson.Gson;


public class FilterCountModel {
public response response;

public class response {
    public String status;
    public String code;
    public String msg;
public pub_find_count pub_find_count;
    public class pub_find_count{
        public int  counts;
    }
}

public FilterCountModel FilterCountModel(String response) {
    Gson gson = new Gson();
    return (FilterCountModel) gson.fromJson(response,FilterCountModel.class);
}
}
