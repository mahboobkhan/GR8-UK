package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GetPubProfileModel {
        public GetPubProfileModel GetPubProfileModel(String response){
            return (GetPubProfileModel) new Gson().fromJson(response, GetPubProfileModel.class);
        }

        public response response;

        public class response {
            public String status;
            public String code;
            public String msg;
            public data data;

            public class data{
                public String about_pub;
                public String pub_profile;
                public String pub_featured_Image;
                public String[] other_photos;
                @SerializedName("1_status")
                public String one_status;
                @SerializedName("1_comment")
                public String one_comment;
                @SerializedName("2_status")
                public String two_status;
                @SerializedName("3_status")
                public String three_status;
                @SerializedName("3_comment")
                public String three_comment;
                @SerializedName("4_status")
                public String four_status;
                @SerializedName("4_comment")
                public String four_comment;
                @SerializedName("5_comment")
                public String five_comment;
                @SerializedName("6_status")
                public String six_status;
                @SerializedName("6_comment")
                public String six_comment;
                @SerializedName("7_status")
                public String seven_status;
                @SerializedName("8_status")
                public String eight_status;
                @SerializedName("9_status")
                public String nine_status;
                @SerializedName("9_comment")
                public String nine_comment;
                @SerializedName("10_status")
                public String ten_status;
                @SerializedName("11_status")
                public String eleven_status;
                @SerializedName("11_comment")
                public String eleven_comment;
                @SerializedName("12_status")
                public String twelve_status;
                @SerializedName("13_status")
                public String thirteen_status;
            }
        }
}
