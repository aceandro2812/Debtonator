package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/4/2017.
 */

public class TransactionSetPojo {
    String id = null;
    String nameString = null;
    String metadata = null;
    String created_at = null;

    public TransactionSetPojo(String id, String nameString, String metadata) {
        this.id = id;
        this.nameString = nameString;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    String updated_at = null;

}
