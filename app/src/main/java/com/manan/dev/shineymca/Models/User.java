package com.manan.dev.shineymca.Models;

import java.util.HashMap;

public class User {
    private String userName;
    private String userUsername;
    private String userID;
    private String[] userClubsInterested;
    private HashMap<String,Integer> userSettingsMap;
    private String attendance;
    //TODO Add more properties here
    public User(){}
    public User(String userName, String userUsername, String userID) {
        this.userName = userName;
        this.userUsername = userUsername;
        this.userID = userID;
        //this.userSettingsMap = ???? ; define default settings here
    }
    public User(String userID , String attendance) {
        this.attendance = attendance;
        this.userID = userID;
        //this.userSettingsMap = ???? ; define default settings here
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String[] getUserClubsInterested() {
        return userClubsInterested;
    }

    public void setUserClubsInterested(String[] userClubsInterested) {
        this.userClubsInterested = userClubsInterested;
    }

    public HashMap<String, Integer> getUserSettingsMap() {
        return userSettingsMap;
    }

    public void setUserSettingsMap(HashMap<String, Integer> userSettingsMap) {
        this.userSettingsMap = userSettingsMap;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }
}
