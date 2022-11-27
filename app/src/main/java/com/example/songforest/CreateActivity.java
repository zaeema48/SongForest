package com.example.songforest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateActivity extends AppCompatActivity {

    ImageView profile;
    EditText name;
    android.widget.Button save_profile;
    ProgressBar progressbar;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    Uri selectedImageUri;   //for image link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        profile = findViewById(R.id.imageView3);
        name = findViewById(R.id.entername);
        save_profile = findViewById(R.id.profileButton);
        progressbar = findViewById(R.id.progressBar2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");  //will fetch all the images from the phone
                startActivityForResult(intent, 45); //will create image uri (WILL CALL THE METHOD ActivityResult)
            }

        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(CreateActivity.this, "Enter your Name!!", Toast.LENGTH_SHORT).show();
                }
                progressbar.setVisibility(View.VISIBLE);

                StorageReference imageReference = firebaseStorage.getReference().child("Profiles").child(firebaseAuth.getUid());
                imageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image_uri = uri.toString();
                                    String Uid = firebaseAuth.getUid();
                                    String phone_no = firebaseAuth.getCurrentUser().getPhoneNumber();
                                    User user = new User(username, phone_no, Uid, image_uri);

                                    firebaseDatabase.getReference().child("Users").child(Uid)
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                    Intent intent = new Intent(CreateActivity.this, HomePage.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){
            profile.setImageURI(data.getData());
            selectedImageUri=data.getData();
        }
    }
}


