package com.best.karem.lapitchat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.best.karem.lapitchat.Adapters.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private ViewPager viewPager;
    private Toolbar toolbar;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private DatabaseReference usersDatbase;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        String current_user = auth.getCurrentUser().getUid();



        usersDatbase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lapit Chat");

        tabLayout = findViewById(R.id.main_tab);

        viewPager = findViewById(R.id.main_viewPager);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            SendToStart();

        }else{

            usersDatbase.child("online").setValue(true);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            usersDatbase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void SendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);



        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.menu_all_users){
            sendToAllUsers();
        }else if(item.getItemId() == R.id.menu_account_setting){
            GoToSetting();
        }else if(item.getItemId() == R.id.menu_log_out){
            Logout();
        }

        return super.onOptionsItemSelected(item);


    }

    private void sendToAllUsers() {

        Intent allUsersIntent = new Intent(MainActivity.this , AllUsersActivity.class);
        startActivity(allUsersIntent);

    }

    private void GoToSetting() {

        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingIntent);

    }

    private void Logout() {

        auth.signOut();

        Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }
}
