package com.Gr8niteout.model;


import com.google.gson.Gson;

import java.util.ArrayList;

public class CountryModel {

    public CountryModel CountryModel(String response) {

        return (CountryModel) new Gson().fromJson(response,CountryModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public ArrayList<country_list> country_list;

        public class country_list{
            public String country_name;
            public String country_code;
            public String numcode;
        }
    }
}
