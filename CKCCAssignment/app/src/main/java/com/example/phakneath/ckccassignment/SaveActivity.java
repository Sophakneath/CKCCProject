package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Adapter.myFoundAdapter;
import com.example.phakneath.ckccassignment.Adapter.mySaveAdapter;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
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

public class SaveActivity extends AppCompatActivity implements View.OnClickListener, mySaveAdapter.openDetail {

    CircleImageView back;
    private List<SaveLostFound> saves;
    RecyclerView listSaves;
    mySaveAdapter mySaveAdapter;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    LostFound getlostFound;
    ProgressBar progressBar;
    TextView noPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_save);

        initView();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        progressBar.setVisibility(View.VISIBLE);
        //getSaves();
    }
    @Override
    protected void onStart() {
        super.onStart();
        getSaves();
    }

    public void initView()
    {
        back = findViewById(R.id.back);
        listSaves = findViewById(R.id.container);
        progressBar = findViewById(R.id.progress);
        noPost = findViewById(R.id.notpost);
    }

    public void setAdapter(List<SaveLostFound> saves)
    {
        progressBar.setVisibility(View.GONE);
        if(saves.size() <= 0) noPost.setVisibility(View.VISIBLE);
        else noPost.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listSaves.setLayoutManager(layoutManager);
        mySaveAdapter = new mySaveAdapter(this, saves, uid);
        listSaves.setAdapter(mySaveAdapter);
    }

    public void getSaves()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting").child("individual").child(uid).child("save");
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
                //if(savess.size() > 0) {
                    setAdapter(savess);
                    mySaveAdapter.openDetail = SaveActivity.this::onOpenDetail;
                //}
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
    }

    @Override
    public void onOpenDetail(SaveLostFound lostFound) {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        //getSaveFromOtherUser(lostFound);
        bundle.putSerializable("getSaveLostFound", lostFound);
        bundle1.putSerializable("user", null);
        intent.putExtras(bundle);
        intent.putExtras(bundle1);
        startActivity(intent);
    }
}
