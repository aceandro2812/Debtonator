package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/17/2017.
 */

public class TransactionContributionListItem {
    String id = null;
    String transactionsDetailsId = null;
    String personId = null;
    String contribution = null;
    String personName = null;
    boolean isConsumer = false;

    public TransactionContributionListItem() {

    }

    public TransactionContributionListItem(String id, String transactions_details_id, String people_details_id, String contribution, String person_name, boolean is_consumer) {
        this.id = id;
        this.transactionsDetailsId = transactions_details_id;
        this.personId = people_details_id;
        this.contribution = contribution;
        this.personName = person_name;
        this.isConsumer = is_consumer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionsDetailsId() {
        return transactionsDetailsId;
    }

    public void setTransactionsDetailsId(String transactionsDetailsId) {
        this.transactionsDetailsId = transactionsDetailsId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public boolean isConsumer() {
        return isConsumer;
    }

    public void setConsumer(boolean consumer) {
        this.isConsumer = consumer;
    }
}