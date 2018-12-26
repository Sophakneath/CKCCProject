package com.example.phakneath.ckccassignment.sharePreferences;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;



public class AppSingleton {



    private String playerId;
    private static AppSingleton instance;
    private AppSingleton(){

    }

    public static AppSingleton getInstance(){
        if(instance==null){
            instance=new AppSingleton();
        }
        return instance;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

}
