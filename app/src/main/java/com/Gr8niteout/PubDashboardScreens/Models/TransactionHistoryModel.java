package com.Gr8niteout.PubDashboardScreens.Models;
import com.google.gson.Gson;

import java.util.List;

public class TransactionHistoryModel {
    public TransactionHistoryModel TransactionHistoryModel(String response) {

        return (TransactionHistoryModel) new Gson().fromJson(response, TransactionHistoryModel.class);
    }

//     ["receiver_id": "728", "sender_id": "728", "pub_name": "Dog and duck", "currency": "GBP",
//             "expiry_date": "2024-12-05 00:00:00", "recipient_name": "Sheila Gaughan",
//             "amount": "0", "pub_id": "1231", "id": "2164", "sender_name": "Sheila", "date": "2024-11-05"]
    public response response;

    public class response{
        public String status;
        public String code;
        public String msg;
        public int totalCountRow;
        public List<data> data;
    }

    public class data{
        public String amount;
        public String date;
        public String currency;
        public String pub_name;
        public String username;
        public String sender_name;
        public String sender_id;
        public String receiver_id;
        public String pub_id;
        public String id;
        public String expiry_date;
        public String recipient_name;
        public String comment;
    }
}
