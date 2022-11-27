package com.example.songforest;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    ImageView imageView;
    TextView name;
    android.widget.Button update_profile, logout;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;

    String image_token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, null);
        imageView=view.findViewById(R.id.profileimg);
        name=view.findViewById(R.id.entername);
        update_profile=view.findViewById(R.id.updateprofile);
        logout=view.findViewById(R.id.logout);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        //to fetch the image from the firebase storage

        StorageReference imageStorage= firebaseStorage.getReference();
        imageStorage.child("Profiles").child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                image_token= uri.toString();    //stores the img link in string format;
                Picasso.get().load(uri).into(imageView);    //picasso is lib to load img from firebase
            }
        });
        //to fetch the username from realtime database
        DatabaseReference nameReference = firebaseDatabase.getReference();
        nameReference.child("Users").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  //snapshot is whole user data; name, ph no, img link etc
                User user= snapshot.getValue(User.class);   //stores the snapshot user to local user
                name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to fetch the data", Toast.LENGTH_SHORT).show();
            }
        });

        //Update profile working
        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateActivity.class);
                startActivity(intent);
            }
        });

        //Logout working
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
                firebaseAuth.signOut();

                Intent intent = new Intent (getContext(), MainActivity.class );
                startActivity(intent);
                getActivity().finish();
            }
        });



        return view;

    }
}
