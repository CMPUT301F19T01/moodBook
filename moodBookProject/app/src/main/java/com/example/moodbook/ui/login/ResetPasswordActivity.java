package com.example.moodbook.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity allows the user to reset their account password
 */
public class ResetPasswordActivity extends AppCompatActivity {


    private Button ResetPasswordButton;
    private EditText ResetEmailInput;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);



        ResetPasswordButton = findViewById(R.id.ok_button);
        ResetEmailInput = findViewById(R.id.type_email);

        mAuth = FirebaseAuth.getInstance();

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = ResetEmailInput.getText().toString();
                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPasswordActivity.this, "You have not entered a valid email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Check your email to Reset your password",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this,  message,Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }

            }
        });
    }
}
