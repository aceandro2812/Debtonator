package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/3/2017.
 */

public class PersonPojo {
    String id = null;
    String username = null;
    String phone = null;
    String email = null;
    String metadata = null;
    String uuid = null;
    String created = null;
    String updated = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public PersonPojo(String username, String phone, String email, String metadata, String uuid) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.metadata = metadata;
        this.uuid = uuid;
    }
}
