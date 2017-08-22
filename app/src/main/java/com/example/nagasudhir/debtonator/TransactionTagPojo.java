package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/22/2017.
 */

public class TransactionTagPojo {
    String id = null;
    String name_string = null;
    String transactions_details_id = null;
    String updated_at = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_string() {
        return name_string;
    }

    public void setName_string(String name_string) {
        this.name_string = name_string;
    }

    public String getTransactions_details_id() {
        return transactions_details_id;
    }

    public void setTransactions_details_id(String transactions_details_id) {
        this.transactions_details_id = transactions_details_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public TransactionTagPojo(String id, String name_string, String transactions_details_id, String updated_at) {
        this.id = id;
        this.name_string = name_string;
        this.transactions_details_id = transactions_details_id;
        this.updated_at = updated_at;
    }

    public TransactionTagPojo() {

    }
}
