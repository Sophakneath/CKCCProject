package com.example.phakneath.ckccassignment.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Model.Notification;
import com.example.phakneath.ckccassignment.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {

    List<Notification> notifications;
    Context context;
    DatabaseReference mDatabase;

    public notificationAdapter(Context context, List<Notification> notifications)
    {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false);
        return new notificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        getUsename(notification, holder.description);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profile;
        CardView container;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            profile = itemView.findViewById(R.id.profile);
            description = itemView.findViewById(R.id.description);
        }
    }

    public void getUsename(Notification notification, TextView description)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(notification.getPostOwnerID()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sh;
                sh = dataSnapshot.getValue(String.class) + " has found your item at " + notification.getLocation();
                description.setText(sh);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
