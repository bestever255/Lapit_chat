package com.best.karem.lapitchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName, profileStatus, profileTotalFriends;
    private Button sendFriendRequestBtn, declineFriendreqBtn;


    private DatabaseReference databaseReference;
    private DatabaseReference friendreqDatabase;
    private DatabaseReference friendsDatabase;
    private DatabaseReference notificationDatabase;
    private DatabaseReference userDatabase;
    private FirebaseAuth auth;
    private String current_user;

    private String user_id;
    private String current_state = "not_friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        user_id = getIntent().getStringExtra("user_id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendreqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);


        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileStatus = findViewById(R.id.profile_status);
        sendFriendRequestBtn = findViewById(R.id.profile_send_friend_request_btn);
        declineFriendreqBtn = findViewById(R.id.profile_decline_friend_request_btn);
        profileTotalFriends = findViewById(R.id.profile_total_friends);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile).into(profileImage);

                friendreqDatabase.child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                current_state = "req_received";
                                sendFriendRequestBtn.setText("Accept Friend request");

                            } else if (req_type.equals("sent")) {
                                current_state = "req_sent";
                                sendFriendRequestBtn.setText("Cancel Friend Request");
                            } else {

                                friendreqDatabase.child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(user_id)) {

                                            current_state = "friends";
                                            sendFriendRequestBtn.setText("Unfriend this person");

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFriendRequestBtn.setEnabled(false);

                if (current_state.equals("not_friends")) {

                    friendreqDatabase.child(current_user).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {

                                friendreqDatabase.child(user_id).child(current_user).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Map<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", current_user);
                                        notificationData.put("type", "request");

                                        notificationDatabase.child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    sendFriendRequestBtn.setEnabled(true);
                                                    current_state = "request_sent";
                                                    sendFriendRequestBtn.setText("Cancel Friend Request");

                                                    Toast.makeText(ProfileActivity.this, "Request Sent successfully", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    // Not Friends

                } else if (current_state.equals("req_sent")) {

                    friendreqDatabase.child(current_user).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                friendreqDatabase.child(user_id).child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            sendFriendRequestBtn.setEnabled(true);
                                            sendFriendRequestBtn.setText("Send Friend Request");
                                            current_state = "not_friends";

                                            Toast.makeText(ProfileActivity.this, "Friend request removed", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    // Accept Friend Reqeust

                } else if (current_state.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendsDatabase.child(current_user).child(user_id).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            friendreqDatabase.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendreqDatabase.child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()) {

                                                sendFriendRequestBtn.setEnabled(true);
                                                current_state = "friends";
                                                sendFriendRequestBtn.setText("Unfriend this person");

                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Failed to accept friend request", Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });
                                }
                            });

                        }
                    });


                } else if (current_state.equals("friends")) {

                    friendsDatabase.child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                sendFriendRequestBtn.setEnabled(true);
                                current_state = "not_friends";
                                sendFriendRequestBtn.setText("Send Friend Request");

                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        userDatabase.child("online").setValue(true);

    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//
//        userDatabase.child("online").setValue(false);
//
//    }
}
