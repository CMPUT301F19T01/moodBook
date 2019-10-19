package com.example.moodbook.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.moodbook.MainActivity;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * This creates a fragment that allows the user to set a username
 * Citation: https://stackoverflow.com/questions/16918854/find-fragment-by-tag-name-in-container  Ken Wolf - used to determine where the fragment was created
 * https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile Used to update username
 */


public class UsernameFragment extends DialogFragment {
    private EditText usernameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_username, null);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Determine if registering or updating
        Fragment fragment = getFragmentManager().findFragmentByTag("registering");
        if ("registering" == fragment.getTag()){
            return builder
                    .setView(view)
                    .setTitle("Set username")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //TODO verify username unique

                            // register the user with firebase
                            ((LoginActivity) getActivity()).register(
                                    ((LoginActivity) getActivity()).email.getText().toString(),
                                    ((LoginActivity) getActivity()).password.getText().toString(), usernameEditText.getText().toString());

                            //updateUsername(usernameEditText.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

        } else { //TODO updating username. only implement if client requests this feature
            return builder
                    .setView(view)
                    .setTitle("Set username")
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("Cancel", null)
                    .create();
        }
    }

    /*/**
     * This updates the firebase user profile with a username
     * @param username

    private void updateUsername(String username){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("Email", "User profile updated");
                            }
                        }
                    });

        }

        Log.d("EMAIL", "USERNAME:" + user.getDisplayName());
    }*/

}
