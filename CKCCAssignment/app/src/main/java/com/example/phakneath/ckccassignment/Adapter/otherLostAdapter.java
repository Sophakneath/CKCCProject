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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class otherLostAdapter extends RecyclerView.Adapter<otherLostAdapter.ViewHolder> {

    private Context context;
    private List<LostFound> lostFounds;
    public openDetail openDetail;
    private List<SaveLostFound> saves = new ArrayList<>();
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    int count = 0;
    foundListAdapter foundListAdapter;

    public otherLostAdapter(Context context, List<LostFound> lostFounds)
    {
        this.context = context;
        this.lostFounds = lostFounds;
    }

    public void setSaves(List<SaveLostFound> saves)
    {
        this.saves = saves;
    }


    @NonNull
    @Override
    public otherLostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.found_list_layout, parent, false);
        return new otherLostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull otherLostAdapter.ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        foundListAdapter = new foundListAdapter();

        LostFound lostFound = lostFounds.get(position);
        holder.found.setText("Lost : " + lostFound.getItem());
        holder.location.setText("Location : " + lostFound.getLocation());
        holder.contact.setText("Contact : " + lostFound.getContactNum());
        if(lostFound.getReward()!= null) holder.star.setVisibility(View.VISIBLE);
        holder.more.setVisibility(View.GONE);
        if(lostFound.getImage() != null){ foundListAdapter.getImage(holder.imagefound, lostFound.getImage(), context); holder.defaultpic.setVisibility(View.GONE); }


        SaveLostFound saveLostFound = new SaveLostFound();
        saveLostFound.setId(lostFound.getId());
        saveLostFound.setMyOwnerID(lostFound.getMyOwner());
        getSaves(holder.onSave, holder.notsave, saveLostFound);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDetail.onOpenDetailLostPost(lostFounds.get(position));
            }
        });

        /*holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0)
                {
                    count = 1;
                    onSave(saveLostFound, holder.onSave,holder.notsave);
                }
                else if(count == 1)
                {
                    count = 0;
                    onUnSave(saveLostFound, holder.onSave, holder.notsave);
                }
            }
        });*/

        holder.onSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnSave(saveLostFound, holder.onSave, holder.notsave);
            }
        });

        holder.notsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(saveLostFound, holder.onSave,holder.notsave);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lostFounds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView more,notsave, onSave;
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
        public void onOpenDetailLostPost(LostFound lostFound);
    }

    public void onSave(SaveLostFound lostFound, ImageView a, ImageView b)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uid).child("save").child(lostFound.getId()).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                a.setVisibility(View.VISIBLE);
                b.setVisibility(View.GONE);
                Toast.makeText(context, "Save Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUnSave(SaveLostFound lostFound, ImageView a, ImageView b)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uid).child("save").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                a.setVisibility(View.GONE);
                b.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Unsave Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSaves(ImageView a, ImageView b, SaveLostFound lf)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Posting").child("individual").child(uid).child("save");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<SaveLostFound> savess = new ArrayList<>();
                SaveLostFound lostFound = new SaveLostFound();

                for(DataSnapshot d: dataSnapshot.getChildren())
                {
                    lostFound = d.getValue(SaveLostFound.class);
                    savess.add(lostFound);
                }

                saves = savess;

                if(saves.size() >0)
                    for (SaveLostFound l: saves) {
                        if(l.getId().equals(lf.getId()))
                        {
                            a.setVisibility(View.VISIBLE);
                            b.setVisibility(View.GONE);
                            count = 1;
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
