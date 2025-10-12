package com.Gr8niteout.model;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Pub_photo_model {
    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public photos_list photos_list;

        public class photos_list{
            public String status;
            public String count;
            public int limit;

            public ArrayList<Pub_photo_model.response.photos_list.photos_lists> getPhotos_lists() {
                return photos_lists;
            }

            public void setPhotos_lists(ArrayList<Pub_photo_model.response.photos_list.photos_lists> photos_lists) {
                this.photos_lists = photos_lists;
            }

            public ArrayList<photos_lists> photos_lists;

            public class photos_lists{
                public String photo_id;
                public String photo;
                public String days_ago;
                public String description;
                public String user_image;
                public String user_name;
                public String photo_share_url;
            }
        }

    }
    public Pub_photo_model Pub_photo_model(String response) {
        Gson gson = new Gson();
        return (Pub_photo_model) gson.fromJson(response,Pub_photo_model.class);
    }
}
