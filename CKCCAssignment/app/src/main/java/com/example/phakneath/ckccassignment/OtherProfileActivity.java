package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import com.example.phakneath.ckccassignment.Fragment.myDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.myLostFragment;
import com.example.phakneath.ckccassignment.Fragment.otherDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.otherLostFragment;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileActivity extends AppCompatActivity implements View.OnClickListener{

    User user;
    CircleImageView profilePic, back;
    TextView username, posting, founds, losts;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String uid;
    PostingActivity postingActivity = new PostingActivity();
    otherDiscoverFragment found = new otherDiscoverFragment();
    otherLostFragment lost = new otherLostFragment();
    NestedScrollView scrollView;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    List<LostFound> itemFound = new ArrayList<>();
    List<LostFound> itemLost = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_other_profile);

        initView();
        getIntentUser();
        scrollView.setFillViewport (true);

        back.setOnClickListener(this::onClick);
    }

    public void getIntentUser()
    {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("otherUser");
        getLostsFounds(user);
    }

    public void initView()
    {
        profilePic = findViewById(R.id.profilePic);
        back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        posting = (TextView) findViewById(R.id.posts);
        founds = (TextView) findViewById(R.id.founds);
        losts = (TextView) findViewById(R.id.losts);
        //container = findViewById(R.id.container);
        scrollView = (NestedScrollView) findViewById (R.id.nest_scrollview);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
    }

    public void updateUI(User user)
    {
        username.setText(user.getUsername());
        if(user.getFounds() != null) itemFound = user.getFounds();
        if(user.getLosts() != null) itemLost = user.getLosts();

        founds.setText(itemFound.size() + "");
        losts.setText(itemLost.size() + "");
        int size = itemFound.size() + itemLost.size();
        posting.setText("Posts : " + size+ "");

        if(user.getImagePath() != null && user.getExtension() != null)
        {
            postingActivity.getImage(profilePic, user.getImagePath()+"."+user.getExtension(), this);
        }
    }

    public void sendDataToFragment(User user)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("founds", user);
        found.setArguments(bundle);
        lost.setArguments(bundle);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(found, "DISCOVER");
        adapter.addFrag(lost, "LOST");
        viewPager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void getLostsFounds(User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting").child("individual").child(user.getId());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> tlosts = new ArrayList<>();
                List<LostFound> tfounds = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.child("losts").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tlosts.add(lostFound);
                }
                for (DataSnapshot d: dataSnapshot.child("founds").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tfounds.add(lostFound);
                }

                user.setLosts(tlosts);
                user.setFounds(tfounds);
                //.Toast.makeText(ProfileActivity.this, "" + tfounds.size() + "" + tlosts.size(), Toast.LENGTH_SHORT).show();
                updateUI(user);
                sendDataToFragment(user);
                setupViewPager(viewPager);
                viewPagerTab.setViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
