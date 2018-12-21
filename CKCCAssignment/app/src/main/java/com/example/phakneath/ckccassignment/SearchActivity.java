package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v7.widget.SearchView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Adapter.searchResultAdapter;
import com.example.phakneath.ckccassignment.Adapter.searchResultAdapter.openDetail;
import com.example.phakneath.ckccassignment.Model.LostFound;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, openDetail{

    SearchView searchView;
    CircleImageView back;
    RecyclerView resultFounds, resultLosts;
    private List<LostFound> searchLists;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uID;
    ImageView foundTab, lostTab;
    RelativeLayout foundCon, lostCon;
    int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_search);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        back.setOnClickListener(this::onClick);
        foundCon.setOnClickListener(this::onClick);
        lostCon.setOnClickListener(this::onClick);

        searchView.setFocusable(true);
        searchView.setQueryHint("Search items ... ");
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
                resultFounds.setLayoutManager(layoutManager);
                firebaseSearchFound(newText);

                LinearLayoutManager layoutManager1 = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
                resultLosts.setLayoutManager(layoutManager1);
                firebaseSearchLost(newText);

                return true;
            }
        });
    }

    private void initView() {
        searchView = findViewById(R.id.search);
        back = findViewById(R.id.back);
        resultFounds = findViewById(R.id.containerFound);
        foundTab = findViewById(R.id.foundTab);
        lostTab = findViewById(R.id.lostTab);
        foundCon = findViewById(R.id.foundCon);
        lostCon = findViewById(R.id.lostCon);
        resultLosts = findViewById(R.id.containerLost);
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            finish();
        }
        else if(v == foundCon)
        {
            resultFounds.setVisibility(View.VISIBLE);
            resultLosts.setVisibility(View.GONE);
            foundTab.setVisibility(View.VISIBLE);
            lostTab.setVisibility(View.GONE);
            count = 1;
        }
        else if(v == lostCon)
        {
            resultFounds.setVisibility(View.GONE);
            resultLosts.setVisibility(View.VISIBLE);
            foundTab.setVisibility(View.GONE);
            lostTab.setVisibility(View.VISIBLE);
            count = 0;
        }
    }

    //search from firebase
    private void firebaseSearchFound(String newText)
    {
        if(!newText.isEmpty()) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Posting").child("founds");
            Query query = mDatabase.orderByChild("item").startAt(newText).endAt(newText + "\uf8ff");
            FirebaseRecyclerOptions<LostFound> options =
                    new FirebaseRecyclerOptions.Builder<LostFound>()
                            .setQuery(query, LostFound.class)
                            .setLifecycleOwner(this)
                            .build();

            FirebaseRecyclerAdapter<LostFound, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LostFound, ViewHolder>(options) {

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_result_layout, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull LostFound model) {
                    LostFound lostFound = model;

                    holder.item.setText("Found : " + lostFound.getItem());
                    holder.location.setText("Location : " + lostFound.getLocation());
                    holder.contact.setText("Contact : " + lostFound.getContactNum());

                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDetail(lostFound);
                        }
                    });
                }
            };
            resultFounds.setAdapter(firebaseRecyclerAdapter);
        }
        else
        {
            resultFounds.setAdapter(null);
        }

    }

    private void firebaseSearchLost(String newText)
    {
        if(!newText.isEmpty()) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Posting").child("losts");
            Query query = mDatabase.orderByChild("item").startAt(newText).endAt(newText + "\uf8ff");
            FirebaseRecyclerOptions<LostFound> options =
                    new FirebaseRecyclerOptions.Builder<LostFound>()
                            .setQuery(query, LostFound.class)
                            .setLifecycleOwner(this)
                            .build();

            FirebaseRecyclerAdapter<LostFound, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LostFound, ViewHolder>(options) {

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_result_layout, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull LostFound model) {
                    LostFound lostFound = model;

                    holder.item.setText("Lost : " + lostFound.getItem());
                    holder.location.setText("Location : " + lostFound.getLocation());
                    holder.contact.setText("Contact : " + lostFound.getContactNum());

                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDetail(lostFound);
                        }
                    });
                }
            };
            resultLosts.setAdapter(firebaseRecyclerAdapter);
        }
        else
        {
            resultLosts.setAdapter(null);
        }

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

    @Override
    public void onOpenDetail(LostFound lostFound) {

    }

    public void openDetail(LostFound lostFound)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        bundle.putSerializable("getLostFound", lostFound);
        bundle1.putSerializable("user", null);
        intent.putExtras(bundle);
        intent.putExtras(bundle1);
        startActivity(intent);
    }
}
