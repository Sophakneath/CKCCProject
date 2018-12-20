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

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class mySaveAdapter extends RecyclerView.Adapter<mySaveAdapter.ViewHolder> {

    private Context context;
    private List<LostFound> saves;
    public openDetail openDetail;
    int count = 1;
    String uid;
    DatabaseReference mDatabase;

    public mySaveAdapter(Context context, List<LostFound> saves, String uid)
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
        LostFound lostFound = saves.get(position);
        if(lostFound.getId().startsWith("F")) holder.found.setText("Found : " + lostFound.getItem());
        else  if(lostFound.getId().startsWith("L")) holder.found.setText("Lost : " + lostFound.getItem());

        holder.more.setVisibility(View.GONE);
        holder.location.setText("Location : " + lostFound.getLocation());
        holder.contact.setText("Contact : " + lostFound.getContactNum());
        if(lostFound.getReward()!= null) holder.star.setVisibility(View.VISIBLE);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetail.onOpenDetail(saves.get(position));
            }
        });

        holder.onSave.setVisibility(View.VISIBLE);
        holder.notsave.setVisibility(View.GONE);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnSave(saves.get(position));
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
        public void onOpenDetail(LostFound lostFound);
    }

    public void onUnSave(LostFound lostFound)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uid).child("save").child(lostFound.getId()).removeValue();
    }

}
