package com.example.moodbook.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.moodbook.R;


public class UsernameFragment extends DialogFragment {
    private EditText usernameEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_username, null);
        usernameEditText = view.findViewById(R.id.usernameEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Set username")
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();
    }

}
