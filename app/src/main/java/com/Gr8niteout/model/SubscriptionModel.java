package com.Gr8niteout.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class SubscriptionModel {

    @SerializedName("response")
    private ResponseData response;

    public ResponseData getResponse() {
        return response;
    }

    public void setResponse(ResponseData response) {
        this.response = response;
    }

    public static class ResponseData {
        @SerializedName("status")
        private String status;

        @SerializedName("code")
        private String code;

        @SerializedName("data")
        private SubscriptionData data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public SubscriptionData getData() {
            return data;
        }

        public void setData(SubscriptionData data) {
            this.data = data;
        }
    }

    public static class SubscriptionData {
        @SerializedName("success")
        private String successMessage; // Can be null

        public String getSuccessMessage() {
            return successMessage;
        }

        public void setSuccessMessage(String successMessage) {
            this.successMessage = successMessage;
        }
    }


    public SubscriptionModel SubscriptionModel(String response) {
        Gson gson = new Gson();
        return (SubscriptionModel) gson.fromJson(response,SubscriptionModel.class);
    }
}