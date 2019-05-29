package com.best.karem.lapitchat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.best.karem.lapitchat.Model.Users;
import com.best.karem.lapitchat.ViewHolders.AllUsersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView usersList;

    private FirebaseAuth auth;
    private DatabaseReference allUsers;
    private DatabaseReference userDatabase;


    private FirebaseRecyclerAdapter<Users , AllUsersViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        auth = FirebaseAuth.getInstance();


        allUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        allUsers.keepSynced(true);


        toolbar = findViewById(R.id.all_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        usersList = findViewById(R.id.all_user_recyclerView);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        String current_user = auth.getCurrentUser().getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
        userDatabase.child("online").setValue(true);


        adapter = new FirebaseRecyclerAdapter<Users, AllUsersViewHolder>(

                Users.class,
                R.layout.all_users_item,
                AllUsersViewHolder.class,
                allUsers
        ) {
            @Override
            protected void populateViewHolder(final AllUsersViewHolder viewHolder, final Users model, int position) {

                viewHolder.userName.setText(model.getName());
                viewHolder.userStatus.setText(model.getStatus());

                // Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(viewHolder.userImage);
                Picasso.get().load(model.getThumb_image()).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(model.getThumb_image()).placeholder(R.drawable.profile).into(viewHolder.userImage);

                    }
                });

                final String user_id = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(AllUsersActivity.this , ProfileActivity.class);
                        profileIntent.putExtra("user_id" , user_id);
                        startActivity(profileIntent);


                    }
                });

                viewHolder.userImage.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));


                viewHolder.container.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));




            }
        };

        usersList.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        userDatabase.child("online").setValue(false);
//    }
}
