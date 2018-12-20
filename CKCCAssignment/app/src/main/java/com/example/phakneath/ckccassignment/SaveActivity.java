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

import com.example.phakneath.ckccassignment.Adapter.myFoundAdapter;
import com.example.phakneath.ckccassignment.Adapter.mySaveAdapter;
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

public class SaveActivity extends AppCompatActivity implements View.OnClickListener, mySaveAdapter.openDetail {

    CircleImageView back;
    private List<LostFound> saves;
    RecyclerView listSaves;
    mySaveAdapter mySaveAdapter;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
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
        getSaves();
    }

    public void initView()
    {
        back = findViewById(R.id.back);
        listSaves = findViewById(R.id.container);
    }

    public void setAdapter(List<LostFound> saves)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listSaves.setLayoutManager(layoutManager);
        mySaveAdapter = new mySaveAdapter(this, saves, uid);
        listSaves.setAdapter(mySaveAdapter);
    }

    public void getSaves()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("user").child("id").child(uid);
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

                setAdapter(savess);
                mySaveAdapter.openDetail = SaveActivity.this::onOpenDetail;
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
    public void onOpenDetail(LostFound lostFound) {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        bundle.putSerializable("getLostFound", lostFound);
        bundle1.putSerializable("user", null);
        intent.putExtras(bundle);
        intent.putExtras(bundle1);
        startActivity(intent);
    }
}
