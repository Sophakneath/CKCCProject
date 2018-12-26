package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.phakneath.ckccassignment.sharePreferences.AppSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String playerId ;
    FirebaseDatabase fdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MARK: Notification with onesignal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        playerId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        AppSingleton.getInstance().setPlayerId(playerId);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();

        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified())
        {
            String path = "user/"+mAuth.getCurrentUser().getUid() + "/playerId";
            fdb.getReference(path).setValue(playerId);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    startActivity(new Intent(MainActivity.this, PostingActivity.class));
                    finish();
                }
            }, 3000);
        }
        else
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }, 5000);
        }
    }
}
