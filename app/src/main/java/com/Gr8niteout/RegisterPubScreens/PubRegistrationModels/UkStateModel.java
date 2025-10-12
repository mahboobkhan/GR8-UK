package com.Gr8niteout.RegisterPubScreens.PubRegistrationModels;

import com.google.gson.Gson;

import java.util.ArrayList;

public class UkStateModel {

    public UkStateModel UkStateModel(String response) {

        return (UkStateModel) new Gson().fromJson(response, UkStateModel.class);
    }

  public response response;

    public class response {
        public String status;
        public String code;
        public ArrayList<country_states> country_states;

        public class country_states {
            public String country_iso;
            public String name;
            public String id;
        }
    }
}
