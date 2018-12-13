package com.example.phakneath.ckcc_homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    Button logout;
    TextView username, email;
    String u,e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.logout);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        if(!Preferences.isLogin(this))
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        Intent intent = getIntent();
        if (intent != null) {
            u = intent.getStringExtra("username");
            e = intent.getStringExtra("email");
        }

        username.setText(u);
        email.setText(e);
        logout.setOnClickListener(v->
        {
            Preferences.remove(this);
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
            finish();
        });
    }
}
