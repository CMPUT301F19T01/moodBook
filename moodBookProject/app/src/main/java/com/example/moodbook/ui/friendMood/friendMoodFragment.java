package com.example.moodbook.ui.friendMood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.example.moodbook.ui.myRequests.RequestUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.moodbook.ui.myRequests.*;

import java.util.ArrayList;

public class friendMoodFragment extends PageFragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView acceptedText;
    private ListView acceptedListView;

    private ArrayList<RequestUser> requestList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_friendmood);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        acceptedListView = root.findViewById(R.id.accepted_listView);
//        acceptedText = root.findViewById(R.id.acceptedFriendText);
//        final RequestUser user =  requestList.get(position);
//        acceptedText.setText(user.getUsername());
//        FriendMoodViewModel =
//                ViewModelProviders.of(this).get(friendMoodViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_friendMood);
//        FriendMoodViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }
}
