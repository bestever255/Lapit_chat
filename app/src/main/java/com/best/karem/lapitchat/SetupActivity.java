package com.best.karem.lapitchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_CODE = 1;

    private CircleImageView profileImage;
    private MaterialEditText nameEd;
    private Button confirmBtn;

    private FirebaseAuth auth;

    private Uri resultUri;
    private StorageReference storageReference;

    private DatabaseReference users;

    private String current_user;

    private ProgressBar progressBar;
    private byte[] thumb_byte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        users = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
        users.keepSynced(true);


        profileImage = findViewById(R.id.setup_image);
        nameEd = findViewById(R.id.setup_name);
        confirmBtn = findViewById(R.id.setup_confirm_btn);

        progressBar = findViewById(R.id.setup_progressBar);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.INVISIBLE);

                final String name = nameEd.getText().toString();

                if (TextUtils.isEmpty(name)) {

                    progressBar.setVisibility(View.INVISIBLE);
                    confirmBtn.setVisibility(View.VISIBLE);

                    Toast.makeText(SetupActivity.this, "please Enter your name", Toast.LENGTH_SHORT).show();
                } else {

                    final File thumb_filePath = new File(resultUri.getPath());

                    try {
                        Bitmap thumb_bitmap = new Compressor(SetupActivity.this)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(50)
                                .compressToBitmap(thumb_filePath);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        thumb_byte = baos.toByteArray();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    StorageReference image_path = storageReference.child("profile_images").child(current_user + ".jpg");
                    final StorageReference thumb_file = storageReference.child("profile_images").child("thumbs").child(current_user + ".jpg");

                    image_path.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                final String download_uri = task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask = thumb_file.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        if (thumb_task.isSuccessful()) {


                                            Map<String, String> userMap = new HashMap<>();
                                            userMap.put("name", name);
                                            userMap.put("status", "Hello there I am using lapit chat app");
                                            userMap.put("image", download_uri);
                                            userMap.put("thumb_image", thumb_downloadUrl);
                                            userMap.put("device_token", deviceToken);

                                            users.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        confirmBtn.setVisibility(View.VISIBLE);


                                                        Toast.makeText(SetupActivity.this, "data saved!", Toast.LENGTH_SHORT).show();

                                                        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(mainIntent);
                                                        finish();

                                                    } else {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        confirmBtn.setVisibility(View.VISIBLE);

                                                        Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });


                                        } else {
                                            Toast.makeText(SetupActivity.this, thumb_task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            confirmBtn.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });


                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                confirmBtn.setVisibility(View.VISIBLE);

                                Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check for permission

                // if android version more thena marshmellow
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // check or permission if not granted
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // request permission
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                    } else {
                        // permission granted
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(SetupActivity.this);
                    }
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                profileImage.setImageURI(resultUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
