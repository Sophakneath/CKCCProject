package com.example.phakneath.ckccassignment.Model;

public class LostFound {

    private String id;
    private String item;
    private String location;
    private String contactNum;
    private String remark;

    public LostFound(String id, String item, String location, String contactNum, String remark) {
        this.id = id;
        this.item = item;
        this.location = location;
        this.contactNum = contactNum;
        this.remark = remark;
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
}
