package com.example.phakneath.ckccassignment.Model;

import java.io.Serializable;

public class SaveLostFound implements Serializable{

    private String id;
    private String myOwnerID;
    private long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMyOwnerID() {
        return myOwnerID;
    }

    public void setMyOwnerID(String myOwnerID) {
        this.myOwnerID = myOwnerID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
