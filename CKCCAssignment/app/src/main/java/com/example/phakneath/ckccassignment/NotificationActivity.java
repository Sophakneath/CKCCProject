package com.example.phakneath.ckccassignment;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Fragment.receiveNotificationFragment;
import com.example.phakneath.ckccassignment.Fragment.sendNotificationFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity implements OnClickListener {

    ProgressBar progress;
    CircleImageView back;
    TextView noPost;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    sendNotificationFragment sendNotificationFragment;
    receiveNotificationFragment receiveNotificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initView();
        sendNotificationFragment = new sendNotificationFragment();
        receiveNotificationFragment = new receiveNotificationFragment();
        back.setOnClickListener(this::onClick);
        setFragment();
    }
    private void initView() {
        back = findViewById(R.id.back);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
    }

    public void setFragment()
    {
        setupViewPager(viewPager);
        viewPagerTab.setViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        OtherProfileActivity.ViewPagerAdapter adapter = new OtherProfileActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(receiveNotificationFragment, "RECEIVE");
        adapter.addFrag(sendNotificationFragment, "SEND");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
    }
}
