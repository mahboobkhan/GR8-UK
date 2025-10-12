package com.Gr8niteout.model;


import com.google.gson.Gson;

import java.util.ArrayList;

public class FeatureModel {

    public FeatureModel FeatureModel(String response) {

        return (FeatureModel) new Gson().fromJson(response,FeatureModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public features features;
        public class features{

            public ArrayList<feature_list> feature_list;

            public class feature_list
            {
                public String id;
                public String title;
            }
        }
    }
}
