package com.Gr8niteout.model;


import com.google.gson.Gson;

import java.util.ArrayList;

public class PubProfile {

    public Response response;

    public PubProfile PubProfile(String response) {
        Gson gson = new Gson();
        return (PubProfile)gson.fromJson(response,PubProfile.class);
    }

    public class Response {
        public String status;
        public String code;
        public String msg;

        public Pub_Profile pub_profile;

        public class Pub_Profile
        {
            public ArrayList<String> banner_array;

            public String thumb_image;
            public String pub_name;
            public String description;
            public String latitude;
            public String longitude;
            public String address;
            public String phone;
            public String station;
            public String url;
            public String pub_id;
            public int status;
            public String rating;
            public String price;
            public String distans;
            public String currency;

            public ArrayList<feature_list> feature_list;

            public class feature_list
            {
                public String value;
                public String name;
                public String big_image;
            }

            public ArrayList<days_list> days_list;
            public class days_list
            {
                public String day;
                public String from_time;
                public String to_time;
                public String is_closed;
            }

            public String pub_type;
        }

    }
}
