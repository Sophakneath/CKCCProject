package com.example.phakneath.ckccassignment.Fragment;

import android.animation.TimeAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.LostFoundActivity;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.PostingActivity;
import com.example.phakneath.ckccassignment.R;
import com.example.phakneath.ckccassignment.sharePreferences.AppSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ViewLostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText lost, location, contact, remark, reward;
    CropImageView image;
    ImageView defaultpic, star, staryellow;
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
    Button post;
    int count;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ViewLostFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ViewLostFragment newInstance(String param1, String param2) {
        ViewLostFragment fragment = new ViewLostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_lost, container, false);
        lost = view.findViewById(R.id.lost);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        remark = view.findViewById(R.id.Remark);
        image = view.findViewById(R.id.image);
        defaultpic = view.findViewById(R.id.defaultpic);
        scrollView = view.findViewById(R.id.hello);
        foundtxt = view.findViewById(R.id.foundtxt);
        post = view.findViewById(R.id.post);
        reward = view.findViewById(R.id.rewardDes);
        star = view.findViewById(R.id.star);
        staryellow = view.findViewById(R.id.staryellow);
        des = view.findViewById(R.id.des);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

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
        return view;
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

    public void updateUser()
    {
        String item = lost.getText().toString();
        String loc = location.getText().toString();
        String con = contact.getText().toString();
        String rem = null;
        String id = "L" + uID + System.currentTimeMillis();
        String rewardDes = null;

        if(!TextUtils.isEmpty(remark.getText())) rem = remark.getText().toString();
        if(!TextUtils.isEmpty(reward.getText())) rewardDes = reward.getText().toString();
        LostFound lostFound = new LostFound(id, AppSingleton.getInstance().getPlayerId(), item,loc,con,rem,rewardDes, uID, null, null, System.currentTimeMillis());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uID).child("losts").child(id).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("Posting").child("losts").child(id).setValue(lostFound);
                Toast.makeText(getContext(), "Post Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verifyUpdate()
    {
        if(TextUtils.isEmpty(lost.getText()) || TextUtils.isEmpty(location.getText()) || TextUtils.isEmpty(contact.getText()))
        {
            Toast.makeText(getContext(), "Please enter the above information", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(count == 1 && TextUtils.isEmpty(reward.getText()))
            {
                Toast.makeText(getContext(), "Please enter the reward desciption", Toast.LENGTH_SHORT).show();
            }
            else {
                updateUser();
                LostFoundActivity.activity.finish();
                Toast.makeText(getContext(), "Post Successfull", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
