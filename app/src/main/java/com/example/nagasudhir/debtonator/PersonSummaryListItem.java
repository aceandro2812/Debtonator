package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/26/2017.
 */

public class PersonSummaryListItem {
    private String personId = null;
    private String personName = null;
    private String transactionId = null;
    private String transactionName = null;
    private Double transactionWorth = null;
    private Double personContribution = null;
    private Double personConsumption = null;
    private int numTransactionPeople = 0;

    public PersonSummaryListItem() {
    }

    public PersonSummaryListItem(String personId, String personName, String transactionId, String transactionName, Double transactionWorth, Double personContribution, Double personConsumption, int numTransactionPeople) {
        this.personId = personId;
        this.personName = personName;
        this.transactionId = transactionId;
        this.transactionName = transactionName;
        this.transactionWorth = transactionWorth;
        this.personContribution = personContribution;
        this.personConsumption = personConsumption;
        this.numTransactionPeople = numTransactionPeople;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public Double getTransactionWorth() {
        return transactionWorth;
    }

    public void setTransactionWorth(Double transactionWorth) {
        this.transactionWorth = transactionWorth;
    }

    public Double getPersonContribution() {
        return personContribution;
    }

    public void setPersonContribution(Double personContribution) {
        this.personContribution = personContribution;
    }

    public Double getPersonConsumption() {
        return personConsumption;
    }

    public void setPersonConsumption(Double personConsumption) {
        this.personConsumption = personConsumption;
    }

    public int getNumTransactionPeople() {
        return numTransactionPeople;
    }

    public void setNumTransactionPeople(int numTransactionPeople) {
        this.numTransactionPeople = numTransactionPeople;
    }
}
