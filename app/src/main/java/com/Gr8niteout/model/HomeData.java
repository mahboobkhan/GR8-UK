package com.Gr8niteout.model;


import com.google.gson.Gson;

import java.util.ArrayList;

public class HomeData {

    public response response;

    public HomeData HomeData(String response) {
        Gson gson = new Gson();

        return (HomeData)gson.fromJson(response,HomeData.class);
    }

    public class response {
        public String status;
        public String code;
        public String msg;
        ;

        public home_data home_data;

        public class home_data
        {
            public String user_status;
            public ArrayList<banner> banner;
            public ArrayList<home_pubinfo> home_pubinfo;
            public class banner
            {
                public String image_id,image_name,banner_url;
            }

            public class home_pubinfo
            {
                public String pub_id,pub_name,profile_pic,rating,price,distance,currency;
                public int status;
            }
        }

    }
}
