package com.example.phakneath.ckccassignment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profilePic, back, setting, addPost;
    TextView username, posting, founds, losts;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    String id;
    PostingActivity postingActivity = new PostingActivity();
    User user;
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        initView();
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        setting.setOnClickListener(this::onClick);
        addPost.setOnClickListener(this::onClick);
        getUser();
    }

    public void initView()
    {
        profilePic = findViewById(R.id.profilePic);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);
        addPost = findViewById(R.id.addPost);
        username = findViewById(R.id.username);
        posting = findViewById(R.id.posting);
        founds = findViewById(R.id.founds);
        losts = findViewById(R.id.losts);
    }

    public void getUser()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("user").child("id").child(id);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
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
         if(user.getFounds() != null && user.getLosts() == null) {
             founds.setText(user.getFounds().size());
             posting.setText("Posts : " + user.getFounds().size());
         }
         else if(user.getLosts() != null && user.getFounds() == null)
         {
             losts.setText(user.getLosts().size());
             posting.setText("Posts : " + user.getLosts().size());
         }
         else if(user.getFounds() != null && user.getLosts() != null)
         {
             posting.setText("Posts : " + user.getFounds().size() + user.getLosts().size());
         }
         else
         {
             founds.setText("0");
             losts.setText("0");
             posting.setText("Posts : 0");
         }

        if(user.getImagePath() != null && user.getExtension() != null)
        {
            postingActivity.getImage(profilePic, user.getImagePath()+"."+user.getExtension(), this);
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
        else if(v == addPost)
        {
            startActivity(new Intent(this, LostFoundActivity.class));
        }
    }


}
