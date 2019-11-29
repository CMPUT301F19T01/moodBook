package com.example.moodbook.ui.profile;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.moodbook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class diplays Activity to view and edits users profile
 * @see ProfileViewActivity
 * @see ProfileEditor
 */
public class EditProfileActivity extends AppCompatActivity
        implements ProfileEditor.ProfilePicInterface, ProfileEditor.ProfileListener{
    ImageView dp;
    TextView name;
    TextView email;
    EditText phone;
    EditText bio;
    FloatingActionButton edit_image;
    String intent_name;
    String intent_email;
    Button save_profile;
    Button close_profile;
    private Bitmap bitImage;

    Uri uriProfileImage;

    StorageReference fireRef;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        bio = findViewById(R.id.bio);
        intent_name = getIntent().getStringExtra("username");
        showImg(intent_name);
        intent_email = getIntent().getStringExtra("email");
        dp = findViewById(R.id.profile_pic);
        edit_image = findViewById(R.id.edit_profile_pic_button);

        name.setText(intent_name);
        email.setText(intent_email);
        save_profile = findViewById(R.id.save_profile);
        close_profile = findViewById(R.id.close_profile);
        changeProfilePic();

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfileEditor.getProfileData(userID,this);

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  addImg(intent_name);

                  ProfileEditor.updateProfile(userID, email.getText().toString(),
                          name.getText().toString(), phone.getText().toString(),
                          bio.getText().toString());

                finish();

            }
        });
        close_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                ProfileEditor.setImage(EditProfileActivity.this);
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

    /**
     * This method sets a profile image
     * @param bitImage
     */
    @Override
    public void setProfilePic(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

    /**
     * This method displays profile data that was obtained from database
     * @param document
     */
    @Override
    public void onGettingUserDoc(DocumentSnapshot document) {
        phone.setText((CharSequence) document.get("phone"));
        bio.setText((CharSequence) document.get("bio"));
        showImg((String) document.get("username"));
    }

    /**
     * inherited by ProfileEditor
     * @param document
     */
    @Override
    public void onGettingMoodDoc(DocumentSnapshot document) {
        // Do nothing
    }

    /**
     * This method adds image to FireBase Storage
     * @param username
     */
    public void addImg(String username) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        fireRef = mStorageRef.child("profilepics/" + username + ".jpeg" );
        Bitmap bitImage = ProfileEditor.getBitmap();
        Uri file = uriProfileImage;
        if (file != null){
            fireRef.putFile(file);
            Log.e("Fire Path", fireRef.toString());

        }else if (bitImage != null) {
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            fireRef.putBytes(data);
        }
    }

    /**
     * This method displays the profile picture
     * @param username
     *  string username of a user
     */
    public void showImg(String username){
        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilepics/" + username + ".jpeg" )
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()/* context */)
                        .load(uri)
                        .centerCrop()
                        .into(dp);
            }
        }) ;
    }


}
