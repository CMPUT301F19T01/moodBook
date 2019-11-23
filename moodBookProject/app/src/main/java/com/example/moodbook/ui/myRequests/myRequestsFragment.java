package com.example.moodbook.ui.myRequests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class myRequestsFragment extends PageFragment {
    private RequestsAdapter requestsAdapter;
    private CoordinatorLayout requestListLayout;
    private ListView requestListView;
    private static final String TAG = myRequestsFragment.class.getSimpleName();

    // connect to DB
    private RequestHandler requestDB;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);
        db = FirebaseFirestore.getInstance();

        requestListLayout = root.findViewById(R.id.request_layout);
        requestListView = root.findViewById(R.id.request_listView);
        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        requestsAdapter=  new RequestsAdapter(getContext(), new ArrayList<RequestUser>());
        requestDB = new RequestHandler(mAuth, getContext(),
                RequestHandler.requestListener(requestsAdapter), TAG);
        requestListView.setAdapter(requestsAdapter);
        return root;
    }

}