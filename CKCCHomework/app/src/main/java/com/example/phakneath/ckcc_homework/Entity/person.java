package com.example.phakneath.ckcc_homework.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class person implements Serializable {

    String username;
    String email;
    String password;

    public person(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public person(){}

    public person(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
