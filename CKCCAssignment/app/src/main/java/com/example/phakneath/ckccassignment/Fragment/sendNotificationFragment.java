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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.receiveNotificationAdapter;
import com.example.phakneath.ckccassignment.Adapter.sendNotificationAdapter;
import com.example.phakneath.ckccassignment.Model.Notification;
import com.example.phakneath.ckccassignment.NotificationDetailActivity;
import com.example.phakneath.ckccassignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class sendNotificationFragment extends Fragment implements receiveNotificationAdapter.onOpenDetailNotification{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView notificationContainer;
    TextView noPost;
    ProgressBar progress;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uID;
    com.example.phakneath.ckccassignment.Adapter.sendNotificationAdapter sendNotificationAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public sendNotificationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static sendNotificationFragment newInstance(String param1, String param2) {
        sendNotificationFragment fragment = new sendNotificationFragment();
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
        uID = mAuth.getCurrentUser().getUid();
        View view = inflater.inflate(R.layout.fragment_send_notification, container, false);
        notificationContainer = view.findViewById(R.id.listNotification);
        progress = view.findViewById(R.id.progress);
        noPost = view.findViewById(R.id.noPost);
        getNotification();
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


    public void setAdapter(List<Notification> notifications)
    {
        progress.setVisibility(View.GONE);
        if(notifications.size() <= 0) noPost.setVisibility(View.VISIBLE);
        else {
            noPost.setVisibility(View.GONE);
            Collections.reverse(notifications);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notificationContainer.setLayoutManager(layoutManager);
        sendNotificationAdapter = new sendNotificationAdapter(getContext(), notifications);
        notificationContainer.setAdapter(sendNotificationAdapter);
        sendNotificationAdapter.onOpenDetailNotification = this::onClickNotification;
    }

    public void getNotification()
    {
        progress.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query myTopPostsQuery = mDatabase.child("Posting").child("individual").child(uID).child("notification").child("send").orderByChild("time");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Notification> notifications = new ArrayList<>();
                Notification notification = new Notification();
                for(DataSnapshot d: dataSnapshot.getChildren())
                {
                    notification = d.getValue(Notification.class);
                    notifications.add(notification);
                }
                setAdapter(notifications);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClickNotification(Notification notification) {
        Intent intent = new Intent(getContext(), NotificationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("notification", notification);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
