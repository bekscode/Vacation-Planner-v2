package com.romang.vacationplanner.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")

public class User {
    @PrimaryKey(autoGenerate = true)
    private int userID;
    private String username;
    private String passwordHash;

    //Generated constructor
    public User(int id, String username, String passwordHash) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User() {

    }

    //Generated getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
