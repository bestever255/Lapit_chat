package com.best.karem.lapitchat;

import android.content.Context;
import android.media.Image;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.best.karem.lapitchat.Adapters.MessageAdapter;
import com.best.karem.lapitchat.Model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatUser;
    private Toolbar toolbar;

    private DatabaseReference rootRef;
    private FirebaseAuth auth;
    private String current_user;

    private TextView titleView;
    private TextView lastSeenView;
    private CircleImageView profileImage;

    private ImageButton chatImageBtn;
    private EditText chatEd;
    private ImageButton chatSendBtn;


    private RecyclerView recyclerView;

    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();


        chatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        String user_image = getIntent().getStringExtra("user_image");
        rootRef = FirebaseDatabase.getInstance().getReference();




        toolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        getSupportActionBar().setCustomView(action_bar_view);

        titleView = findViewById(R.id.custom_bar_title);
        lastSeenView = findViewById(R.id.custom_bar_seen);
        profileImage = findViewById(R.id.custom_bar_image);

        chatImageBtn = findViewById(R.id.chat_add_btn);
        chatEd = findViewById(R.id.chat_message_view);
        chatSendBtn = findViewById(R.id.chat_send_btn);

        adapter = new MessageAdapter(messagesList);

        recyclerView = findViewById(R.id.messages_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        recyclerView.setAdapter(adapter);




        titleView.setText(userName);
        Picasso.get().load(user_image).placeholder(R.drawable.profile).into(profileImage);
//        rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                String online = dataSnapshot.child("online").getValue().toString();
//
////                if(online.equals("true")){
////
////                    lastSeenView.setText("Online");
////
////                }else {
////
////                    long lastTime = Long.parseLong(online);
////                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
////
////                    lastSeenView.setText(lastSeenTime);
////                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        rootRef.child("Chat").child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen" , false);
                    chatAddMap.put("timestamp" , ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + current_user + "/" + chatUser , chatAddMap);
                    chatUserMap.put("Chat/" + chatUser + "/" + current_user , chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ChatActivity.this, databaseError.getMessage().toString() , Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });

    }

    private void loadMessage(){

        rootRef.child("messages").child(current_user).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        String message = chatEd.getText().toString();


        if(TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Enter a message", Toast.LENGTH_SHORT).show();
        }else{

            String current_user_ref = "messages/" + current_user + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + current_user;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(current_user).child(chatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", current_user);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            chatEd.setText("");

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){
                        Toast.makeText(ChatActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }


    }
}
