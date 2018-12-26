package com.example.phakneath.ckccassignment.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String username;
    private String email;
    private String phoneNum;
    private List<LostFound> losts;
    private List<LostFound> founds;
    private String imagePath;
    private String extension;
    private String playerId;
    private List<SaveLostFound> save;

    public User(){}

    public User(String id,String playerId, String username, String email, String phoneNum, List<LostFound> losts, List<LostFound> founds, String imagePath, String extension, List<SaveLostFound> save) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        this.losts = losts;
        this.founds = founds;
        this.imagePath = imagePath;
        this.extension = extension;
        this.save = save;
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<LostFound> getLosts() {
        return losts;
    }

    public void setLosts(List<LostFound> losts) {
        this.losts = losts;
    }

    public List<LostFound> getFounds() {
        return founds;
    }

    public void setFounds(List<LostFound> founds) {
        this.founds = founds;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<SaveLostFound> getSave() {
        return save;
    }

    public void setSave(List<SaveLostFound> save) {
        this.save = save;
    }
}
