package com.example.phakneath.ckccassignment.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.foundListAdapter;
import com.example.phakneath.ckccassignment.Adapter.lostListAdapter;
import com.example.phakneath.ckccassignment.Adapter.myLostAdapter;
import com.example.phakneath.ckccassignment.DetailActivity;
import com.example.phakneath.ckccassignment.EditPostActivity;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.example.phakneath.ckccassignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link myLostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link myLostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myLostFragment extends Fragment implements myLostAdapter.openDetail, myLostAdapter.editPost, foundListAdapter.deletePosts{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView lostList;
    List<LostFound> lostFounds;
    myLostAdapter myLostAdapter;
    DatabaseReference mDatabase;
    User user;
    FirebaseAuth mAuth;
    String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public myLostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostLostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static myLostFragment newInstance(String param1, String param2) {
        myLostFragment fragment = new myLostFragment();
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

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        View view = inflater.inflate(R.layout.fragment_post_lost, container, false);
        lostList = view.findViewById(R.id.container);

        getUser();
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

    public void setAdapter(List<LostFound> lostFounds, User user, List<LostFound> saves)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lostList.setLayoutManager(layoutManager);
        myLostAdapter = new myLostAdapter(getContext(), lostFounds, user, saves);
        lostList.setAdapter(myLostAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onOpenDetailLost(LostFound lostFound, User user) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        bundle.putSerializable("getLostFound", lostFound);
        bundle1.putSerializable("user", user);
        intent.putExtras(bundle);
        intent.putExtras(bundle1);
        startActivity(intent);
    }

    public void getUser()
    {
        user = new User();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("user").child("id").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tusername= dataSnapshot.child("username").getValue(String.class);
                String timagepath = dataSnapshot.child("imagePath").getValue(String.class);
                String textension = dataSnapshot.child("extension").getValue(String.class);
                String tphoneNum = dataSnapshot.child("phoneNum").getValue(String.class);
                String temail = dataSnapshot.child("email").getValue(String.class);

                List<LostFound> tlosts = new ArrayList<>();
                List<LostFound> tfounds = new ArrayList<>();
                List<LostFound> saves = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.child("losts").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tlosts.add(lostFound);
                }
                for (DataSnapshot d: dataSnapshot.child("founds").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    tfounds.add(lostFound);
                }
                for(DataSnapshot d: dataSnapshot.child("save").getChildren())
                {
                    lostFound = d.getValue(LostFound.class);
                    saves.add(lostFound);
                }

                user.setUsername(tusername);
                user.setImagePath(timagepath);
                user.setExtension(textension);
                user.setPhoneNum(tphoneNum);
                user.setEmail(temail);
                user.setLosts(tlosts);
                user.setFounds(tfounds);
                lostFounds = update(user);
                setAdapter(lostFounds, user, saves);
                myLostAdapter.openDetail = myLostFragment.this::onOpenDetailLost;
                myLostAdapter.editPost = myLostFragment.this::onEditPost;
                myLostAdapter.deletePosts = myLostFragment.this::onDeletePosts;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<LostFound> update(User user)
    {
        List<LostFound> lostFounds = new ArrayList<>();
        for (LostFound l:user.getLosts()) {
            lostFounds.add(l);
        }
        return lostFounds;
    }

    @Override
    public void onEditPost(LostFound lostFound) {
        Intent intent = new Intent(getContext(), EditPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", lostFound);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void delete(LostFound lostFound)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uid).child("losts").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("Posting").child("losts").child(lostFound.getId()).removeValue();
                mDatabase.child("user").child("id").child(uid).child("save").child(lostFound.getId()).removeValue();
                Toast.makeText(getContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDeletePosts(LostFound lostFound) {
        delete(lostFound);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
