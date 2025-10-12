package com.Gr8niteout.model;


import com.google.gson.Gson;

import java.util.ArrayList;

public class CountryStateModel {

    public CountryStateModel CountryStateModel(String response) {

        return (CountryStateModel) new Gson().fromJson(response,CountryStateModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public ArrayList<country_states> country_states;

        public class country_states{
            public String country;
            public String iso;
            public ArrayList<state_list> state_list;
            public class state_list
            {
                public String state_id;
                public String state_name;
            }
        }
    }
}
