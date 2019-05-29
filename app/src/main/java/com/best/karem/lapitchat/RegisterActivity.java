package com.best.karem.lapitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    private MaterialEditText emailEd  , passwordEd;
    private Button registerBtn;
    private TextView goToLogin;
    private ProgressBar progressBar;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEd = findViewById(R.id.register_email);
        passwordEd  = findViewById(R.id.register_password);
        registerBtn = findViewById(R.id.register_btn);
        goToLogin = findViewById(R.id.register_login_txt);

        progressBar = findViewById(R.id.register_progressBar);

        auth = FirebaseAuth.getInstance();


        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginIntent = new Intent(RegisterActivity.this , LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();

            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEd.getText().toString();
                String password = passwordEd.getText().toString();

                RegisterUser(email , password);

            }
        });



    }

    private void RegisterUser(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);
        registerBtn.setVisibility(View.INVISIBLE);

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();

        }else{

            auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent setupIntent = new Intent(RegisterActivity.this , SetupActivity.class);
                        startActivity(setupIntent);

                    }else{
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    registerBtn.setVisibility(View.VISIBLE);

                }

            });

        }

    }
}
