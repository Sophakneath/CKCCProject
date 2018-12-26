package com.example.phakneath.ckccassignment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.foundListAdapter;
import com.example.phakneath.ckccassignment.Dialog.LoadingDialog;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.example.phakneath.ckccassignment.sharePreferences.AppSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    String uID, pathImage;
    DatabaseReference mDatabase;
    public static final int OPEN_GALLERY = 1;
    static StorageTask mUploadTask;
    StorageReference ref;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    int count=0;
    Uri uri, mCropImageUri;
    foundListAdapter foundListAdapter;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        foundListAdapter = new foundListAdapter();
        getLostFound();
        star.setOnClickListener(this::onClick);
        staryellow.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
        picture.setOnClickListener(this::onClick);
        defaultpic.setOnClickListener(this::onClick);
        loadingDialog = new LoadingDialog();

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
    }

    public void getLostFound()
    {
        Intent intent = getIntent();
        lostFound = (LostFound) intent.getSerializableExtra("data");
        if(lostFound.getItem() == null)
        {
            getLostFoundFromDatabase(lostFound);
        }
        else updateUI(lostFound);
    }

    public void getLostFoundFromDatabase(LostFound lostFound)
    {
        if(lostFound.getId().startsWith("F")) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(uID).child("founds").child(lostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound found = dataSnapshot.getValue(LostFound.class);
                    updateUI(found);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(lostFound.getId().startsWith("L"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(uID).child("losts").child(lostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LostFound found = dataSnapshot.getValue(LostFound.class);
                    updateUI(found);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUI(LostFound lostFound) {
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
        if(lostFound.getImage() != null){ foundListAdapter.getImage(picture,lostFound.getImage(),this); defaultpic.setVisibility(View.GONE);}
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
        //progress.setVisibility(View.VISIBLE);
        loadingDialog.show(getFragmentManager(), "editLoading");
        String items = item.getText().toString();
        String loc = location.getText().toString();
        String con = contact.getText().toString();
        String rem = null;
        String id = lostFound.getId();
        String rewardDes = null;
        long time = lostFound.getTime();
        String image;
        if(pathImage == null) image = lostFound.getImage();
        else image = pathImage;

        if(!TextUtils.isEmpty(remark.getText().toString().trim())) rem = remark.getText().toString();
        if(!TextUtils.isEmpty(reward.getText().toString().trim())) rewardDes = reward.getText().toString();
        LostFound lostFound = new LostFound(id, AppSingleton.getInstance().getPlayerId(), items,loc,con,rem,rewardDes, uID, image, null, time);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(lostFound.getId().startsWith("F"))
        {
            mDatabase.child("Posting").child("individual").child(uID).child("founds").child(id).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("Posting").child("founds").child(id).setValue(lostFound);
                    if(pathImage == null) {
                        loadingDialog.dismiss();
                        Toast.makeText(EditPostActivity.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(lostFound.getId().startsWith("L"))
        {
            mDatabase.child("Posting").child("individual").child(uID).child("losts").child(id).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("Posting").child("losts").child(id).setValue(lostFound);
                    if(pathImage == null) {
                        loadingDialog.dismiss();
                        Toast.makeText(EditPostActivity.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void verifyUpdate()
    {
        if(TextUtils.isEmpty(item.getText().toString().trim()) || TextUtils.isEmpty(location.getText().toString().trim()) || TextUtils.isEmpty(contact.getText().toString().trim()))
        {
            Toast.makeText(this, "Please enter the above information", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(count == 1 && TextUtils.isEmpty(reward.getText().toString().trim()))
            {
                Toast.makeText(this, "Please enter the reward desciption", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(mUploadTask == null || !mUploadTask.isInProgress()) {
                    uploadImage(uri, lostFound, pathImage);
                }
                if(mUploadTask != null)
                    mUploadTask.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            updateUser();
                            loadingDialog.dismiss();
                            Toast.makeText(EditPostActivity.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                else
                {
                    updateUser();
                    //Toast.makeText(EditPostActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void startCropImageActivity() {
        CropImage.activity()
                .start(this);
    }
    public void setImage(CropImage.ActivityResult result){
        try {
            File tempFile    = new File(result.getUri().getPath());
            Log.d("test",result.getUri().getPath().toString());
            if(tempFile.exists()){
                picture.setImageURI(result.getUri());
                uri = result.getUri();
                pathImage = uri.getLastPathSegment(); //+ System.currentTimeMillis();
                //extension = getFileExtension(uri);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            //CropImage.startPickImageActivity(this);
            startCropImageActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);

            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                defaultpic.setVisibility(View.GONE);
                setImage(result);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //upload image to Firebase Storage
    public void uploadImage(Uri uri, LostFound lostFound, String imagePath) {

        storageReference = FirebaseStorage.getInstance().getReference();
        String p = lostFound.getImage();
        if(uri != null)
        {
            ref = storageReference.child("posting/").child(imagePath);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if(p != null) {
                                ref = storageReference.child("posting/").child(p);
                                ref.delete();
                            }
                            Log.d("uplaod", "onSuccess: " + taskSnapshot.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
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
            count = 1;
        }
        else if(v == staryellow)
        {
            star.setVisibility(View.VISIBLE);
            staryellow.setVisibility(View.GONE);
            des.setVisibility(View.GONE);
            reward.setVisibility(View.GONE);
            reward.setText(null);
            count = 0;
        }
        else if(v == save)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(save.getWindowToken(), 0);
            verifyUpdate();
        }
        else if(v == back)
        {
            finish();
        }
        else if(v == picture)
        {
            onSelectImageClick(picture);
        }
        else if(v == defaultpic)
        {
            onSelectImageClick(picture);
        }
    }
}
