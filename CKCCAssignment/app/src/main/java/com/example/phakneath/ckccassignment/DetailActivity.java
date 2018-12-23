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
import com.example.phakneath.ckccassignment.Fragment.myDiscoverFragment;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    LostFound lostFound = new LostFound();
    RelativeLayout save, share, gotoProfile;
    User user;
    PostingActivity postingActivity = new PostingActivity();
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    int count;
    ImageView onSave, notsave;
    List<LostFound> saves;
    SaveLostFound saveLostFound;
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
        save.setOnClickListener(this::onClick);
        gotoProfile.setOnClickListener(this::onClick);
        getUser();
    }

    public void getUser()
    {
        Intent intent = getIntent();
        lostFound = (LostFound) intent.getSerializableExtra("getLostFound");
        user = (User) intent.getSerializableExtra("user");
        saveLostFound = (SaveLostFound) intent.getSerializableExtra("getSaveLostFound");
        //Toast.makeText(this, "" + lostFound, Toast.LENGTH_SHORT).show();

        if(saveLostFound != null)
        {
            getSaveFromOtherUser(saveLostFound);
        }
        else {
            if (user == null) {
                if (lostFound != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase = mDatabase.child("user").child(lostFound.getMyOwner());
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String tusername = dataSnapshot.child("username").getValue(String.class);
                            String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                            String textension = dataSnapshot.child("extension").getValue(String.class);

                            user = new User(null, tusername, null, null, null, null, timagepath, textension, null);
                            updateUI(lostFound, user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            else updateUI(lostFound,user);
        }

        if(lostFound != null) getSaves(lostFound);
    }

    public void getSaveFromOtherUser(SaveLostFound saveLostFound)
    {
        if(saveLostFound.getId().startsWith("F")) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(saveLostFound.getMyOwnerID()).child("founds").child(saveLostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostFound = dataSnapshot.getValue(LostFound.class);
                    getLostFound(lostFound);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (saveLostFound.getId().startsWith("L"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(saveLostFound.getMyOwnerID()).child("losts").child(saveLostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostFound = dataSnapshot.getValue(LostFound.class);
                    getLostFound(lostFound);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getLostFound(LostFound lostFound)
    {
        if(user == null)
        {
            if(lostFound != null) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase = mDatabase.child("user").child(lostFound.getMyOwner());
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String tusername = dataSnapshot.child("username").getValue(String.class);
                        String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                        String textension = dataSnapshot.child("extension").getValue(String.class);

                        user = new User(null, tusername, null, null, null, null, timagepath, textension, null);
                        updateUI(lostFound, user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else updateUI(lostFound,user);
            getSaves(lostFound);
        }
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
        if(lostFound == null) {
            lostFound = new LostFound();
            lostFound.setId(saveLostFound.getId());
            lostFound.setMyOwner(saveLostFound.getMyOwnerID());
        }

        Intent intent = new Intent(this, EditPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", lostFound);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onDeletePost()
    {
        String id;
        if(lostFound == null) {
            lostFound = new LostFound();
            lostFound.setId(saveLostFound.getId());
        }

        id = lostFound.getId();
        if (lostFound.getId().startsWith("F")) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(uid).child("founds").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("Posting").child("founds").child(id).removeValue();
                    mDatabase.child("Posting").child("individual").child(uid).child("save").child(id).removeValue();
                    finish();
                    Toast.makeText(DetailActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (lostFound.getId().startsWith("L")) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(uid).child("losts").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("Posting").child("losts").child(id).removeValue();
                    mDatabase.child("Posting").child("individual").child(uid).child("save").child(id).removeValue();
                    finish();
                    Toast.makeText(DetailActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void reloadLists()
    {
        String id;
        if(lostFound == null) {
            lostFound = new LostFound();
            lostFound.setId(saveLostFound.getId());
        }

        id = lostFound.getId();
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
        onSave = findViewById(R.id.onsave);
        notsave = findViewById(R.id.notsave);
        gotoProfile = findViewById(R.id.gotoProfile);
    }

    public void getSaves(LostFound lf)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting").child("individual").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LostFound> savess = new ArrayList<>();
                LostFound lostFound = new LostFound();

                for(DataSnapshot d: dataSnapshot.child("save").getChildren())
                {
                    lostFound = d.getValue(LostFound.class);
                    savess.add(lostFound);
                }

                saves = savess;

                if(savess.size() >0)
                    for (LostFound l: savess) {
                        if(l.getId().equals(lf.getId()))
                        {
                            onSave.setVisibility(View.VISIBLE);
                            notsave.setVisibility(View.GONE);
                            count = 1;
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onSave(SaveLostFound saveLostFound)
    {
        if(saveLostFound == null)
        {
            saveLostFound = new SaveLostFound();
            saveLostFound.setId(lostFound.getId());
            saveLostFound.setMyOwnerID(lostFound.getMyOwner());
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uid).child("save").child(saveLostFound.getId()).setValue(saveLostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSave.setVisibility(View.VISIBLE);
                notsave.setVisibility(View.GONE);
                Toast.makeText(DetailActivity.this, "Save Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUnSave(SaveLostFound saveLostFound)
    {
        if(saveLostFound == null)
        {
            saveLostFound = new SaveLostFound();
            saveLostFound.setId(lostFound.getId());
            saveLostFound.setMyOwnerID(lostFound.getMyOwner());
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uid).child("save").child(saveLostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSave.setVisibility(View.GONE);
                notsave.setVisibility(View.VISIBLE);
                Toast.makeText(DetailActivity.this, "Unsave Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        mDatabase.child("user").child(lostFound.getMyOwner()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tusername= dataSnapshot.child("username").getValue(String.class);
                String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                String textension = dataSnapshot.child("extension").getValue(String.class);
                String id = dataSnapshot.child("id").getValue(String.class);

                /*List<LostFound> tlosts = new ArrayList<>();
                List<LostFound> tfounds = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.child("losts").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tlosts.add(lostFound);
                }
                for (DataSnapshot d: dataSnapshot.child("founds").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tfounds.add(lostFound);
                }*/

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
        mDatabase = mDatabase.child("Posting").child("individual").child(lostFound.getMyOwner());
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

                otherUser.setLosts(tlosts);
                otherUser.setFounds(tfounds);
                otherProfile(otherUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == save)
        {
            if(count == 0)
            {
                count = 1;
                onSave(saveLostFound);
            }
            else if(count == 1)
            {
                count = 0;
                onUnSave(saveLostFound);
            }
        }
        else if(v == gotoProfile)
        {
            if(lostFound.getMyOwner().equals(uid))
            {
                myOwnProfile();
            }
            else
            {
                getOtherProfile();
            }
        }
    }
}
