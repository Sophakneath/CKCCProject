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
 * {@link PostDiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostDiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDiscoverFragment extends Fragment implements foundListAdapter.openDetail, myLostAdapter.editPost, foundListAdapter.deletePosts{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView foundList;
    List<LostFound> lostFounds;
    foundListAdapter foundListAdapter;
    User user;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostDiscoverFragment() {
        // Required empty public constructor
    }

    public static PostDiscoverFragment newInstance(String param1, String param2) {
        PostDiscoverFragment fragment = new PostDiscoverFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_found, container, false);
        foundList = view.findViewById(R.id.container);

        /*lostFounds = new ArrayList<>();
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Bag", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));

        setAdapter();*/
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAdapter(List<LostFound> lostFounds)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        foundList.setLayoutManager(layoutManager);
        foundListAdapter = new foundListAdapter(getContext(), lostFounds, uid);
        foundList.setAdapter(foundListAdapter);
        //getSaves();
    }

    @Override
    public void onOpenDetailFoundPost(LostFound lostFound) {
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> allFoundPosts = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.child("founds").getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    allFoundPosts.add(lostFound);
                }

                setAdapter(allFoundPosts);
                foundListAdapter.openDetail = PostDiscoverFragment.this::onOpenDetailFoundPost;
                foundListAdapter.editPost = PostDiscoverFragment.this::onEditPost;
                foundListAdapter.deletePosts = PostDiscoverFragment.this::onDeletePosts;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        mDatabase.child("user").child("id").child(uid).child("founds").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("Posting").child("founds").child(lostFound.getId()).removeValue();
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
