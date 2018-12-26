package com.example.phakneath.ckccassignment.Model;

import java.io.Serializable;

public class LostFound implements Serializable {

    private String id;
    private String item;
    private String location;
    private String contactNum;
    private String remark;
    private String reward;
    private String myOwner;
    private String image;
    private String extension;
    private String playerId;
    private long time;

    public LostFound(){}

    public LostFound(String id, String item, String location, String contactNum, String remark, String reward, String myOwner, String image, String extension, String playerId, long time) {
        this.id = id;
        this.item = item;
        this.location = location;
        this.contactNum = contactNum;
        this.remark = remark;
        this.reward = reward;
        this.myOwner = myOwner;
        this.image = image;
        this.extension = extension;
        this.playerId = playerId;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getMyOwner() {
        return myOwner;
    }

    public void setMyOwner(String myOwner) {
        this.myOwner = myOwner;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
