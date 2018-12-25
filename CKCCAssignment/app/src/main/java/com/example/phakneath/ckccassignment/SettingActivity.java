package com.example.phakneath.ckccassignment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.ckccassignment.Dialog.LoadingDialog;
import com.example.phakneath.ckccassignment.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView back, profile;
    EditText username, phoneNum;
    RelativeLayout email;
    TextView save, myEmail;
    User user;
    PostingActivity postingActivity = new PostingActivity();
    private String pathImage, extension;
    Uri uri;
    DatabaseReference mDatabase;
    public static final int OPEN_GALLERY = 1;
    static StorageTask mUploadTask;
    StorageReference ref;
    StorageReference storageReference;
    String uID;
    FirebaseAuth mAuth;
    LoadingDialog dialog = new LoadingDialog();
    public static Activity activity;
    Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
        email.setOnClickListener(this::onClick);
        profile.setOnClickListener(this::onClick);
        getUser();
        activity = this;
    }

    public void initView()
    {
        back = findViewById(R.id.back);
        profile = findViewById(R.id.profilePic);
        username = findViewById(R.id.username);
        phoneNum = findViewById(R.id.phoneNum);
        email = findViewById(R.id.email);
        save = findViewById(R.id.setting);
        myEmail = findViewById(R.id.myEmail);
    }
    public void getUser()
    {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        updateUI(user);
    }

    public void updateUI(User user)
    {
        username.setText(user.getUsername());
        phoneNum.setText(user.getPhoneNum());
        myEmail.setText(user.getEmail());
        if(user.getImagePath() != null)
        {
            postingActivity.getImage(profile, user.getImagePath(), this);
        }
        username.setSelection(username.getText().length());
        phoneNum.setSelection(phoneNum.getText().length());
    }

    //permission for open gallery
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
                profile.setImageURI(result.getUri());
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
                setImage(result);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    //upload image to Firebase Storage
    public void uploadImage(Uri uri, User user, String imagePath) {

        storageReference = FirebaseStorage.getInstance().getReference();
        String p = user.getImagePath() + "." + user.getExtension();
        if(uri != null)
        {
            ref = storageReference.child("profile/").child( imagePath);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref = storageReference.child("profile/").child(p);
                            ref.delete();
                            Log.d("uplaod", "onSuccess: " + taskSnapshot.toString());


                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("user").child(uID).child("imagePath").setValue(imagePath);
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

    public void updateInformationUser(String uID, String imagePath, User user)
    {
        if(mUploadTask == null || !mUploadTask.isInProgress()) {
            uploadImage(uri, user, imagePath);
        }
        if(mUploadTask != null)
        mUploadTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                editUsers(uID, user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        else editUsers(uID, user);
    }

    public void editUsers(String uID, User user)
    {
        //Toast.makeText(this, user.getUsername() + user.getGender() + user.getPhone() , Toast.LENGTH_SHORT).show();
        if(!username.getText().toString().equals(user.getUsername()) || !phoneNum.getText().toString().equals(user.getPhoneNum()))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("user").child(uID).child("username").setValue(username.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("user").child(uID).child("phoneNum").setValue(phoneNum.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(uri != null || !username.getText().toString().equals(user.getUsername()) || !phoneNum.getText().toString().equals(user.getPhoneNum())) Toast.makeText(SettingActivity.this, " Changed Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
            });
        }
        else
        {
            dialog.dismiss();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
       if(v == back)
       {
           finish();
       }
       else if(v == save)
       {
           InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
           imm.hideSoftInputFromWindow(save.getWindowToken(), 0);
           dialog.show(getFragmentManager(), "dialogLoading");
           updateInformationUser(uID, pathImage, user);
       }
       else if(v == email)
       {
           startActivity(new Intent(this, EditEmailActivity.class));
       }
       else if(v == profile)
       {
           onSelectImageClick(profile);
       }
    }
}
