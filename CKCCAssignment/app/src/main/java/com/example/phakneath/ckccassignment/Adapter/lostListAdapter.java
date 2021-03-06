package com.example.phakneath.ckccassignment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.phakneath.ckccassignment.DetailActivity;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class lostListAdapter extends RecyclerView.Adapter<lostListAdapter.ViewHolder> {

    private Context context;
    private List<LostFound> lostFounds;
    public openDetail openDetail;
    private String uID;
    public myLostAdapter.editPost editPost;
    public foundListAdapter.deletePosts deletePosts;
    private List<SaveLostFound> saves;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uid;
    int count = 0;
    foundListAdapter foundListAdapter;
    public OnShareListioner onShareListioner;

    public void setOnShareListioner(OnShareListioner onShareListioner){
        this.onShareListioner = onShareListioner;
    }

    public lostListAdapter(Context context, List<LostFound> lostFounds, String uID)
    {
        this.context = context;
        this.lostFounds = lostFounds;
        this.uID = uID;
    }

    public void setSaves(List<SaveLostFound> saves)
    {
        this.saves = saves;
    }


    @NonNull
    @Override
    public lostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.found_list_layout, parent, false);
        return new lostListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull lostListAdapter.ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        foundListAdapter = new foundListAdapter();

        LostFound lostFound = lostFounds.get(position);
        holder.found.setText("Lost : " + lostFound.getItem());
        holder.location.setText("Location : " + lostFound.getLocation());
        holder.contact.setText("Contact : " + lostFound.getContactNum());
        if(lostFound.getReward()!= null) holder.star.setVisibility(View.VISIBLE);
        if(lostFound.getMyOwner().equals(uID)) holder.more.setVisibility(View.VISIBLE);

        if(lostFound.getImage() != null){ foundListAdapter.getImage(holder.imagefound, lostFound.getImage(), context); holder.defaultpic.setVisibility(View.GONE); }

        SaveLostFound saveLostFound = new SaveLostFound();
        saveLostFound.setId(lostFound.getId());
        saveLostFound.setMyOwnerID(lostFound.getMyOwner());
        saveLostFound.setTime(lostFound.getTime());
        getSaves(holder.onSave, holder.notsave, saveLostFound);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.inflate(R.menu.more_menu);
                menu.setGravity(Gravity.RIGHT);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                //Toast.makeText(context, "Eidt", Toast.LENGTH_SHORT).show();
                                editPost.onEditPost(lostFounds.get(position));
                                return true;
                            case R.id.delete: deletePosts.onDeletePosts(lostFounds.get(position));
                                return true;
                            default:
                                return false;
                        }

                    }
                });

                menu.show();
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDetail.onOpenDetailLostPost(lostFounds.get(position));
            }
        });

        holder.onSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnSave(saveLostFound, holder.onSave, holder.notsave);
            }
        });

        holder.notsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLostFound.setTime(System.currentTimeMillis());
                onSave(saveLostFound, holder.onSave,holder.notsave);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onShareListioner != null){
                    try {
                        Bitmap b = ((BitmapDrawable) holder.imagefound.getDrawable()).getBitmap();
                        onShareListioner.onShareClick(b,lostFound);
                    }catch (Exception e){

                    }
                }

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
        mDatabase.child("Posting").child("individual").child(uid).child("save").child(lostFound.getId()).setValue(lostFound).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        mDatabase.child("Posting").child("individual").child(uid).child("save").child(lostFound.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        saves = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("Posting").child("individual").child(uid).child("save").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
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
                            //count = 1;
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
