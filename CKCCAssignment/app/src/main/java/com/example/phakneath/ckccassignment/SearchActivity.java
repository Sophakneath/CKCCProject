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
import com.example.phakneath.ckccassignment.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, openDetail{

    SearchView searchView;
    CircleImageView back;
    RecyclerView resultFounds, resultLosts, resultUsers;
    private List<LostFound> searchLists;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uID;
    ImageView foundTab, lostTab, usersTab;
    RelativeLayout foundCon, lostCon, usersCon;
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
        usersCon.setOnClickListener(this::onClick);

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

                layoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
                resultLosts.setLayoutManager(layoutManager);
                firebaseSearchLost(newText);

                layoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
                resultUsers.setLayoutManager(layoutManager);
                firebaseSearchUsers(newText);

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
        resultUsers = findViewById(R.id.containerUsers);
        usersCon = findViewById(R.id.usersCon);
        usersTab = findViewById(R.id.usersTab);
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
            foundTab.setVisibility(View.VISIBLE);
            resultLosts.setVisibility(View.GONE);
            lostTab.setVisibility(View.GONE);
            resultUsers.setVisibility(View.GONE);
            usersTab.setVisibility(View.GONE);

            count = 1;
        }
        else if(v == lostCon)
        {
            resultFounds.setVisibility(View.GONE);
            foundTab.setVisibility(View.GONE);
            resultLosts.setVisibility(View.VISIBLE);
            lostTab.setVisibility(View.VISIBLE);
            resultUsers.setVisibility(View.GONE);
            usersTab.setVisibility(View.GONE);
            count = 0;
        }
        else if(v == usersCon)
        {
            resultFounds.setVisibility(View.GONE);
            foundTab.setVisibility(View.GONE);
            resultLosts.setVisibility(View.GONE);
            lostTab.setVisibility(View.GONE);
            resultUsers.setVisibility(View.VISIBLE);
            usersTab.setVisibility(View.VISIBLE);
            count = 2;
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

    private void firebaseSearchUsers(String newText)
    {
        if(!newText.isEmpty()) {
            try {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
                Query query = mDatabase.orderByChild("username").startAt(newText).endAt(newText + "\uf8ff");
                FirebaseRecyclerOptions<User> options =
                        new FirebaseRecyclerOptions.Builder<User>()
                                .setQuery(query, User.class)
                                .setLifecycleOwner(this)
                                .build();

                FirebaseRecyclerAdapter<User, ViewHolderUsers> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, ViewHolderUsers>(options) {

                    @NonNull
                    @Override
                    public ViewHolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_users_layout, parent, false);
                        return new ViewHolderUsers(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolderUsers holder, int position, @NonNull User model) {
                        User user = model;

                        holder.item.setText(user.getUsername());

                        holder.container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openDetailUsers(user);
                            }
                        });
                    }
                };
                resultUsers.setAdapter(firebaseRecyclerAdapter);
            }catch(Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            resultUsers.setAdapter(null);
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

    public static class ViewHolderUsers extends RecyclerView.ViewHolder
    {
        CircleImageView picture;
        TextView item;
        CardView container;

        public ViewHolderUsers(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            item = itemView.findViewById(R.id.found);
            picture = itemView.findViewById(R.id.picture);
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

    public void openDetailUsers(User user)
    {
        if(user.getId().equals(uID))
        {
            myOwnProfile();
            //Toast.makeText(this, "my profile" + "\n" + user.getId() + "\n" + uID, Toast.LENGTH_SHORT).show();
        }
        else if(!user.getId().equals(uID))
        {
            otherProfile(user);
            //Toast.makeText(this, "other profile" + "\n" + user.getId() + "\n" + uID, Toast.LENGTH_SHORT).show();
        }
    }

    public void myOwnProfile()
    {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void otherProfile(User user)
    {
        Intent intent = new Intent(this, OtherProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("otherUser", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
