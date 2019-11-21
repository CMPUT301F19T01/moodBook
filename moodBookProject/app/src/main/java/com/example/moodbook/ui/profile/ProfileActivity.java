package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
//        edit_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showImageChooser();
//
//            }
//        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                saveUserInformation();
            }
        });
        changeProfilePic();

    }

    private void saveUserInformation() {
    }
//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CHOOSE_IMAGE && requestCode == RESULT_OK && data != null && data.getData()!=null){
//            uriProfileImage = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
//                dp.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ProfileEditor.getImageResult(requestCode, resultCode, data, dp, this);
//
//        }
//    }
    private void uploadImageToFirebaseStorage(){
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+ intent_name+".jpg");
        if(uriProfileImage != null){
            image_progress.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            image_progress.setVisibility(View.GONE);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!urlTask.isSuccessful());
                            Uri downloadUrl =urlTask.getResult();
                            profileImageUrl = urlTask.getResult().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            image_progress.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }
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
    }
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"),CHOOSE_IMAGE);
    }

    @Override
    public void setProfilePic(Bitmap bitImage) {
        this.bitImage = bitImage;
    }
}
