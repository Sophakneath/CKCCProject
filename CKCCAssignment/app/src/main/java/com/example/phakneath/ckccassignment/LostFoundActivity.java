package com.example.phakneath.ckccassignment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.ckccassignment.Adapter.myLostAdapter;
import com.example.phakneath.ckccassignment.Fragment.PostDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.PostLostFragment;
import com.example.phakneath.ckccassignment.Fragment.ViewDiscoverFragment;
import com.example.phakneath.ckccassignment.Fragment.ViewLostFragment;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.example.phakneath.ckccassignment.OtherProfileActivity.ViewPagerAdapter;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class LostFoundActivity extends AppCompatActivity implements View.OnClickListener, myLostAdapter.editPost{

    LinearLayout container;
    ViewDiscoverFragment viewDiscoverFragment;
    ViewDiscoverFragment viewLostFragment;
    CircleImageView back;
    TextView post;
    public static Activity activity;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);
        viewLostFragment = ViewDiscoverFragment.newInstance("","",false);
        viewDiscoverFragment = ViewDiscoverFragment.newInstance("","",true);

        initView();
        activity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        back.setOnClickListener(this::onClick);
        setupViewPager(viewPager);
        viewPagerTab.setViewPager(viewPager);
    }

    public void initView()
    {

        container = findViewById(R.id.container);
        back = findViewById(R.id.back);
        post = findViewById(R.id.posting);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab= (SmartTabLayout) findViewById(R.id.viewpagertab);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == post)
        {

        }
    }

    @Override
    public void onEditPost(LostFound lostFound) {
        Intent intent = new Intent(this, EditPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", lostFound);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(viewDiscoverFragment, "DISCOVER");
        adapter.addFrag(viewLostFragment, "LOST");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                viewDiscoverFragment.setImage(result);
                viewLostFragment.setImage(result);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
