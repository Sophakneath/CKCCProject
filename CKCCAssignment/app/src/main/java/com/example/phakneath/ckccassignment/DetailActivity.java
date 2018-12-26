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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.foundListAdapter;
import com.example.phakneath.ckccassignment.Dialog.FounderDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    CircleImageView back;
    TextView username, found, location, contact, remark, rewardDes;
    CircleImageView profile;
    ImageView more, star, picture, defaultpic;
    LostFound lostFound;
    RelativeLayout save, share, gotoProfile;
    User user;
    PostingActivity postingActivity;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    int count;
    ImageView onSave, notsave;
    List<LostFound> saves;
    SaveLostFound saveLostFound;
    Button founder;
    FounderDialog dialog;
    foundListAdapter foundListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        postingActivity = new PostingActivity();
        foundListAdapter = new foundListAdapter();
        lostFound = new LostFound();
        dialog = new FounderDialog();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        gotoProfile.setOnClickListener(this::onClick);
        founder.setOnClickListener(this::onClick);
        onSave.setOnClickListener(this::onClick);
        notsave.setOnClickListener(this::onClick);
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
            if(user.getImagePath() != null)
            {
                postingActivity.getImage(profile, user.getImagePath(), this);
            }
        }

        if(lostFound.getId().startsWith("F")) {
            found.setText("Found : " + lostFound.getItem());
            founder.setText("I Lost it");
        }
        else if(lostFound.getId().startsWith("L"))
        {
            found.setText("Lost : " + lostFound.getItem());
            founder.setText("I Found it");
        }

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
        if(lostFound.getImage() != null) {foundListAdapter.getImage(picture,lostFound.getImage(),this); defaultpic.setVisibility(View.GONE);}

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
        founder = findViewById(R.id.founder);
        defaultpic = findViewById(R.id.defaultpic);
    }

    public void getSaves(LostFound lf)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(uid).child("save").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<LostFound> savess = new ArrayList<>();
                LostFound lostFound = new LostFound();

                for(DataSnapshot d: dataSnapshot.getChildren())
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
            saveLostFound.setTime(System.currentTimeMillis());
        }
        else saveLostFound.setTime(System.currentTimeMillis());
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
            saveLostFound.setTime(System.currentTimeMillis());
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
        Query query = mDatabase.child("Posting").child("individual").child(lostFound.getMyOwner()).child("losts").orderByChild("time");
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
        Query query = mDatabase.child("Posting").child("individual").child(lostFound.getMyOwner()).child("founds").orderByChild("time");
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

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == onSave)
        {
            onUnSave(saveLostFound);
        }
        else if(v == notsave)
        {
            onSave(saveLostFound);
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
        else if(v == founder)
        {
            if(saveLostFound == null)
            {
                SaveLostFound saveLostFound = new SaveLostFound();
                saveLostFound.setMyOwnerID(lostFound.getMyOwner());
                saveLostFound.setId(lostFound.getId());
                saveLostFound.setTime(lostFound.getTime());
                dialog.setData(saveLostFound);
            }
            else dialog.setData(saveLostFound);

            dialog.show(getFragmentManager(), "myFounderDialog");
        }
    }
}
