package com.Gr8niteout.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class SubscriptionAPIModel {
        @SerializedName("response")
        private ResponseData response;

        public ResponseData getResponse() {
            return response;
        }

        public void setResponse(ResponseData response) {
            this.response = response;
        }

        public SubscriptionAPIModel SubscriptionAPIModel(String response) {
            Gson gson = new Gson();
            try {
                return gson.fromJson(response, SubscriptionAPIModel.class);
            } catch (Exception e) {
                Log.e("SubscriptionModel", "Error parsing JSON: " + e.getMessage());
                return null;
            }
        }

        public static class ResponseData {
            @SerializedName("status")
            private String status;

            @SerializedName("msg")
            private String msg;

            @SerializedName("name")
            private String name;

            @SerializedName("email")
            private String email;

            @SerializedName("owner_id")
            private String ownerId;

            @SerializedName("latest_subscription")
            private SubscriptionData latestSubscription;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getOwnerId() {
                return ownerId;
            }

            public void setOwnerId(String ownerId) {
                this.ownerId = ownerId;
            }

            public SubscriptionData getLatestSubscription() {
                return latestSubscription;
            }

            public void setLatestSubscription(SubscriptionData latestSubscription) {
                this.latestSubscription = latestSubscription;
            }
        }

        public static class SubscriptionData {
            @SerializedName("type")
            private String type;

            @SerializedName("plan_name")
            private String planName;

            @SerializedName("created_at")
            private String createdAt;

            @SerializedName("end_subscription")
            private String endSubscription;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getPlanName() {
                return planName;
            }

            public void setPlanName(String planName) {
                this.planName = planName;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getEndSubscription() {
                return endSubscription;
            }

            public void setEndSubscription(String endSubscription) {
                this.endSubscription = endSubscription;
            }
        }
    }