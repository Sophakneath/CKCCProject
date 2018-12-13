package com.example.phakneath.ckccassignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phakneath.ckccassignment.Dialog.LoadingDialog;
import com.example.phakneath.ckccassignment.Fragment.PostDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.PostFoundFragment;
import com.example.phakneath.ckccassignment.Fragment.ViewDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.ViewFoundFragment;
import com.example.phakneath.ckccassignment.sharePreferences.UserPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    Button logout;
    FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    CircleImageView iconMenu, profile;
    NavigationView navigationView;
    RelativeLayout header;
    ImageView search;
    TextView name;
    DatabaseReference mDatabase;
    String id;
    FirebaseStorage storage;
    public static Activity activity;
    LoadingDialog dialog = new LoadingDialog();
    RelativeLayout discover, found;
    LinearLayout container;
    PostDiscoverFragment postDiscoverFragment = new PostDiscoverFragment();
    PostFoundFragment postFoundFragment = new PostFoundFragment();
    ImageView bDiscover, bFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_posting);

        initView();
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();
        iconMenu.setOnClickListener(this::onClick);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        header.setOnClickListener(this::onClick);
        search.setOnClickListener(this::onClick);
        discover.setOnClickListener(this::onClick);
        found.setOnClickListener(this::onClick);
        getUser();
        openDiscover();
    }

    public void initView()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        iconMenu = findViewById(R.id.iconMenu);
        navigationView = findViewById(R.id.nav);

        View headerView = navigationView.getHeaderView(0);
        header = headerView.findViewById(R.id.gotoProfile);
        profile = headerView.findViewById(R.id.profile);
        name = headerView.findViewById(R.id.username);
        container = findViewById(R.id.container);
        search = findViewById(R.id.searchView);
        discover = findViewById(R.id.discover);
        found = findViewById(R.id.found);
        bDiscover = findViewById(R.id.backgroundDiscover);
        bFound = findViewById(R.id.backgroundFound);
    }


    @Override
    public void onClick(View v) {
        if(v == iconMenu)
        {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        else if(v == header)
        {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if(v == search)
        {
            startActivity(new Intent(this, DetailActivity.class));
        }
        else if(v == discover)
        {
            openDiscover();
        }
        else if(v == found)
        {
            openFound();
        }
    }

    public void signout()
    {
        mAuth.signOut();
        UserPreferences.remove(this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PostingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    public void logoutDialog()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show(getFragmentManager(), "dialogLoading");
            }
        }, 500);

    }

    public void getUser()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("user").child("id").child(id);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String imagePath = dataSnapshot.child("imagePath").getValue(String.class);
                String extension = dataSnapshot.child("extension").getValue(String.class);

                updateUI(username,imagePath,extension);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getImage(ImageView img, String getImage, Context context)
    {
        storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("profile/" + getImage);
        try {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Glide.with(context).load(uri).into(img);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUI(String username, String imagePath, String extension)
    {
        name.setText(username);
        if(imagePath != null && extension != null)
        getImage(profile, imagePath+"."+extension, this);
    }

    public void openDiscover()
    {
        bDiscover.setVisibility(View.VISIBLE);
        bFound.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, postDiscoverFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openFound()
    {
        bFound.setVisibility(View.VISIBLE);
        bDiscover.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.container, postFoundFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.logout: logoutDialog(); signout(); dialog.dismiss();
                break;
            case R.id.home: drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.save: startActivity(new Intent(this, SaveActivity.class));
                break;
            case R.id.privacy: startActivity(new Intent(this, PrivacyPolicyActivity.class));
                break;
        }
        return true;
    }
}
