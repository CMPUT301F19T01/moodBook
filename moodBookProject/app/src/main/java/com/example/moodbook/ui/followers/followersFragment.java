package com.example.moodbook.ui.followers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.Request.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class followersFragment extends PageFragment {

    // connect to DB
    private RequestHandler requestDB;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_followers);
        db = FirebaseFirestore.getInstance();

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();

        return root;
    }
}