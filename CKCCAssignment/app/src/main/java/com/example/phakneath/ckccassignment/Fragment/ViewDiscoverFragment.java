package com.example.phakneath.ckccassignment.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.system.ErrnoException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.ckccassignment.Dialog.LoadingDialog;
import com.example.phakneath.ckccassignment.LostFoundActivity;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.example.phakneath.ckccassignment.PostingActivity;
import com.example.phakneath.ckccassignment.R;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ViewDiscoverFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public EditText found, location, contact, remark, reward;
    ImageView image;
    ImageView defaultpic, star, staryellow;;
    private String pathImage, extension;
    private Uri mCropImageUri;
    Uri uri;
    DatabaseReference mDatabase;
    public static final int OPEN_GALLERY = 1;
    static StorageTask mUploadTask;
    StorageReference ref;
    StorageReference storageReference;
    String uID;
    FirebaseAuth mAuth;
    ScrollView scrollView;
    TextView foundtxt, des;
    LinearLayout rewardContainer;
    Button post;
    int count;
    Boolean isFoundFragment = true;
    LoadingDialog loadingDialog;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ViewDiscoverFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static ViewDiscoverFragment newInstance(String param1, String param2,Boolean isFoundFragment) {
        ViewDiscoverFragment fragment = new ViewDiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3,isFoundFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            isFoundFragment = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_discover, container, false);
        found = view.findViewById(R.id.found);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        remark = view.findViewById(R.id.Remark);
        image = view.findViewById(R.id.image);
        defaultpic = view.findViewById(R.id.defaultpic);
        scrollView = view.findViewById(R.id.hello);
        foundtxt = view.findViewById(R.id.foundtxt);
        post = view.findViewById(R.id.post);

        reward = view.findViewById(R.id.frewardDes);
        star = view.findViewById(R.id.fstar);
        staryellow = view.findViewById(R.id.fstaryellow);
        des = view.findViewById(R.id.fdes);
        rewardContainer = view.findViewById(R.id.frewardContainer);
        setType(isFoundFragment);
        loadingDialog = new LoadingDialog();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onLoadImageClick();
                onSelectImageClick(image);

            }
        });

        foundtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onCropImageClick();

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(post.getWindowToken(), 0);
                verifyUpdate();
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staryellow.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                des.setVisibility(View.VISIBLE);
                reward.setVisibility(View.VISIBLE);
                count = 1;
            }
        });

        staryellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staryellow.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                des.setVisibility(View.GONE);
                reward.setVisibility(View.GONE);
                count = 0;
            }
        });


        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        return view;
    }

    public void setType(Boolean isFoundFragment){
        if (isFoundFragment){
            rewardContainer.setVisibility(View.GONE);
            this.isFoundFragment = true;
            staryellow.setVisibility(View.GONE);
            star.setVisibility(View.GONE);
            des.setVisibility(View.GONE);
            reward.setVisibility(View.GONE);

        }else {
            rewardContainer.setVisibility(View.VISIBLE);
            this.isFoundFragment = true;
            staryellow.setVisibility(View.GONE);
            star.setVisibility(View.VISIBLE);
            des.setVisibility(View.GONE);
            reward.setVisibility(View.GONE);
            this.isFoundFragment = false;
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void startCropImageActivity() {
        CropImage.activity()
                .start(getActivity());
    }
    public void setImage(CropImage.ActivityResult result){
        try {
            File tempFile    = new File(result.getUri().getPath());
            Log.d("test",result.getUri().getPath().toString());
            if(tempFile.exists()){
              image.setImageURI(result.getUri());
              defaultpic.setVisibility(View.GONE);
              uri = result.getUri();
              pathImage = uri.getLastPathSegment(); //+ System.currentTimeMillis();
              //extension = getFileExtension(uri);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(getContext())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            //CropImage.startPickImageActivity(getActivity());
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
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(getActivity());
    }

    //permission for open gallery
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void postLost()
    {
        loadingDialog.show(getActivity().getFragmentManager(), "postDiscover");
        String item = found.getText().toString();
        String loc = location.getText().toString();
        String con = contact.getText().toString();
        String rem = null;
        String id = "L" + uID + System.currentTimeMillis();
        String rewardDes = null;

        if(!TextUtils.isEmpty(remark.getText())) rem = remark.getText().toString();
        if(!TextUtils.isEmpty(reward.getText())) rewardDes = reward.getText().toString();
        LostFound lostFound = new LostFound(id, item,loc,con,rem,rewardDes, uID, pathImage, null, System.currentTimeMillis());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("losts").child(id).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("Posting").child("losts").child(id).setValue(lostFound);
                if(pathImage == null) {
                    loadingDialog.dismiss();
                    LostFoundActivity.activity.finish();
                    Toast.makeText(getContext(), "Post Successfull", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateUser()
    {
        loadingDialog.show(getActivity().getFragmentManager(), "postDiscover");
        String item = found.getText().toString();
        String loc = location.getText().toString();
        String con = contact.getText().toString();
        String rem = remark.getText().toString();
        String id = "F" + uID + System.currentTimeMillis();
        LostFound lostFound = new LostFound(id, item,loc,con,rem,null, uID, pathImage, null, System.currentTimeMillis());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("founds").child(id).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("Posting").child("founds").child(id).setValue(lostFound);
                if(pathImage == null) {
                    loadingDialog.dismiss();
                    LostFoundActivity.activity.finish();
                    Toast.makeText(getContext(), "Post Successfull", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getBaseContext(), "Post Unsuccessfull", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //upload image to Firebase Storage
    public void uploadImage(Uri uri, String imagePath, String extension) {

        storageReference = FirebaseStorage.getInstance().getReference();
        if(uri != null)
        {
            ref = storageReference.child("posting/").child(imagePath);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            LostFoundActivity.activity.finish();
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), "Post Successfull", Toast.LENGTH_SHORT).show();
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

    public void verifyUpdate()
    {
        if(TextUtils.isEmpty(found.getText()) || TextUtils.isEmpty(location.getText()) || TextUtils.isEmpty(contact.getText()))
        {
            Toast.makeText(getContext(), "Please enter the above information", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (isFoundFragment){
                updateUser();
                if(pathImage != null)
                if(mUploadTask == null || !mUploadTask.isInProgress())
                    uploadImage(uri,pathImage,extension);
            }else{
                if(count == 1 && TextUtils.isEmpty(reward.getText()))
                {
                    Toast.makeText(getContext(), "Please enter the reward desciption", Toast.LENGTH_SHORT).show();
                }
                else {
                    postLost();
                    if(pathImage != null)
                    if(mUploadTask == null || !mUploadTask.isInProgress())
                        uploadImage(uri,pathImage,extension);
                }
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
