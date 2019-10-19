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


import com.example.moodbook.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This creates a fragment that allows the user to set a username
 * Citation: https://stackoverflow.com/questions/16918854/find-fragment-by-tag-name-in-container  Ken Wolf - used to determine where the fragment was created
 * https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile Used to update username
 */


public class UsernameFragment extends DialogFragment {
    private EditText usernameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_username, null);
        usernameEditText = view.findViewById(R.id.usernameEditText);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Determine if registering or updating
        Fragment fragment = getFragmentManager().findFragmentByTag("registering");
        if ("registering" == fragment.getTag()) {
            final ArrayList<String> usernameList = ((LoginActivity) getActivity()).usernameList;
            return builder
                    .setView(view)
                    .setTitle("Set username")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String username = usernameEditText.getText().toString();
                            Log.d("EmailUsernameTesting", Arrays.toString(usernameList.toArray()));

                            if (usernameList.contains(username)) {
                                Log.w("Email", "username exists:" + username);
                            } else {
                                // register the user with firebase
                                ((LoginActivity) getActivity()).register(
                                        ((LoginActivity) getActivity()).email.getText().toString(),
                                        ((LoginActivity) getActivity()).password.getText().toString(), username);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

        // Not registering an account
        } else { //TODO updating username. only implement if client requests this feature
            return builder
                    .setView(view)
                    .setTitle("Set username")
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("Cancel", null)
                    .create();
        }
    }
}