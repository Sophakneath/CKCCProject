package com.example.phakneath.ckccassignment;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.phakneath.ckccassignment.Fragment.ViewDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.ViewFoundFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class LostFoundActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout discover, found;
    LinearLayout container;
    ViewDiscoverFragment viewDiscoverFragment = new ViewDiscoverFragment();
    ViewFoundFragment viewFoundFragment = new ViewFoundFragment();
    ImageView bDiscover, bFound;
    CircleImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lost_found);

        initView();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        discover.setOnClickListener(this::onClick);
        found.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
        openDiscover();
    }

    public void initView()
    {
        discover = findViewById(R.id.discover);
        found = findViewById(R.id.found);
        container = findViewById(R.id.container);
        bDiscover = findViewById(R.id.backgroundDiscover);
        bFound = findViewById(R.id.backgroundFound);
        back = findViewById(R.id.back);
    }

    @Override
    public void onClick(View v) {
        if(v == discover)
        {
           openDiscover();
        }
        else if(v == found)
        {
            openFound();
        }
        else if(v == back)
        {
            finish();
        }
    }

    public void openDiscover()
    {
        bDiscover.setVisibility(View.VISIBLE);
        bFound.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, viewDiscoverFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openFound()
    {
        bFound.setVisibility(View.VISIBLE);
        bDiscover.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.container, viewFoundFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
