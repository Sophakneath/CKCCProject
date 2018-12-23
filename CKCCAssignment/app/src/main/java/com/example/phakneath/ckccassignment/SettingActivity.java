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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        if(user.getImagePath() != null && user.getExtension() != null)
        {
            postingActivity.getImage(profile, user.getImagePath() + "." + user.getExtension(), this);
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

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Uri.parse(MediaStore.Images.Media.DATA));
        i.setType("image/*");
        startActivityForResult(i,OPEN_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == OPEN_GALLERY)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                openGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_GALLERY)
        {
            if (resultCode == RESULT_OK)
            {
                uri = data.getData();
                Glide.with(this).load(uri).into(profile);
                pathImage = uri.getLastPathSegment() + System.currentTimeMillis();
                extension = getFileExtension(uri);
                Log.e("ooooo", "Path Image : " + pathImage);
            }
        }
    }


    //upload image to Firebase Storage
    public void uploadImage(Uri uri, User user, String imagePath, String extension) {

        storageReference = FirebaseStorage.getInstance().getReference();
        String p = user.getImagePath() + "." + user.getExtension();
        if(uri != null)
        {
            ref = storageReference.child("profile/").child( imagePath + "." + extension);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref = storageReference.child("profile/").child(p);
                            ref.delete();
                            Log.d("uplaod", "onSuccess: " + taskSnapshot.toString());


                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("user").child(uID).child("imagePath").setValue(imagePath);
                            mDatabase.child("user").child(uID).child("extension").setValue(extension);
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

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(SettingActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},OPEN_GALLERY);
        }
        else
        {
            openGallery();
        }
    }

    public void updateInformationUser(String uID, String imagePath, String extension, User user)
    {
        if(mUploadTask == null || !mUploadTask.isInProgress()) {
            uploadImage(uri, user, imagePath, extension);
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
           updateInformationUser(uID, pathImage, extension, user);
       }
       else if(v == email)
       {
           startActivity(new Intent(this, EditEmailActivity.class));
       }
       else if(v == profile)
       {
           gallery();
       }
    }
}
