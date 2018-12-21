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

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.R;

import java.util.List;

public class searchResultAdapter extends RecyclerView.Adapter<searchResultAdapter.ViewHolder> {
    private List<LostFound> searchLists;
    private Context context;
    public openDetail openDetail;

    public searchResultAdapter(List<LostFound> searchLists, Context context)
    {
        this.searchLists = searchLists;
        this.context = context;
    }

    @NonNull
    @Override
    public searchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_layout, parent, false);
        return new searchResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchResultAdapter.ViewHolder holder, int position) {
        LostFound lostFound = searchLists.get(position);

        if(lostFound.getItem().startsWith("F")) holder.item.setText("Found : " + lostFound.getItem());
        else if(lostFound.getItem().startsWith("L")) holder.item.setText("Lost : " + lostFound.getItem());
        holder.location.setText("Location : " + lostFound.getLocation());
        holder.contact.setText("Contact : " + lostFound.getContactNum());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetail.onOpenDetail(searchLists.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchLists.size();
    }

    public interface openDetail
    {
        public void onOpenDetail(LostFound lostFound);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView defaultpic,picture;
        TextView item, location, contact;
        CardView container;

        public ViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            item = itemView.findViewById(R.id.found);
            location = itemView.findViewById(R.id.location);
            contact = itemView.findViewById(R.id.contact);
            picture = itemView.findViewById(R.id.picture);
            defaultpic = itemView.findViewById(R.id.defaultpic);
        }
    }
}
