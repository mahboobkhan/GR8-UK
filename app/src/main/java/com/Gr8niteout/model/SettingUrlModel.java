package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sudhansu on 1/7/2017.
 */

    public class SettingUrlModel {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;

        public ArrayList<settings_data> settings_data;

        public class settings_data{
            public String title;
            public String url;
        }
    }

    public SettingUrlModel SettingUrlModel(String response) {
        Gson gson = new Gson();
        return (SettingUrlModel) gson.fromJson(response,SettingUrlModel.class);
    }
}
