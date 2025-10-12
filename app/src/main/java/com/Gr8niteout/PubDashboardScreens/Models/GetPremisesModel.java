package com.Gr8niteout.PubDashboardScreens.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GetPremisesModel {
    public GetPremisesModel GetPremisesModel(String response){
        return (GetPremisesModel) new Gson().fromJson(response, GetPremisesModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public String msg;
        public data data;

        public class data{
            public String pub_name;
            public String premises_type;
            public String currency;
            public String phone_no;
            public String nearest_station;
            public String post_code;
            public String address1;
            public String address2;
            public String town;
            public String other_state;
            public String country;

            @SerializedName("1_from")
            public String one_from;
            @SerializedName("1_to")
            public String one_to;
            @SerializedName("1_chk")
            public String one_chk;

            @SerializedName("2_from")
            public String two_from;
            @SerializedName("2_to")
            public String two_to;
            @SerializedName("2_chk")
            public String two_chk;

            @SerializedName("3_from")
            public String three_from;
            @SerializedName("3_to")
            public String three_to;
            @SerializedName("3_chk")
            public String three_chk;

            @SerializedName("4_from")
            public String four_from;
            @SerializedName("4_to")
            public String four_to;
            @SerializedName("4_chk")
            public String four_chk;

            @SerializedName("5_from")
            public String five_from;
            @SerializedName("5_to")
            public String five_to;
            @SerializedName("5_chk")
            public String five_chk;

            @SerializedName("6_from")
            public String six_from;
            @SerializedName("6_to")
            public String six_to;
            @SerializedName("6_chk")
            public String six_chk;

            @SerializedName("7_from")
            public String seven_from;
            @SerializedName("7_to")
            public String seven_to;
            @SerializedName("7_chk")
            public String seven_chk;
        }
    }
}
