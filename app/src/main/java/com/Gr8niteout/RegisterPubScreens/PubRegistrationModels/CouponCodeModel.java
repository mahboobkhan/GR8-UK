package com.Gr8niteout.RegisterPubScreens.PubRegistrationModels;

import com.google.gson.Gson;

public class CouponCodeModel {
    public CouponCodeModel CouponCodeModel(String response) {

        return (CouponCodeModel) new Gson().fromJson(response, CouponCodeModel.class);
    }

    public response response;

    public class response {
        public String status;
        public String code;
        public apply_coupan apply_coupan;

        public class apply_coupan {
            public Boolean valid;
            public String msg;
            public String discount_per;
            public String discountprice;
            public String converted_amount;
        }
    }
}
