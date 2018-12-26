package com.example.phakneath.ckccassignment.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Model.Notification;
import com.example.phakneath.ckccassignment.Model.User;
import com.example.phakneath.ckccassignment.PostingActivity;
import com.example.phakneath.ckccassignment.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class receiveNotificationAdapter extends RecyclerView.Adapter<receiveNotificationAdapter.ViewHolder> {

    List<Notification> notifications;
    Context context;
    DatabaseReference mDatabase;
    PostingActivity postingActivity = new PostingActivity();

    public receiveNotificationAdapter(Context context, List<Notification> notifications)
    {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false);
        return new receiveNotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        getUsename(notification, holder.description, holder.profile);
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

    public void getUsename(Notification notification, TextView description, CircleImageView profile)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(notification.getPostOwnerID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String sh = "";
                if(notification.getPostID().startsWith("F")) sh = user.getUsername() + " has lost this item at " + notification.getLocation();
                else if(notification.getPostID().startsWith("L")) sh = user.getUsername() + " has found your item at " + notification.getLocation();
                description.setText(sh);
                postingActivity.getImage(profile,user.getImagePath(),context);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
