package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/12/2017.
 */

public class TransactionContributionPojo {
    String id = null;
    String transactions_details_id = null;
    String people_details_id = null;
    String contribution = null;
    boolean is_consumer = false;

    public TransactionContributionPojo(String id, String transactions_details_id, String people_details_id, String contribution, boolean is_consumer) {
        this.id = id;
        this.transactions_details_id = transactions_details_id;
        this.people_details_id = people_details_id;
        this.contribution = contribution;
        this.is_consumer = is_consumer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactions_details_id() {
        return transactions_details_id;
    }

    public void setTransactions_details_id(String transactions_details_id) {
        this.transactions_details_id = transactions_details_id;
    }

    public String getPeople_details_id() {
        return people_details_id;
    }

    public void setPeople_details_id(String people_details_id) {
        this.people_details_id = people_details_id;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public boolean is_consumer() {
        return is_consumer;
    }

    public void setIs_consumer(boolean is_consumer) {
        this.is_consumer = is_consumer;
    }

}
