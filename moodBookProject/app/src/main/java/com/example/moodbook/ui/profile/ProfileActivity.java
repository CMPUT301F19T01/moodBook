package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.example.moodbook.MainActivity;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileActivity extends AppCompatActivity implements ProfileEditor.ProfilePicInterface{
    ImageView dp;
    TextView name;
    TextView email;
    EditText phone;
    EditText bio;
    FloatingActionButton edit_image;
    private static final int CHOOSE_IMAGE =101;
    String intent_name;
    String intent_email;
    Button save_profile;
    private Bitmap bitImage;

    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;

    StorageReference fireRef;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        bio = findViewById(R.id.bio);
        intent_name = getIntent().getStringExtra("name");
        showImg(intent_name);
        intent_email = getIntent().getStringExtra("email");
        dp = (ImageView) findViewById(R.id.profile_pic);
        edit_image = (FloatingActionButton) findViewById(R.id.edit_profile_pic_button);

        name.setText(intent_name);
        email.setText(intent_email);
        save_profile =(Button) findViewById(R.id.save_profile);
        changeProfilePic();
        getData();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  addImg(intent_name);
                  updateProfile(userID, email.getText().toString(), name.getText().toString(), phone.getText().toString(),bio.getText().toString());
                  finishActivity(0);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * This allows a user to edit a mood image.
     */
    private void changeProfilePic() {
        dp = findViewById(R.id.profile_pic);

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProfileEditor.setImage(ProfileActivity.this);

            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            ProfileEditor.getImageResult(requestCode, resultCode, data, dp, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uriProfileImage = data.getData();
    }


    @Override
    public void setProfilePic(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

    public void addImg(String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fireRef = mStorageRef.child("profilepics/" + username + ".jpeg" );
        Bitmap bitImage = ProfileEditor.getBitmap();
        Uri file = uriProfileImage;
        if (file != null){
            UploadTask uploadTask = fireRef.putFile(file);
            Log.e("Fire Path", fireRef.toString());

        }else if (bitImage != null) {
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = fireRef.putBytes(data);
        }
    }
    public void showImg(String username){
        // Reference to an image file in Cloud Storage
        mStorageRef.child("profilepics/" + username + ".jpeg" ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()/* context */)
                        .load(uri)
                        .centerCrop()
                        .into(dp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                dp.setImageResource(R.drawable.purpleprofile);
            }
        });
    }
    public void getData(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        DocumentReference docRef = collectionReference.document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DocumentSnapshot data: ", "DocumentSnapshot data: " + document.get("phone"));
                        phone.setText((CharSequence) document.get("phone"));
                        bio.setText((CharSequence) document.get("bio"));
                        showImg((String) document.get("username"));

                    } else {
                        Log.d("No such document", "No such document");
                    }
                } else {
                    Log.d("get failed with ", "get failed with ", task.getException());
                }
            }
        });

    }

    public void updateProfile(String UserID, String email, String username, String phone, String bio){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("username", username);
        data.put("phone",phone);
        data.put("bio", bio);

        collectionReference
                .document(UserID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "uid stored in db");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "failed:" + e.toString());
                    }
                });


    }





}
