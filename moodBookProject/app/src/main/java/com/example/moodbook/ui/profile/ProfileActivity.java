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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.example.moodbook.MainActivity;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity implements ProfileEditor.ProfilePicInterface{
    ImageView dp;
    TextView name;
    TextView email;
    FloatingActionButton edit_image;
    private static final int CHOOSE_IMAGE =101;
    String intent_name;
    String intent_email;
    ProgressBar image_progress;
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
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        intent_name = getIntent().getStringExtra("name");
        intent_email = getIntent().getStringExtra("email");
        dp = (ImageView) findViewById(R.id.profile_pic);
        edit_image = (FloatingActionButton) findViewById(R.id.edit_profile_pic_button);
        name.setText("Username: " + intent_name);
        email.setText("Email: " + intent_email);
        image_progress = (ProgressBar) findViewById(R.id.image_progress);
        save_profile =(Button) findViewById(R.id.save_profile);
        showImg(intent_name);
        changeProfilePic();
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                saveUserInformation();
                  addImg(intent_name);
                  finishActivity(1);
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
        ProfileEditor.getImageResult(requestCode, resultCode, data, dp, this);
        uriProfileImage = data.getData();
    }


    @Override
    public void setProfilePic(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

    public void addImg(String username) {
        fireRef = mStorageRef.child("profilepics/" + username + ".jpeg" );
        Bitmap bitImage = ProfileEditor.getBitmap();
        Uri file = uriProfileImage;
        if (file != null){
            UploadTask uploadTask = fireRef.putFile(file);
            Log.e("Fire Path", fireRef.toString());

        }
    }
    public void showImg(String username){
        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilepics/" + username + ".jpeg" ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

}
