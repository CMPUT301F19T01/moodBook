package com.example.moodbook.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodEditor;
import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * This activity handles registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 */

public class RegisterActivity extends AppCompatActivity implements ProfileEditor.ProfilePicInterface {

    private FirebaseAuth mAuth;

    protected DBAuth dbAuth;
    private UsernameList usernameList;


    private Button registerButton;
    protected EditText email;
    private EditText username;
    protected EditText password;
    private FloatingActionButton edit_profile_pic;
    private ImageView profile_pic;
    private Bitmap bitImage;
    private StorageReference photoReference;
    private static FirebaseStorage storage;
    private Context context;
    private ProfilePicSetter DBpic;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        dbAuth = new DBAuth(mAuth, FirebaseFirestore.getInstance());

        usernameList = new UsernameList(FirebaseFirestore.getInstance());
        usernameList.updateUsernameList(); // fetch the usernamelist now so it is ready by the time the user clicks register

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        edit_profile_pic = findViewById(R.id.edit_profile_pic_button);
        storage = FirebaseStorage.getInstance();
        photoReference = storage.getReferenceFromUrl("gs://moodbook-60da3.appspot.com");
        DBpic = new ProfilePicSetter(getApplicationContext());
        changeProfilePic();


        // Register is not not modularized because FireBase calls are asynchronous. Since they are asynchronous, we can't depend on results returned from methods until the onCompleteListener knows that the task is finished
        // REGISTER button
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String emailS = email.getText().toString();
                final String passwordS = password.getText().toString();
                final String usernameS = username.getText().toString();



                if (dbAuth.verifyEmail(emailS)){
                    if (dbAuth.verifyPass(passwordS)){
                        if (usernameList.verifyUsername(usernameS)){
                            // all fields are good

                            mAuth.createUserWithEmailAndPassword(emailS, passwordS)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                dbAuth.createUser(user, emailS, usernameS);
                                                dbAuth.updateUsername(user, usernameS);
                                                Intent intent = new Intent();
                                                setResult(Activity.RESULT_OK, intent);
                                                finish();

                                            } else {
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                email.setError("Email in use"); // Firebase call fails when email is in use
                                            }
                                        }
                                    });
                        } else {
                            username.setError("Username in use");
                        }
                    } else {
                        password.setError("Password must be >= 6 chars");
                    }
                } else {
                    email.setError("Incorrect email format");
                }
            }
        });


    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        username = findViewById(R.id.username);
        ProfileEditor.getImageResult(requestCode, resultCode, data, profile_pic, this);
//        ProfileEditor.uploadImage(username.toString(),this,data);
    }
    /**
     * This allows a user to edit a mood image.
     */
    private void changeProfilePic() {
        profile_pic = findViewById(R.id.profile_pic);

        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProfileEditor.setImage(RegisterActivity.this);
            }
        });
    }

    /**
     * This is a method inherited from the MoodEditor Interface that sets a value for a bitmap Image
     * @param bitImage
     *  This is a bitmap image
     * This override MoodEditor.MoodInterface setMoodReasonPhoto(),
     * and is setter for bitImage
     * @param bitImage
     *  This is current reason photo of mood event
     */
    @Override
    public void setProfilePic(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

}



