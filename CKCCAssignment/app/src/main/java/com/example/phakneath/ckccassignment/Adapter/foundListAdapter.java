package com.example.phakneath.ckccassignment.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class foundListAdapter extends RecyclerView.Adapter<foundListAdapter.ViewHolder> {

    private Context context;
    private List<LostFound> lostFounds;

    public foundListAdapter(Context context, List<LostFound> lostFounds)
    {
        this.context = context;
        this.lostFounds = lostFounds;
    }

    @NonNull
    @Override
    public foundListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.found_list_layout, parent, false);
        return new foundListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull foundListAdapter.ViewHolder holder, int position) {
        LostFound lostFound = lostFounds.get(position);
        holder.name.setText(lostFound.getItem());
    }

    @Override
    public int getItemCount() {
        return lostFounds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.items);
        }
    }

}
