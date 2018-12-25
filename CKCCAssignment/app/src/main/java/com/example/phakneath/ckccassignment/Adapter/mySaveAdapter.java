package com.example.phakneath.ckccassignment.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
import com.example.phakneath.ckccassignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mySaveAdapter extends RecyclerView.Adapter<mySaveAdapter.ViewHolder> {

    private Context context;
    private List<SaveLostFound> saves;
    public openDetail openDetail;
    int count;
    String uid;
    DatabaseReference mDatabase;
    List<LostFound> lostFounds;
    foundListAdapter foundListAdapter;


    public mySaveAdapter(Context context, List<SaveLostFound> saves, String uid)
    {
        this.context = context;
        this.saves = saves;
        this.uid = uid;
    }

    @NonNull
    @Override
    public mySaveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.found_list_layout, parent, false);
        return new mySaveAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mySaveAdapter.ViewHolder holder, int position) {
        SaveLostFound lostFound = saves.get(position);
        getSaveLostFound(lostFound, holder.found, holder.location, holder.contact, holder.star, holder.container, holder.imagefound, holder.defaultpic);
        holder.more.setVisibility(View.GONE);
        holder.onSave.setVisibility(View.VISIBLE);
        holder.notsave.setVisibility(View.GONE);
        foundListAdapter = new foundListAdapter();

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnSave(lostFound);
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetail.onOpenDetail(lostFound);
            }
        });
    }

    @Override
    public int getItemCount() {
        return saves.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView more, notsave, onSave;
        CardView container;
        ImageView star, imagefound, defaultpic;
        TextView found, location, contact, tsave;
        RelativeLayout save, share;

        public ViewHolder(View itemView) {
            super(itemView);

            more = itemView.findViewById(R.id.more);
            container = itemView.findViewById(R.id.container);
            star = itemView.findViewById(R.id.staryellow);
            found = itemView.findViewById(R.id.found);
            location = itemView.findViewById(R.id.location);
            contact = itemView.findViewById(R.id.contact);
            imagefound = itemView.findViewById(R.id.imageFound);
            defaultpic = itemView.findViewById(R.id.defaultpic);
            save = itemView.findViewById(R.id.save);
            share = itemView.findViewById(R.id.shareTo);
            notsave = itemView.findViewById(R.id.notsave);
            onSave = itemView.findViewById(R.id.onsave);
            tsave = itemView.findViewById(R.id.tsave);
        }
    }

    public interface openDetail
    {
        public void onOpenDetail(SaveLostFound lostFound);
    }

    public void onUnSave(SaveLostFound lostFound)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(uid).child("save").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Unsave Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSaveLostFound(SaveLostFound saveLostFound, TextView found, TextView location, TextView contact, ImageView star, CardView container, ImageView picture, ImageView defaultpic)
    {
        if(saveLostFound.getId().startsWith("F"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(saveLostFound.getMyOwnerID()).child("founds").child(saveLostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostFounds = new ArrayList<>();
                    LostFound lostFound = dataSnapshot.getValue(LostFound.class);
                    lostFounds.add(lostFound);

                    if(lostFound != null) {
                        found.setText("Found : " + lostFound.getItem());
                        location.setText("Location : " + lostFound.getLocation());
                        contact.setText("Contact : " + lostFound.getContactNum());
                        if (lostFound.getReward() != null) star.setVisibility(View.VISIBLE);
                        if(lostFound.getImage() != null){ foundListAdapter.getImage(picture, lostFound.getImage(), context); defaultpic.setVisibility(View.GONE); }
                        //Toast.makeText(context, "not found", Toast.LENGTH_SHORT).show();
                        //count = 1;
                    }
                    else
                    {
                        //Toast.makeText(context, "not found", Toast.LENGTH_SHORT).show();
                        found.setText("Post not found or maybe user have already delete this post.");
                        found.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        location.setVisibility(View.GONE);
                        contact.setVisibility(View.GONE);
                        star.setVisibility(View.GONE);
                        container.setClickable(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(saveLostFound.getId().startsWith("L"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Posting").child("individual").child(saveLostFound.getMyOwnerID()).child("losts").child(saveLostFound.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lostFounds = new ArrayList<>();
                    LostFound lostFound = dataSnapshot.getValue(LostFound.class);
                    lostFounds.add(lostFound);

                    if(lostFound != null) {
                        found.setText("Lost : " + lostFound.getItem());
                        location.setText("Location : " + lostFound.getLocation());
                        contact.setText("Contact : " + lostFound.getContactNum());
                        if (lostFound.getReward() != null) star.setVisibility(View.VISIBLE);
                        if(lostFound.getImage() != null){ foundListAdapter.getImage(picture, lostFound.getImage(), context); defaultpic.setVisibility(View.GONE); }
                        //count = 1;
                    }
                    else
                    {
                        found.setText("Post not found or maybe user have already delete this post.");
                        found.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        location.setVisibility(View.GONE);
                        contact.setVisibility(View.GONE);
                        star.setVisibility(View.GONE);
                        container.setClickable(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
