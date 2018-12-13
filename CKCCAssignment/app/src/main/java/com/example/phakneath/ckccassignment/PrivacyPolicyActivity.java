package com.example.phakneath.ckccassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivacyPolicyActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView web;
    private CircleImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        initView();
        web.loadUrl("file:///android_asset/privacypolicy.html");
        backBtn.setOnClickListener(this::onClick);
    }

    private void initView() {
        web = findViewById(R.id.web);
        backBtn = findViewById(R.id.back);
    }

    @Override
    public void onClick(View v) {
        if(v == backBtn)
        {
            finish();
        }
    }
}
