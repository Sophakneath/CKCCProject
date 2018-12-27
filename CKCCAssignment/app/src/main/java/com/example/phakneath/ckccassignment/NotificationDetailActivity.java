package com.example.phakneath.ckccassignment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.ckccassignment.Adapter.foundListAdapter;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.Notification;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationDetailActivity extends AppCompatActivity implements View.OnClickListener {
    CircleImageView profile, back;
    TextView username, caption, atLocation, contactMe, myRemark, item, location, contact;
    ImageView picture, defaultpic, onSave, onUnSave, starYellow;
    CardView container;
    Notification notification;
    DatabaseReference mDatabase;
    PostingActivity postingActivity;
    foundListAdapter foundListAdapter;
    FirebaseAuth mAuth;
    String uID;
    SaveLostFound saveLostFound;
    RelativeLayout gotoProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        initView();
        postingActivity = new PostingActivity();
        foundListAdapter = new foundListAdapter();
        back.setOnClickListener(this::onClick);
        container.setOnClickListener(this::onClick);
        onSave.setOnClickListener(this::onClick);
        onUnSave.setOnClickListener(this::onClick);
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        gotoProfile.setOnClickListener(this::onClick);
        getData();
    }

    public void getData()
    {
        Intent intent = getIntent();
        notification = (Notification) intent.getSerializableExtra("notification");
        getUser(notification);
        updateUI(notification);
        getPosts(notification);
        setSavePosts(notification);
    }

    public void getPosts(Notification notification)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(notification.getPostID().startsWith("F")) {
            mDatabase.child("Posting").child("individual").child(notification.getPostOwnerID()).child("founds").child(notification.getPostID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound lostFound = dataSnapshot.getValue(LostFound.class);
                    if(lostFound != null) setUIPosts(lostFound);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(notification.getPostID().startsWith("L"))
        {
            mDatabase.child("Posting").child("individual").child(notification.getPostOwnerID()).child("losts").child(notification.getPostID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound lostFound = dataSnapshot.getValue(LostFound.class);
                    if(lostFound != null) setUIPosts(lostFound);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void setUIPosts(LostFound lostFound)
    {
        if(lostFound.getId().startsWith("F")) item.setText("Found : " + lostFound.getItem());
        else if(lostFound.getId().startsWith("L")) item.setText("Lost : " + lostFound.getItem());
        location.setText("Location : " + lostFound.getLocation());
        contact.setText("Contact : " + lostFound.getContactNum());

        if(lostFound.getImage() != null) {
            foundListAdapter.getImage(picture, lostFound.getImage(), this);
            defaultpic.setVisibility(View.GONE);
        }

        if(lostFound.getReward() != null)
        {
            starYellow.setVisibility(View.VISIBLE);
        }
    }

    public void updateUI(Notification notification)
    {
        if(notification.getPostID().startsWith("F"))
        {
            caption.setText("I have lost this item : ");
            atLocation.setText("Location Lost : " + notification.getLocation());
        }
        else if(notification.getPostID().startsWith("L"))
        {
            caption.setText("I have found this item : ");
            atLocation.setText("Location found : " + notification.getLocation());
        }
        contactMe.setText("Contact Me : " + notification.getContact());
        myRemark.setText("Remark : " + notification.getRemark());
    }

    public void getUser(Notification notification)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(notification.getFounderLosterID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String pathImage = dataSnapshot.child("imagePath").getValue(String.class);
                updateUIUser(username, pathImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUIUser(String name, String image)
    {
        username.setText(name);
        if(name != null) postingActivity.getImage(profile, image, this);
    }

    public void setSavePosts(Notification notification)
    {
        saveLostFound = new SaveLostFound();
        saveLostFound.setMyOwnerID(notification.getPostOwnerID());
        saveLostFound.setId(notification.getPostID());
        saveLostFound.setTime(System.currentTimeMillis());
        getSaves(saveLostFound);
    }

    private void initView() {
        profile = findViewById(R.id.profilePic);
        username = findViewById(R.id.username);
        caption = findViewById(R.id.caption);
        atLocation = findViewById(R.id.atLocation);
        contactMe = findViewById(R.id.contactMe);
        myRemark = findViewById(R.id.myRemark);
        item = findViewById(R.id.found);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        picture = findViewById(R.id.imageFound);
        defaultpic = findViewById(R.id.defaultpic);
        onSave = findViewById(R.id.onsave);
        onUnSave = findViewById(R.id.notsave);
        container = findViewById(R.id.containerFound);
        back = findViewById(R.id.back);
        container = findViewById(R.id.container);
        starYellow= findViewById(R.id.staryellow);
        gotoProfile = findViewById(R.id.gotoProfile);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == container)
        {
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle bundle = new Bundle();
            Bundle bundle1 = new Bundle();
            bundle.putSerializable("getSaveLostFound", saveLostFound);
            bundle1.putSerializable("user", null);
            intent.putExtras(bundle);
            intent.putExtras(bundle1);
            startActivity(intent);
        }
        else if(v == onSave)
        {
            onUnSave(saveLostFound);
        }
        else if(v == onUnSave)
        {
            onSave(saveLostFound);
        }
        else if(v == gotoProfile)
        {
            if(notification.getPostOwnerID().equals(uID))
            {
                myOwnProfile();
            }
            else
            {
                getOtherProfile();
            }
        }
    }

    public void onSave(SaveLostFound lostFound)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("save").child(lostFound.getId()).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSave.setVisibility(View.VISIBLE);
                onUnSave.setVisibility(View.GONE);
                Toast.makeText(NotificationDetailActivity.this, "Save Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(NotificationDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUnSave(SaveLostFound lostFound)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("save").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSave.setVisibility(View.GONE);
                onUnSave.setVisibility(View.VISIBLE);
                Toast.makeText(NotificationDetailActivity.this, "Unsave Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getSaves(SaveLostFound lf)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting").child("individual").child(uID).child("save");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<SaveLostFound> savess = new ArrayList<>();
                SaveLostFound lostFound = new SaveLostFound();

                for(DataSnapshot d: dataSnapshot.getChildren())
                {
                    lostFound = d.getValue(SaveLostFound.class);
                    savess.add(lostFound);
                }

                if(savess.size() >0)
                    for (SaveLostFound l: savess) {
                        if(l.getId().equals(lf.getId()))
                        {
                            onSave.setVisibility(View.VISIBLE);
                            onUnSave.setVisibility(View.GONE);
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void myOwnProfile()
    {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void otherProfile(User user)
    {
        Intent intent = new Intent(this, OtherProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("otherUser", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void getOtherProfile()
    {
        User otherUser = new User();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(notification.getPostOwnerID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tusername= dataSnapshot.child("username").getValue(String.class);
                String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                String textension = dataSnapshot.child("extension").getValue(String.class);
                String id = dataSnapshot.child("id").getValue(String.class);

                otherUser.setUsername(tusername);
                otherUser.setImagePath(timagepath);
                otherUser.setExtension(textension);
                otherUser.setId(id);
                //otherUser.setPhoneNum(tphoneNum);
                //otherUser.setEmail(temail);
                getFoundsLosts(otherUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFoundsLosts(User otherUser)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(notification.getPostOwnerID()).child("losts").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> tlosts = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tlosts.add(lostFound);
                }

                otherUser.setLosts(tlosts);
                getFounds(otherUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getFounds(User otherUser)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(notification.getPostOwnerID()).child("founds").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> tfounds = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tfounds.add(lostFound);
                }

                otherUser.setFounds(tfounds);
                otherProfile(otherUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
