package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/4/2017.
 */

public class TransactionPojo {
    String id = null;
    String transaction_sets_id = null;
    String description = null;
    String metadata = null;
    String transaction_time = null;
    String uuid = null;
    String created_at = null;
    String updated_at = null;

    public TransactionPojo(String transaction_sets_id, String description, String transaction_time, String uuid) {
        this.transaction_sets_id = transaction_sets_id;
        this.description = description;
        this.transaction_time = transaction_time;
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaction_sets_id() {
        return transaction_sets_id;
    }

    public void setTransaction_sets_id(String transaction_sets_id) {
        this.transaction_sets_id = transaction_sets_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
