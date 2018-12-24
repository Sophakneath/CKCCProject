package com.example.phakneath.ckccassignment.Model;

import java.io.Serializable;

public class Notification implements Serializable{

    String notiID;
    String location;
    String contact;
    String remark;
    String postID;
    String postOwnerID;
    String founderLosterID;

    public Notification(String notiID, String location, String contact, String remark, String postID, String postOwnerID, String founderLosterID) {
        this.notiID = notiID;
        this.location = location;
        this.contact = contact;
        this.remark = remark;
        this.postID = postID;
        this.postOwnerID = postOwnerID;
        this.founderLosterID = founderLosterID;
    }

    public Notification(){}

    public String getNotiID() {
        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostOwnerID() {
        return postOwnerID;
    }

    public void setPostOwnerID(String postOwnerID) {
        this.postOwnerID = postOwnerID;
    }

    public String getFounderLosterID() {
        return founderLosterID;
    }

    public void setFounderLosterID(String founderLosterID) {
        this.founderLosterID = founderLosterID;
    }
}
