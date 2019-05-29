package com.best.karem.lapitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.widget.EmojiAppCompatEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangeStatusActivity extends AppCompatActivity implements View.OnTouchListener {

    private MaterialEditText statusEd;
    private Button confirmBtn;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        findViewById(R.id.mainLayout).setOnTouchListener(this);


        auth = FirebaseAuth.getInstance();
        String current_user = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);

        statusEd = findViewById(R.id.change_status_ed);
        confirmBtn = findViewById(R.id.change_status_confirm_btn);

        progressBar = findViewById(R.id.change_status_progressBar);


        Bundle bundle = getIntent().getExtras();
        String resultText = bundle.getString("status", "");

        statusEd.setText(resultText);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.INVISIBLE);

                databaseReference.child("status").setValue(statusEd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if(task.isSuccessful()){
                            Toast.makeText(ChangeStatusActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                            Intent settingIntent = new Intent(ChangeStatusActivity.this , SettingActivity.class);
                            settingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(settingIntent);
                            finish();
                        }else{
                            Toast.makeText(ChangeStatusActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                        confirmBtn.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        return true;
    }
}
