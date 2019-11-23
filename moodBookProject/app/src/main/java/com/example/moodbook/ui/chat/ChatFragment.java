package com.example.moodbook.ui.chat;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatFragment extends PageFragment {
    private ViewAdapter viewAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // get root view from PageFragment
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_chat);

        return root;
    }


}
