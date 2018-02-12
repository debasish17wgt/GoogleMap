package com.wgt.mapintegration.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by root on 10/2/18.
 */

@Entity
public class UserModel {

    @NonNull
    @PrimaryKey
    private String email;
    private String name;
    private String picLoc;
    @Ignore
    private boolean loggedStatus;

    public UserModel() {}

    public UserModel(String email, String name, String picLoc, boolean loggedStatus) {
        this.email = email;
        this.name = name;
        this.picLoc = picLoc;
        this.loggedStatus = loggedStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicLoc() {
        return picLoc;
    }

    public void setPicLoc(String picLoc) {
        this.picLoc = picLoc;
    }

    public boolean getLoggedStatus() {
        return loggedStatus;
    }

    public void setLoggedStatus(boolean loggedStatus) {
        this.loggedStatus = loggedStatus;
    }
}
