package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener{

    EditText item, location, contact, remark, reward;
    ImageView star, staryellow, picture, defaultpic;
    CircleImageView back;
    Button save;
    LinearLayout rewardLayout;
    TextView des;
    LostFound lostFound;
    ProgressBar progress;
    String uID;
    DatabaseReference mDatabase;
    public static final int OPEN_GALLERY = 1;
    static StorageTask mUploadTask;
    StorageReference ref;
    StorageReference storageReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        initView();
        getLostFound();
        star.setOnClickListener(this::onClick);
        staryellow.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
        picture.setOnClickListener(this::onClick);
        defaultpic.setOnClickListener(this::onClick);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
    }

    public void getLostFound()
    {
        Intent intent = getIntent();
        lostFound = (LostFound) intent.getSerializableExtra("data");
        updateUI();
    }

    private void updateUI() {
        item.setText(lostFound.getItem());
        location.setText(lostFound.getLocation());
        contact.setText(lostFound.getContactNum());
        remark.setText(lostFound.getRemark());
        if(lostFound.getReward() != null)
        {
            rewardLayout.setVisibility(View.VISIBLE);
            staryellow.setVisibility(View.VISIBLE);
            reward.setVisibility(View.VISIBLE);
            des.setVisibility(View.VISIBLE);
            reward.setText(lostFound.getReward());
        }
        else
        {
            rewardLayout.setVisibility(View.GONE);
            staryellow.setVisibility(View.GONE);
            reward.setVisibility(View.GONE);
            des.setVisibility(View.GONE);
            reward.setText(lostFound.getReward());
        }
        item.setSelection(item.getText().length());
    }

    private void initView() {
        item = findViewById(R.id.item);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        remark = findViewById(R.id.Remark);
        reward = findViewById(R.id.rewardDes);
        star = findViewById(R.id.star);
        staryellow = findViewById(R.id.staryellow);
        picture = findViewById(R.id.picture);
        defaultpic = findViewById(R.id.defaultpic);
        save = findViewById(R.id.save);
        rewardLayout = findViewById(R.id.reward);
        des = findViewById(R.id.des);
        progress = findViewById(R.id.progress);
        back = findViewById(R.id.back);
        picture = findViewById(R.id.picture);
        defaultpic = findViewById(R.id.defaultpic);

    }

    public void updateUser()
    {
        progress.setVisibility(View.VISIBLE);
        String items = item.getText().toString();
        String loc = location.getText().toString();
        String con = contact.getText().toString();
        String rem = null;
        String id = lostFound.getId();
        String rewardDes = null;

        if(!TextUtils.isEmpty(remark.getText())) rem = remark.getText().toString();
        if(!TextUtils.isEmpty(reward.getText())) rewardDes = reward.getText().toString();
        LostFound lostFound = new LostFound(id, items,loc,con,rem,rewardDes, uID);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(lostFound.getId().startsWith("F"))
        {
            mDatabase.child("user").child("id").child(uID).child("founds").child(id).setValue(lostFound);
            mDatabase.child("Posting").child("founds").child(id).setValue(lostFound);
        }
        else if(lostFound.getId().startsWith("L"))
        {
            mDatabase.child("user").child("id").child(uID).child("losts").child(id).setValue(lostFound);
            mDatabase.child("Posting").child("losts").child(id).setValue(lostFound);
        }
    }

    public void verifyUpdate()
    {
        if(TextUtils.isEmpty(item.getText()) || TextUtils.isEmpty(location.getText()) || TextUtils.isEmpty(contact.getText()))
        {
            Toast.makeText(this, "Please enter the above information", Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateUser();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
            Toast.makeText(this, "Edit Successfull", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == star)
        {
            star.setVisibility(View.GONE);
            staryellow.setVisibility(View.VISIBLE);
            des.setVisibility(View.VISIBLE);
            reward.setVisibility(View.VISIBLE);
        }
        else if(v == staryellow)
        {
            star.setVisibility(View.VISIBLE);
            staryellow.setVisibility(View.GONE);
            des.setVisibility(View.VISIBLE);
            reward.setVisibility(View.VISIBLE);
        }
        else if(v == save)
        {
            verifyUpdate();
        }
        else if(v == back)
        {
            finish();
        }
        else if(v == picture)
        {

        }
        else if(v == defaultpic)
        {

        }
    }
}