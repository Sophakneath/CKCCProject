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

import com.example.phakneath.ckccassignment.Adapter.foundListAdapter;
import com.example.phakneath.ckccassignment.Adapter.lostListAdapter;
import com.example.phakneath.ckccassignment.Adapter.myLostAdapter;
import com.example.phakneath.ckccassignment.DetailActivity;
import com.example.phakneath.ckccassignment.EditPostActivity;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.User;
import com.example.phakneath.ckccassignment.R;
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
 * {@link PostLostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostLostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostLostFragment extends Fragment implements lostListAdapter.openDetail, myLostAdapter.editPost, foundListAdapter.deletePosts{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView lostList;
    List<LostFound> lostFounds;
    lostListAdapter lostListAdapter;
    User user;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostLostFragment() {
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
    public static PostLostFragment newInstance(String param1, String param2) {
        PostLostFragment fragment = new PostLostFragment();
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

        /*lostFounds = new ArrayList<>();
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Bag", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));
        lostFounds.add(new LostFound("001", "Phone", "PP", "010", "Hello",null));

        setAdapter();
        lostListAdapter.openDetail = this::onOpenDetailLostPost;*/
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

    public void setAdapter(List<LostFound> lostFounds)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lostList.setLayoutManager(layoutManager);
        lostListAdapter = new lostListAdapter(getContext(), lostFounds, uid);
        lostList.setAdapter(lostListAdapter);
        //getSaves();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onOpenDetailLostPost(LostFound lostFound) {
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
        mDatabase = mDatabase.child("Posting").child("losts");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LostFound> allLostPosts = new ArrayList<>();
                LostFound lostFound = new LostFound();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    lostFound = d.getValue(LostFound.class);
                    allLostPosts.add(lostFound);
                }

                setAdapter(allLostPosts);
                lostListAdapter.openDetail = PostLostFragment.this::onOpenDetailLostPost;
                lostListAdapter.editPost = PostLostFragment.this::onEditPost;
                lostListAdapter.deletePosts = PostLostFragment.this::onDeletePosts;
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
        mDatabase.child("user").child("id").child(uid).child("losts").child(lostFound.getId()).removeValue();
        mDatabase.child("Posting").child("losts").child(lostFound.getId()).removeValue();
    }

    @Override
    public void onDeletePosts(LostFound lostFound) {
        delete(lostFound);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
