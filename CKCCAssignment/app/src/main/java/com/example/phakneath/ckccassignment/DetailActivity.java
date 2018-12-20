package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Fragment.PostDiscoverFragment;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    CircleImageView back;
    TextView username, found, location, contact, remark, rewardDes;
    CircleImageView profile;
    ImageView more, star, picture;
    LostFound lostFound;
    RelativeLayout save, share;
    User user;
    PostingActivity postingActivity = new PostingActivity();
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        initView();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        getUser();
    }

    public void getUser()
    {
        Intent intent = getIntent();
        lostFound = (LostFound) intent.getSerializableExtra("getLostFound");
        user = (User) intent.getSerializableExtra("user");
        //Toast.makeText(this, "" + lostFound, Toast.LENGTH_SHORT).show();

        if(user == null)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase = mDatabase.child("user").child("id").child(lostFound.getMyOwner());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String tusername= dataSnapshot.child("username").getValue(String.class);
                    String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                    String textension = dataSnapshot.child("extension").getValue(String.class);

                    user = new User(null, tusername, null,null,null,null,timagepath,textension,null);
                    updateUI(lostFound,user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(user != null) updateUI(lostFound, user);
    }

    public void updateUI(LostFound lostFound, User user)
    {
        if(user != null)
        {
            username.setText(user.getUsername());
            if(user.getImagePath() != null && user.getExtension() != null)
            {
                postingActivity.getImage(profile, user.getImagePath()+"."+user.getExtension(), this);
            }
        }
        found.setText("Found : " + lostFound.getItem());
        location.setText("Location : " +lostFound.getLocation());
        contact.setText("Contact : " +lostFound.getContactNum());
        remark.setText("Remark : " +lostFound.getRemark());
        if(lostFound.getReward() != null)
        {
            rewardDes.setText("Reward Description : " +lostFound.getReward());
            star.setVisibility(View.VISIBLE);
        }
        else rewardDes.setText("Reward Description : None");

        if(lostFound.getMyOwner().equals(uid)) more.setVisibility(View.VISIBLE);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(DetailActivity.this, v);
                menu.inflate(R.menu.more_menu);
                menu.setGravity(Gravity.RIGHT);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                //Toast.makeText(context, "Eidt", Toast.LENGTH_SHORT).show();
                                openEditPost();
                                reloadLists();
                                return true;
                            case R.id.delete:
                                onDeletePost();
                                return true;
                            default:
                                return false;
                        }

                    }
                });

                menu.show();
            }
        });

    }

    public void openEditPost()
    {
        Intent intent = new Intent(this, EditPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", lostFound);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onDeletePost()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(lostFound.getId().startsWith("F")) {
            mDatabase.child("user").child("id").child(uid).child("founds").child(lostFound.getId()).removeValue();
            mDatabase.child("Posting").child("founds").child(lostFound.getId()).removeValue();
        }
        else if(lostFound.getId().startsWith("L"))
        {
            mDatabase.child("user").child("id").child(uid).child("losts").child(lostFound.getId()).removeValue();
            mDatabase.child("Posting").child("losts").child(lostFound.getId()).removeValue();
        }
        finish();
        Toast.makeText(DetailActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
    }

    public void reloadLists()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(lostFound.getId().startsWith("F")) {
            mDatabase = mDatabase.child("Posting").child("founds").child(lostFound.getId());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound found = dataSnapshot.getValue(LostFound.class);
                    lostFound = found;
                    updateUI(lostFound, null);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(lostFound.getId().startsWith("L"))
        {
            mDatabase = mDatabase.child("Posting").child("losts").child(lostFound.getId());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound lost = dataSnapshot.getValue(LostFound.class);
                    lostFound = lost;
                    updateUI(lostFound, null);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void initView()
    {
        back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        found = findViewById(R.id.found);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        remark = findViewById(R.id.Remark);
        rewardDes = findViewById(R.id.rewardDes);
        profile = findViewById(R.id.profilePic);
        more = findViewById(R.id.more);
        star = findViewById(R.id.rewardStar);
        save = findViewById(R.id.save);
        share = findViewById(R.id.shareTo);
        picture = findViewById(R.id.picture);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
    }
}
