package com.example.phakneath.ckccassignment;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Adapter.myFoundAdapter;
import com.example.phakneath.ckccassignment.Adapter.notificationAdapter;
import com.example.phakneath.ckccassignment.Model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity implements OnClickListener {

    RecyclerView notificationContainer;
    ProgressBar progress;
    CircleImageView back;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uID;
    notificationAdapter notificationAdapter;
    TextView noPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_notification);

        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        getNotification();
    }
    private void initView() {
        notificationContainer = findViewById(R.id.listNotification);
        progress = findViewById(R.id.progress);
        back = findViewById(R.id.back);
        noPost = findViewById(R.id.noPost);
    }

    public void setAdapter(List<Notification> notifications)
    {
        progress.setVisibility(View.GONE);
        if(notifications.size() <= 0) noPost.setVisibility(View.VISIBLE);
        else noPost.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        notificationContainer.setLayoutManager(layoutManager);
        notificationAdapter = new notificationAdapter(this, notifications);
        notificationContainer.setAdapter(notificationAdapter);
    }

    public void getNotification()
    {
        progress.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Notification> notifications = new ArrayList<>();
                Notification notification = new Notification();
                for(DataSnapshot d: dataSnapshot.getChildren())
                {
                    notification = d.getValue(Notification.class);
                    notifications.add(notification);
                }
                setAdapter(notifications);

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
}
