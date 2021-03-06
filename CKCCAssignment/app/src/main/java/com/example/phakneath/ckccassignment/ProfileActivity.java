package com.example.phakneath.ckccassignment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.mySaveAdapter;
import com.example.phakneath.ckccassignment.Fragment.myDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.myLostFragment;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profilePic, back, setting;
    TextView username, posting, founds, losts;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String uid;
    PostingActivity postingActivity = new PostingActivity();
    User user;
    RecyclerView container;
    myDiscoverFragment myDiscoverFragment;
    myLostFragment myLostFragment;
    List<LostFound> lostFounds;
    NestedScrollView scrollView;
    public static Activity activity;
    FloatingActionButton add;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        myDiscoverFragment = new myDiscoverFragment();
        myLostFragment = new myLostFragment();

        initView();
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        setting.setOnClickListener(this::onClick);
        add.setOnClickListener(this::onClick);

        scrollView.setFillViewport (true);
        setupViewPager(viewPager);

        viewPagerTab.setViewPager(viewPager);
        getUser();
    }

    public void initView()
    {
        profilePic = findViewById(R.id.profilePic);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);
        username = findViewById(R.id.username);
        posting = (TextView) findViewById(R.id.posts);
        founds = (TextView) findViewById(R.id.founds);
        losts = (TextView) findViewById(R.id.losts);
        //container = findViewById(R.id.container);
        scrollView = (NestedScrollView) findViewById (R.id.nest_scrollview);
        add = findViewById(R.id.add);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
    }

    public void getUser()
    {
        user = new User();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("user").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tusername= dataSnapshot.child("username").getValue(String.class);
                String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                String textension = dataSnapshot.child("extension").getValue(String.class);
                String tphoneNum = dataSnapshot.child("phoneNum").getValue(String.class);
                String temail = dataSnapshot.child("email").getValue(String.class);

                user.setUsername(tusername);
                user.setImagePath(timagepath);
                user.setExtension(textension);
                user.setPhoneNum(tphoneNum);
                user.setEmail(temail);

                getLostsFounds(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getLostsFounds(User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(uid).child("losts").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> tlosts = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tlosts.add(lostFound);
                }

                user.setLosts(tlosts);
                getFounds(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFounds(User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(uid).child("founds").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> tfounds = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tfounds.add(lostFound);
                }

                user.setFounds(tfounds);
                updateUI(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUI(User user)
    {
        username.setText(user.getUsername());
        founds.setText(user.getFounds().size() + "");
        losts.setText(user.getLosts().size() + "");
        int size = user.getFounds().size() + user.getLosts().size();
        posting.setText("Posts : " + size+ "");

        if(user.getImagePath() != null)
        {
            postingActivity.getImage(profilePic, user.getImagePath(), this);
        }
    }

    public void gotoSetting()
    {
        Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == setting)
        {
            gotoSetting();
        }
        else if(v == add)
        {
            startActivity(new Intent(this, LostFoundActivity.class));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        OtherProfileActivity.ViewPagerAdapter adapter = new OtherProfileActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(myDiscoverFragment, "DISCOVER");
        adapter.addFrag(myLostFragment, "LOST");
        viewPager.setAdapter(adapter);
    }


}
